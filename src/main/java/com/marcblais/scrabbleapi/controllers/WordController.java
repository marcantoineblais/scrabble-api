package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.entities.LettersValue;
import com.marcblais.scrabbleapi.services.WordService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class WordController {

    private WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/letters")
    public Set<DictionaryEntry> findWordsWithLetters(@RequestParam(name = "letters") String playerLetters) {
        Language language = wordService.findLanguageById(1);
        Set<DictionaryEntry> entries = wordService.findWordsByLanguage(language);
        Set<DictionaryEntry> matchingEntries =
                DictionnaryEntriesFinder.findEntriesByPlayerLetters(playerLetters.toUpperCase(), entries);

        return matchingEntries;
    }

    @PostMapping("/grid")
    @CrossOrigin(origins = "http://localhost:3000")
    public List<Solution> findWordsThatFitsOnGrid(@RequestBody Grid grid) {
        long startTime = System.currentTimeMillis();
        LettersValue lettersValue = wordService.findLettersValueByLanguage(grid.getGridType().getLanguage());
        Set<DictionaryEntry> entries = wordService.findWordsByLanguage(grid.getGridType().getLanguage());
        List<GridContent> gridContents = grid.toGridContent();
        SolutionsFinder solutionsFinder = new SolutionsFinder(grid, entries, gridContents);
        Set<Solution> solutions = solutionsFinder.toSolutions();
        PointCalculator pointCalculator = new PointCalculator(grid, solutions, lettersValue);
        pointCalculator.calculatePoints();

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");

        return pointCalculator.findTopSolutions(10);
    }
}
