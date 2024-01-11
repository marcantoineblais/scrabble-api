package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.WordWithPlayerLetters;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Word;
import com.marcblais.scrabbleapi.services.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WordController {

    private WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/letters")
    public List<Word> findWordsWithLetters(@RequestParam(name = "letters") String playerLetters) {
        Language language = wordService.findLanguageById(1);
        List<Word> words = wordService.findWordsByLanguage(language);
        WordWithPlayerLetters wordWithPlayerLetters = new WordWithPlayerLetters(words, playerLetters);



        return words;
    }

}
