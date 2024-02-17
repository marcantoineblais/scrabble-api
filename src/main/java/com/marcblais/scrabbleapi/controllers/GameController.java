package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.services.GameService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.ThreadsRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/grid/new")
    public ResponseEntity<PlayerDTO> createGrid(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GameOption gameOption
    ) {
        Player player = findPlayer(token);
        ResponseEntity<PlayerDTO> responseEntity = playerLoggedIn(player);

        if (responseEntity != null)
            return responseEntity;

        if (player.getGrids().size() > 7)
            return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);

        GridDTO gridDTO = new GridDTO();
        Grid grid;
        gridDTO.buildGrid();
        gridDTO.setBlankTiles(new Integer[][]{});
        gridDTO.setGridType(gameOption.getGridType());
        gridDTO.setLanguage(gameOption.getLanguage());
        gridDTO.setName(gameOption.getName().toUpperCase());
        gridDTO.setPlayerLetters("");
        gridDTO.setPlayer(player);

        grid = gridDTO.toGrid();
        grid.setLastUpdate(LocalDateTime.now());
        player.getGrids().add(grid);
        player.getGrids().sort(Grid::compareTo);

        gameService.saveGrid(grid);
        return new ResponseEntity<>(new PlayerDTO(player), HttpStatus.OK);
    }

    @PostMapping("/grid")
    public ResponseEntity<PlayerDTO> saveGame(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GridDTO gridDTO
    ) {
        Player player = findPlayer(token);
        ResponseEntity<PlayerDTO> responseEntity = playerLoggedIn(player);
        Grid newGrid = gridDTO.toGrid();
        Grid grid;

        if (responseEntity != null)
            return responseEntity;

        grid = player.findGrid(newGrid.getId());
        if (grid != null) {
            grid.setGrid(newGrid.getGrid());
            grid.setPlayerLetters(newGrid.getPlayerLetters());
            grid.setBlankTiles(newGrid.getBlankTiles());
            grid.setLastUpdate(LocalDateTime.now());
            player.getGrids().sort(Grid::compareTo);
            gameService.saveGrid(grid);
            return new ResponseEntity<>(new PlayerDTO(player), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/grid")
    public ResponseEntity<PlayerDTO> deleteGame(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GridDTO gridDTO
    ) {
        if (token == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Player player = findPlayer(token);
        ResponseEntity<PlayerDTO> responseEntity = playerLoggedIn(player);
        Grid grid = gridDTO.toGrid();

        if (responseEntity != null)
            return responseEntity;

        grid = player.findGrid(grid.getId());
        if (grid != null) {
            player.getGrids().remove(grid);
            this.gameService.deleteGrid(grid);
            return new ResponseEntity<>(new PlayerDTO(player), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



    @PostMapping("/grid/solve")
    public ResponseEntity<List<Solution>> findWordsThatFitsOnGrid(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GridDTO grid
    ) {
        Player player = findPlayer(token);
        ResponseEntity<List<Solution>> responseEntity = playerLoggedIn(player);

        if (responseEntity != null)
            return responseEntity;

        // Recuperer la valeur des lettres
        LettersValue lettersValue = gameService.findLettersValueByLanguage(grid.getLanguage());

//        // Recuperer la liste des mots dans la langue de la grille
        Set<DictionaryEntry> entries = gameService.findWordsByLanguage(grid.getLanguage());

        // Creer la liste des lignes de contenu de la grille
        List<GridRowsCols> gridRowsCols = grid.toGridRowsColsList();

        // Creer la liste des patterns pour chaque GridRowsCols
        Map<GridRowsCols, Map<Integer, List<String>>> patternsByGridRowsCols = new HashMap<>();
        gridRowsCols.forEach(g -> patternsByGridRowsCols.put(g, g.testPatterns(grid.getPlayerLetters())));

        // Creer la liste des entries qui sont deja sur la liste
        List<GridEntry> gridEntries = grid.toGridEntriesList();

        // Recuperer tous les patterns uniques
        Set<String> uniquePatterns = new HashSet<>();
        for (Map<Integer, List<String>> map : patternsByGridRowsCols.values()) {
            for (List<String> patternsByIndex : map.values()) {
                Set<String> patternsWithoutBonus = patternsByIndex.stream()
                        .map(p -> p.replaceAll("[0-9]", ".")).collect(Collectors.toSet());
                uniquePatterns.addAll(patternsWithoutBonus);
            }
        }

        // Pour chaque pattern unique, trouver les mots qui sont possibles
        // Creer un nouveau thread pour chaque pattern
        Map<String, Set<DictionaryEntry>> entriesByPattern = new HashMap<>();
        Queue<Thread> entriesByPatternThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForPattern = new ThreadGroup("pattern");
        for (String pattern : uniquePatterns) {
            Thread thread = new Thread(threadGroupForPattern, () -> {
                Set<DictionaryEntry> entriesForPattern = DictionnaryEntriesFinder.findEntriesByPattern(
                        pattern, grid.getPlayerLetters(), entries, ""
                );

                synchronized (entriesByPattern) {
                    entriesByPattern.put(pattern, entriesForPattern);
                }
            });

            entriesByPatternThreads.add(thread);
        }
        try {
            ThreadsRunner.runThreads(entriesByPatternThreads, threadGroupForPattern);
        } catch (Exception ignored) {
        }

        // Assign every list of entries to their matching patterns by grid content
        Map<GridRowsCols, Map<Integer, Set<DictionaryEntry>>> entriesByGridRowsCols = new HashMap<>();
        for (GridRowsCols gridRowCol : patternsByGridRowsCols.keySet()) {
            Map<Integer, Set<DictionaryEntry>> entriesByIndex = new HashMap<>();
            Map<Integer, List<String>> map = patternsByGridRowsCols.get(gridRowCol);

            for (Integer index : map.keySet()) {
                Set<DictionaryEntry> entriesList = new HashSet<>();

                for (String pattern : map.get(index)) {
                    String patternWithoutBonus = pattern.replaceAll("[0-9]", ".");
                    entriesList.addAll(entriesByPattern.get(patternWithoutBonus));
                }

                entriesByIndex.put(index, entriesList);
            }

            entriesByGridRowsCols.put(gridRowCol, entriesByIndex);
        }

        // For every entries in the grid contents, test if the entry is forming another word perpendicular to it.
        // Create a new thread for every dictionary entry in the set
        // Filter the grid content for every gridRowCol and sort them by coordinates
        Queue<Thread> solutionsForGridRowsColsThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForGridRowsCols = new ThreadGroup("gridRowsCols");
        Set<Solution> solutions = new HashSet<>();
        for (GridRowsCols gridRowCol : entriesByGridRowsCols.keySet()) {
            Map<Integer, Set<DictionaryEntry>> map = entriesByGridRowsCols.get(gridRowCol);

            for (Integer index : map.keySet()) {
                for (DictionaryEntry entry : map.get(index)) {
                    Thread thread = new Thread(threadGroupForGridRowsCols, () -> {
                       // CREATE THREAD TO FIND SOLUTIONS
                    });
                }
            }
        }

//        SolutionsFinder solutionsFinder = new SolutionsFinder(grid, entries, gridContents);
//        Set<Solution> solutions = solutionsFinder.toSolutions();
//        PointCalculator pointCalculator = new PointCalculator(grid, solutions, lettersValue);
//        pointCalculator.calculatePoints();
//
//        List<Solution> bestSolutions = pointCalculator.findTopSolutions(10);
//        return new ResponseEntity<>(bestSolutions, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Player findPlayer(String token) {
        String username = PlayerToken.getUsernameFromJwt(token);
        return gameService.findPlayerByUsername(username);
    }

    private <T> ResponseEntity<T> playerLoggedIn(Player player) {
        if (player == null) {
            HttpHeaders headers = new HttpHeaders();
            try {
                headers.setLocation(new URI("/"));
                return new ResponseEntity<T>(headers, HttpStatus.UNAUTHORIZED);
            } catch (Exception ex) {
                return new ResponseEntity<T>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return null;
    }
}
