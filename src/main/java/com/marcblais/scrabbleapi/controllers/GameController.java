package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.services.GameService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
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
        List<GridRowCol> gridRowsCols = grid.toGridRowColList();

        // Creer la liste des patterns pour chaque GridRowsCols
        Map<GridRowCol, Map<Integer, List<String>>> patternsByGridRowsCols = new HashMap<>();
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
                        pattern, grid.getPlayerLetters(), entries
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
        Map<GridRowCol, Map<Integer, Set<DictionaryEntry>>> entriesByGridRowsCols = new HashMap<>();
        for (GridRowCol gridRowCol : patternsByGridRowsCols.keySet()) {
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

        // For every entries in the grid contents, create a solution object and fill their adjacent solution list
        // Create a new thread for every dictionary entry in the set
        Queue<Thread> solutionsBuildingThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForSolutionsBuilding = new ThreadGroup("solutionsBuilding");
        Set<Solution> unfilteredSolutions = new HashSet<>();
        for (GridRowCol gridRowCol : entriesByGridRowsCols.keySet()) {
            Map<Integer, Set<DictionaryEntry>> map = entriesByGridRowsCols.get(gridRowCol);

            for (Integer index : map.keySet()) {
                int y = gridRowCol.isVertical() ? index : gridRowCol.getIndex();
                int x = gridRowCol.isVertical() ? gridRowCol.getIndex() : index;

                for (DictionaryEntry entry : map.get(index)) {
                    // I NEED TO ADD LOGIC HERE TO CREATE SOLUTION WITH BLANK TILES //

                    Thread thread = new Thread(threadGroupForSolutionsBuilding, () -> {
                        Map<Integer, AdjacentSolution> adjacentSolutions = new HashMap<>();

                        for (int i = 0; i < entry.getWord().length(); i++) {
                            int offSetY = gridRowCol.isVertical() ? i : 0;
                            int offSetX = gridRowCol.isVertical() ? 0 : i;

                            GridEntry beforeEntry = gridEntries.stream()
                                    .filter(e -> e.isBefore(y + offSetY, x + offSetX, gridRowCol.isVertical()))
                                    .findFirst()
                                    .orElse(null);

                            GridEntry afterEntry = gridEntries.stream()
                                    .filter(e -> e.isAfter(y + offSetY, x + offSetX, gridRowCol.isVertical()))
                                    .findFirst()
                                    .orElse(null);

                            String beforeString = beforeEntry == null ? "" : beforeEntry.getEntry();
                            String afterString = afterEntry == null ? "" : afterEntry.getEntry();

                            String adjacentWord = beforeString + entry.getWord().charAt(i) + afterString;

                            if (adjacentWord.length() > 1)
                                adjacentSolutions.put(i, new AdjacentSolution(adjacentWord));
                        }

                        Solution solution = new Solution(
                                entry,
                                gridRowCol,
                                adjacentSolutions,
                                gridRowCol.getContent().substring(index, index + entry.getWord().length()),
                                gridRowCol.isVertical(),
                                x,
                                y
                        );

                        synchronized (unfilteredSolutions) {
                            unfilteredSolutions.add(solution);
                        }
                    });

                    solutionsBuildingThreads.add(thread);
                }
            }
        }
        try {
            ThreadsRunner.runThreads(solutionsBuildingThreads, threadGroupForSolutionsBuilding);
        } catch (Exception ignored) {}

        // Create a set of words containing all the adjacent solutions
        Set<String> allAdjacentSolutionsWords = new HashSet<>();
        for (Solution solution : unfilteredSolutions) {
            for (AdjacentSolution adjacentSolution : solution.getAdjacentSolutions().values()) {
                allAdjacentSolutionsWords.add(adjacentSolution.getWord());
            }
        }

        // Test every word in the set and create a set with every valid words
        // Create a new thread for every word to test
        Set<String> allValidAdjacentSolutionsWords = new HashSet<>();
        Queue<Thread> adjacentSolutionsTestingThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForTestingAdjacentSolutions = new ThreadGroup("adjacentSolutionTest");

        for (String word : allAdjacentSolutionsWords) {
            Thread thread = new Thread(threadGroupForTestingAdjacentSolutions , () -> {
               if (entries.stream().anyMatch(e -> e.getWord().equals(word)))
                   synchronized (allValidAdjacentSolutionsWords) {
                        allValidAdjacentSolutionsWords.add(word);
                   }
            });

            adjacentSolutionsTestingThreads.add(thread);
        }

        try {
            ThreadsRunner.runThreads(adjacentSolutionsTestingThreads, threadGroupForTestingAdjacentSolutions);
        } catch (Exception ignored) {}


        // Test every solutions and remove the ones with invalid adjacent entry words
        Set<Solution> validSolutions = unfilteredSolutions.stream()
                .filter(s -> s.getAdjacentSolutions()
                        .values()
                        .stream()
                        .allMatch(as -> allValidAdjacentSolutionsWords.contains(as.getWord()))
                ).collect(Collectors.toSet());

        // Calculate the points for every valid solutions and keep the 10 best solutions
        validSolutions.forEach(s -> PointCalculator.calculatePointsForSolutions(s, lettersValue));
        List<Solution> solutions = PointCalculator.getNBestSolutions(validSolutions, 10);
        solutions.sort(Solution::compareTo);

        return new ResponseEntity<>(solutions, HttpStatus.OK);
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
