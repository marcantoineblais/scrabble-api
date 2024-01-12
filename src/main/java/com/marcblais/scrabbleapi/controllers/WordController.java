package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.Grid;
import com.marcblais.scrabbleapi.dto.GridContent;
import com.marcblais.scrabbleapi.dto.WordWithLetters;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Dictionary;
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
    public List<Dictionary> findWordsWithLetters(@RequestParam(name = "letters") String playerLetters) {
        long startTime = System.currentTimeMillis();
        Language language = wordService.findLanguageById(1);
        List<Dictionary> dictionaries = wordService.findWordsByLanguage(language);
        WordWithLetters wordWithLetters = new WordWithLetters(dictionaries, playerLetters);

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");
        return wordWithLetters.getWords();
    }

    @PostMapping("/grid")
    public List<Dictionary> findWordsThatFitsOnGrid(@RequestBody Grid grid) {
        long startTime = System.currentTimeMillis();
        Language language = wordService.findLanguageById(1);
        List<Dictionary> dictionaries = wordService.findWordsByLanguage(language);
        List<GridContent> gridContents = grid.toGridContent();

        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("\nRequest took " + endTime + "ms\n");
    }
}
