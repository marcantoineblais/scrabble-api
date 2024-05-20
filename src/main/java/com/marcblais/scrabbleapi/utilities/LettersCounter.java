package com.marcblais.scrabbleapi.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LettersCounter {
    public static Map<String, Integer> lettersCountMap(List<String> letters) {
        Map<String, Integer> lettersMap = new HashMap<>();

        for (String letter : letters) {
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
