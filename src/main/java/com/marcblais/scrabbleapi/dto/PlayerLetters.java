package com.marcblais.scrabbleapi.dto;

import java.util.Map;

public class PlayerLetters {
    private String letters;

    public PlayerLetters() {
    }

    public PlayerLetters(String letters) {
        this.letters = letters;
    }

    public String getLetters() {
        return letters;
    }

    public Map<String, Integer> getLettersMap() {
        return LettersCounter.getLettersCountMap(letters);
    }
}
