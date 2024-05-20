package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DictionnaryEntriesFinder {

    public static DictionaryEntry findEntryByWord(String word, Set<DictionaryEntry> entries) {
        return entries.stream().filter(e -> e.getWord().equals(word)).findFirst().orElse(null);
    }

    public static boolean isWordMadeFromLetters(DictionaryEntry entry, Map<String, Integer> playerLetters) {
        Map<String, Integer> entryLettersMap = entry.getLetters();
        int blankTiles = playerLetters.getOrDefault("#", 0);

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

    public static Set<DictionaryEntry> findEntriesByPlayerLetters(List<String> playerLetters, Set<DictionaryEntry> entries) {
        Map<String, Integer> lettersCountMap =
                LettersCounter.lettersCountMap(playerLetters);

        return entries.stream().filter(entry -> {
            if (entry.getWord().length() > playerLetters.size())
                return false;

            return isWordMadeFromLetters(entry, lettersCountMap);
        }).collect(Collectors.toSet());
    }

    public static Set<DictionaryEntry> findEntriesByPattern(
            List<String> pattern, List<String> playerLetters, Set<DictionaryEntry> entries
    ) {
        List<String> letters = new ArrayList<>(playerLetters);
        letters.addAll(pattern);

        Map<String, Integer> lettersCountMap = LettersCounter.lettersCountMap(letters);

        return entries.stream().filter(entry -> {
            if (entry.getWord().length() != pattern.size())
                return false;

            if (!entry.getWord().matches("^" + String.join("", pattern) + "$"))
                return false;

            return isWordMadeFromLetters(entry, lettersCountMap);
        }).collect(Collectors.toSet());
    }
}
