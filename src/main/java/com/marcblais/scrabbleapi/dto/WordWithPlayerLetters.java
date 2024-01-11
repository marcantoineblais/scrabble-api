package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Word;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordWithPlayerLetters {
    private List<Word> words;
    private Word playerLetters;

    public WordWithPlayerLetters(List<Word> words, String playerLetters) {
        this.words = words;
        this.playerLetters = new Word(playerLetters.toUpperCase());
    }

    public List<Word> getWords() {
        return words.stream().filter(word -> {
            if (word.getWord().length() > playerLetters.getWord().length())
                return false;

            return isWordMadeFromLetters(word, playerLetters.getLetters());
        }).toList();
    }

    private boolean isWordMadeFromLetters(Word word, Map<String, Integer> playerLetters) {
        Map<String, Integer> wordLetters = word.getLetters();

        for (String key : wordLetters.keySet()) {
            if (!playerLetters.containsKey(key))
                return false;

            if (wordLetters.get(key) > playerLetters.get(key))
                return false;
        }

        return true;
    }
}
