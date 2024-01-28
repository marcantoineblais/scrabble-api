package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.entities.LettersValue;
import com.marcblais.scrabbleapi.services.GameService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
import com.marcblais.scrabbleapi.utilities.SolutionsFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Set<DictionaryEntry> findWordsWithLetters(@RequestParam(name = "letters") String playerLetters) {
        Language language = gameService.findLanguageById(1);
        Set<DictionaryEntry> entries = gameService.findWordsByLanguage(language);
        Set<DictionaryEntry> matchingEntries =
                DictionnaryEntriesFinder.findEntriesByPlayerLetters(playerLetters.toUpperCase(), entries);

        return matchingEntries;
    }

    @PostMapping("/grid")
    public List<Solution> findWordsThatFitsOnGrid(@RequestBody Grid grid) {
        LettersValue lettersValue = gameService.findLettersValueByLanguage(grid.getLanguage());
        Set<DictionaryEntry> entries = gameService.findWordsByLanguage(grid.getLanguage());
        List<GridContent> gridContents = grid.toGridContent();
        SolutionsFinder solutionsFinder = new SolutionsFinder(grid, entries, gridContents);
        Set<Solution> solutions = solutionsFinder.toSolutions();
        PointCalculator pointCalculator = new PointCalculator(grid, solutions, lettersValue);
        pointCalculator.calculatePoints();

        return pointCalculator.findTopSolutions(10);
    }
}
