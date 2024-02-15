package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.services.GameService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
import com.marcblais.scrabbleapi.utilities.SolutionsFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/letters")
    public Set<DictionaryEntry> findWordsWithLetters(
            @RequestParam(name = "letters") String playerLetters,
            @RequestParam(name = "language") String name
    ) {
        Language language = gameService.findLanguageByName(name);
        Set<DictionaryEntry> entries = gameService.findWordsByLanguage(language);
        Set<DictionaryEntry> matchingEntries =
                DictionnaryEntriesFinder.findEntriesByPlayerLetters(playerLetters.toUpperCase(), entries);

        return matchingEntries;
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

        LettersValue lettersValue = gameService.findLettersValueByLanguage(grid.getLanguage());
        Set<DictionaryEntry> entries = gameService.findWordsByLanguage(grid.getLanguage());
        List<GridContent> gridContents = grid.toGridContent();
        SolutionsFinder solutionsFinder = new SolutionsFinder(grid, entries, gridContents);
        Set<Solution> solutions = solutionsFinder.toSolutions();
        PointCalculator pointCalculator = new PointCalculator(grid, solutions, lettersValue);
        pointCalculator.calculatePoints();

        List<Solution> bestSolutions = pointCalculator.findTopSolutions(10);
        return new ResponseEntity<>(bestSolutions, HttpStatus.OK);
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
