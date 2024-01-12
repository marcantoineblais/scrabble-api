package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.Grid;
import com.marcblais.scrabbleapi.dto.GridContent;
import com.marcblais.scrabbleapi.dto.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.dto.PlayerLetters;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.services.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WordController {

    private WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/letters")
    public List<DictionaryEntry> findWordsWithLetters(@RequestParam(name = "letters") String letters) {
        long startTime = System.currentTimeMillis();
        Language language = wordService.findLanguageById(1);
        List<DictionaryEntry> entries = wordService.findWordsByLanguage(language);
        PlayerLetters playerLetters = new PlayerLetters(letters);
        DictionnaryEntriesFinder dictionnaryEntriesFinder = new DictionnaryEntriesFinder(entries, playerLetters);

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");
        return dictionnaryEntriesFinder.getEntries();
    }

    @PostMapping("/grid")
    public List<DictionaryEntry> findWordsThatFitsOnGrid(@RequestBody Grid grid) {
        long startTime = System.currentTimeMillis();
        Language language = wordService.findLanguageById(1);
        List<DictionaryEntry> dictionaries = wordService.findWordsByLanguage(language);
        List<GridContent> gridContents = grid.toGridContent();

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");

        return null;
    }
}
