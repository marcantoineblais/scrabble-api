package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Word;
import com.marcblais.scrabbleapi.services.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class WordController {

    private WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("/words-with-letters")
    public List<Word> findWordsWithLetters(@RequestParam(name = "letters") String playerLetters) {
        Language language = wordService.findLanguageById(1);
        List<Word> words = wordService.findWordsByLanguage(language);

        words = words.stream().filter(word -> {
            if (word.getWord().length() > playerLetters.length())
                return false;

            return isWordMadeFromLetters(word, playerLetters);
        }).toList();

        return words;
    }

    public boolean isWordMadeFromLetters(Word word, String playerLetters) {
        Map<String, Integer> wordLetters = word.getLetters();

        for (String key : wordLetters.keySet()) {
            if (!playerLetters.contains(key))
                return false;
            if (playerLetters.split(key).length - 1 > wordLetters.get(key))
                return false;
        }

        return true;
    }
}
