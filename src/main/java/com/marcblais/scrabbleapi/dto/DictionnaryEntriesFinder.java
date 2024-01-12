package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.List;
import java.util.Map;

public class DictionnaryEntriesFinder {
    private List<DictionaryEntry> entries;
    private PlayerLetters playerLetters;
    private String gridContent;

    public DictionnaryEntriesFinder(List<DictionaryEntry> entries, PlayerLetters playerLetters) {
        this.entries = entries;
        this.playerLetters = playerLetters;
        this.gridContent = "";
    }

    public DictionnaryEntriesFinder(List<DictionaryEntry> entries, PlayerLetters playerLetters, String gridContent) {
        this.entries = entries;
        this.playerLetters = playerLetters;
        this.gridContent = gridContent;
    }

    public List<DictionaryEntry> getEntries() {
        return entries.stream().filter(entry -> {
            if (entry.getWord().length() < gridContent.length())
                return false;

            if (entry.getWord().length() > playerLetters.getLetters().length())
                return false;

            if (entry.getWord().equals(gridContent))
                return false;

            return isWordMadeFromLetters(entry, playerLetters.getLettersMap());
        }).toList();
    }

    private boolean isWordMadeFromLetters(DictionaryEntry dictionaryEntry, Map<String, Integer> playerLetters) {
        Map<String, Integer> wordLetters = dictionaryEntry.getLetters();

        for (String key : wordLetters.keySet()) {
            if (!playerLetters.containsKey(key))
                return false;

            if (wordLetters.get(key) > playerLetters.get(key))
                return false;
        }

        return true;
    }
}
