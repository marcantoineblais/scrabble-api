package com.marcblais.scrabbleapi.utilities;

import java.util.HashMap;
import java.util.Map;

public class LettersCounter {
    public static Map<String, Integer> lettersCountMap(String[] letters) {
        Map<String, Integer> lettersMap = new HashMap<>();

        for (String letter : letters) {
            lettersMap.merge(letter, 1, Integer::sum);
        }

        return lettersMap;
    }
}
