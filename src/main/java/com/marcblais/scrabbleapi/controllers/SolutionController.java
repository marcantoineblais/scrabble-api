package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.entities.LettersValue;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.services.SolutionService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
import com.marcblais.scrabbleapi.utilities.ThreadsRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SolutionController {

    private final SolutionService solutionService;

    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    @PostMapping("/grid/solve")
    public ResponseEntity<List<Solution>> findWordsThatFitsOnGrid(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GridDTO grid
    ) {
        Player player = findPlayer(token);

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // Recuperer la valeur des lettres
        LettersValue lettersValue = solutionService.findLettersValueByLanguage(grid.getLanguage());

        // Recuperer la liste des mots dans la langue de la grille
        Set<DictionaryEntry> entries = solutionService.findWordsByLanguage(grid.getLanguage());

        // Find the best solutions for the grid
        List<Solution> solutions = solutionService.solveGrid(grid, entries, lettersValue);

        return new ResponseEntity<>(solutions, HttpStatus.OK);
    }


    private Player findPlayer(String token) {
        String username = PlayerToken.getUsernameFromJwt(token);
        return solutionService.findPlayerByUsername(username);
    }
}
