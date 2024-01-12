package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Dictionary;

import java.util.List;
import java.util.Map;

public class WordWithLetters {
    private List<Dictionary> dictionaries;
    private Dictionary playerLetters;

    public WordWithLetters(List<Dictionary> dictionaries, String playerLetters) {
        this.dictionaries = dictionaries;
        this.playerLetters = new Dictionary(playerLetters.toUpperCase());
    }

    public List<Dictionary> getWords() {
        return dictionaries.stream().filter(word -> {
            if (word.getWord().length() > playerLetters.getWord().length())
                return false;

            return isWordMadeFromLetters(word, playerLetters.getLetters());
        }).toList();
    }

    private boolean isWordMadeFromLetters(Dictionary dictionary, Map<String, Integer> playerLetters) {
        Map<String, Integer> wordLetters = dictionary.getLetters();

        for (String key : wordLetters.keySet()) {
            if (!playerLetters.containsKey(key))
                return false;

            if (wordLetters.get(key) > playerLetters.get(key))
                return false;
        }

        return true;
    }
}
