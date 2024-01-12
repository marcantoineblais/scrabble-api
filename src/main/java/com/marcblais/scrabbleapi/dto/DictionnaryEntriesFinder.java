package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.List;
import java.util.Map;

public class DictionnaryEntriesFinder {
    private List<DictionaryEntry> entries;
    private String playerLetters;
    private String gridContent;

    public DictionnaryEntriesFinder(List<DictionaryEntry> entries, String playerLetters) {
        this.entries = entries;
        this.playerLetters = playerLetters;
        this.gridContent = "";
    }

    public DictionnaryEntriesFinder(List<DictionaryEntry> entries, String playerLetters, String gridContent) {
        this.entries = entries;
        this.playerLetters = playerLetters;
        this.gridContent = gridContent;
    }

    public List<DictionaryEntry> getEntries() {
        return entries.stream().filter(entry -> {
            if (entry.getWord().length() < gridContent.length() + 1)
                return false;

            if (entry.getWord().length() > (gridContent + playerLetters).length())
                return false;

            return isWordMadeFromLetters(entry, LettersCounter.getLettersCountMap(gridContent + playerLetters));
        }).toList();
    }

    private boolean isWordMadeFromLetters(DictionaryEntry entry, Map<String, Integer> playerLetters) {
        Map<String, Integer> entryLetters = entry.getLetters();

        for (String key : entryLetters.keySet()) {
            if (!playerLetters.containsKey(key))
                return false;

            if (entryLetters.get(key) > playerLetters.get(key))
                return false;
        }

        return true;
    }
}
