package com.marcblais.scrabbleapi.dto;

import java.util.HashMap;
import java.util.Map;

public class LettersCounter {
    public static Map<String, Integer> getLettersCountMap(String letters) {
        Map<String, Integer> lettersMap = new HashMap<>();
        String[] lettersArray = letters.split("");

        for (String letter : lettersArray) {
            if (lettersMap.containsKey(letter)) {
                int count = lettersMap.get(letter);
                lettersMap.put(letter, count + 1);
            } else {
                lettersMap.put(letter, 1);
            }
        }

        return lettersMap;
    }
}
