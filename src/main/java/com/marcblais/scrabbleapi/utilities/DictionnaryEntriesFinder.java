package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DictionnaryEntriesFinder {

    public static DictionaryEntry findEntryByWord(String word, Set<DictionaryEntry> entries) {
        return entries.stream().filter(e -> e.getWord().equals(word)).findFirst().orElse(null);
    }

    public static boolean isWordMadeFromLetters(DictionaryEntry entry, Map<String, Integer> playerLetters) {
        Map<String, Integer> entryLettersMap = entry.getLetters();
        int blankTiles = playerLetters.getOrDefault(".", 0);

        for (String key : entryLettersMap.keySet()) {
            int nbLettersEntry = entryLettersMap.get(key);
            int nbLettersPlayer = playerLetters.getOrDefault(key, 0);

            if (nbLettersEntry > nbLettersPlayer) {
                int nbMissingLetters = nbLettersEntry - nbLettersPlayer;

                if (nbMissingLetters > blankTiles)
                    return false;
                else
                    blankTiles -= nbMissingLetters;
            }
        }

        return true;
    }

    public static Set<DictionaryEntry> findEntriesByPlayerLetters(String playerLetters, Set<DictionaryEntry> entries) {
        Map<String, Integer> lettersCountMap =
                LettersCounter.lettersCountMap(playerLetters);

        return entries.stream().filter(entry -> {
            if (entry.getWord().length() > playerLetters.length())
                return false;

            return isWordMadeFromLetters(entry, lettersCountMap);
        }).collect(Collectors.toSet());
    }

    public static Set<DictionaryEntry> findEntriesByPattern(
            String pattern, String playerLetters, Set<DictionaryEntry> entries
    ) {
        String letters = pattern.replace(".", "") + playerLetters;
        Map<String, Integer> lettersCountMap = LettersCounter.lettersCountMap(letters);

        return entries.stream().filter(entry -> {
            if (entry.getWord().length() != pattern.length())
                return false;

            if (!entry.getWord().matches("^" + pattern + "$"))
                return false;

            return isWordMadeFromLetters(entry, lettersCountMap);
        }).collect(Collectors.toSet());
    }
}
