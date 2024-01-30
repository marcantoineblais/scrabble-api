package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.services.GameService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
import com.marcblais.scrabbleapi.utilities.SolutionsFinder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<Grid> createGrid(
            @RequestBody GameOption gameOption
    ) {
        System.out.println(gameOption);
//        Player player = findPlayer(token);
//        ResponseEntity<Grid> responseEntity = playerLoggedIn(player);

//        if (responseEntity != null)
//            return responseEntity;

        Grid grid = new Grid();
        grid.buildGrid();
        grid.setGridType(gameOption.getGridType());
        grid.setLanguage(gameOption.getLanguage());
        grid.setName(gameOption.getName());
        System.out.println(grid);
//        gameService.saveGrid(grid);
        return new ResponseEntity<>(grid, HttpStatus.OK);
    }

    @PostMapping("/grid/solve")
    public ResponseEntity<List<Solution>> findWordsThatFitsOnGrid(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody Grid grid
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
