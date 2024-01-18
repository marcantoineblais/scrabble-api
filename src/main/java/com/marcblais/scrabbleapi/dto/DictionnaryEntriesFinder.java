package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DictionnaryEntriesFinder {

    public static DictionaryEntry findEntryByWord(String word, List<DictionaryEntry> entries) {
        return entries.stream().filter(e -> e.getWord().equals(word)).findFirst().orElse(null);
    }

    public static boolean isWordMadeFromLetters(DictionaryEntry entry, Map<String, Integer> playerLetters) {
        Map<String, Integer> entryLettersMap = entry.getLetters();

        for (String key : entryLettersMap.keySet()) {
            if (!playerLetters.containsKey(key))
                return false;

            if (entryLettersMap.get(key) > playerLetters.get(key))
                return false;
        }

        return true;
    }

    public static List<DictionaryEntry> findEntriesByPlayerLetters(String playerLetters, List<DictionaryEntry> entries) {
        Map<String, Integer> lettersCountMap =
                LettersCounter.lettersCountMap(playerLetters);

        return entries.stream().filter(entry -> {
            if (entry.getWord().length() != playerLetters.length())
                return false;

            return isWordMadeFromLetters(entry, lettersCountMap);
        }).toList();
    }

    public static List<DictionaryEntry> findEntriesByPattern(String pattern, String playerLetters, List<DictionaryEntry> entries) {
        String letters = pattern.replace(".", "") + playerLetters;
        Map<String, Integer> lettersCountMap = LettersCounter.lettersCountMap(letters);

        return entries.stream().filter(entry -> {
            if (entry.getWord().length() != pattern.length())
                return false;

            if (!entry.getWord().matches("^" + pattern + "$"))
                return false;

            return isWordMadeFromLetters(entry, lettersCountMap);
        }).toList();
    }
}
