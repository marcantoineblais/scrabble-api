package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.services.WordService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WordController {

    private WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/letters")
    public List<DictionaryEntry> findWordsWithLetters(@RequestParam(name = "letters") String playerLetters) {
        long startTime = System.currentTimeMillis();
        Language language = wordService.findLanguageById(1);
        List<DictionaryEntry> entries = wordService.findWordsByLanguage(language);
        DictionnaryEntriesFinder dictionnaryEntriesFinder =
                new DictionnaryEntriesFinder(entries, playerLetters.toUpperCase());

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");
        return dictionnaryEntriesFinder.getEntries();
    }

    @PostMapping("/grid")
    @CrossOrigin(origins = "http://localhost:3000")
    public List<Solution> findWordsThatFitsOnGrid(@RequestBody Grid grid) {
        long startTime = System.currentTimeMillis();

        List<DictionaryEntry> entries = wordService.findWordsByLanguage(grid.getLanguage());
        List<GridContent> gridContents = grid.toGridContent();
        SolutionsFinder solutionsFinder = new SolutionsFinder(grid, entries, gridContents);
        List<Solution> solutions = solutionsFinder.toSolutions();

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");

        return solutions;
    }
}
