package com.marcblais.scrabbleapi.dto;

import java.util.*;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.entities.Grid;

public class SolutionsFinder {
    private Grid grid;
    private List<DictionaryEntry> entries;
    private List<GridContent> gridContents;

    public SolutionsFinder() {
    }

    public SolutionsFinder(Grid grid, List<DictionaryEntry> entries, List<GridContent> gridContents) {
        this.grid = grid;
        this.entries = entries;
        this.gridContents = gridContents;
    }

    public List<Solution> toSolutions() {
        List<Solution> solutions = new ArrayList<>();

        for (GridContent gridContent : gridContents) {
            Map<Integer, List<String>> testPatterns = gridContent.testPatterns();

            for (int key : testPatterns.keySet()) {
                for (String pattern : testPatterns.get(key)) {
                    List<DictionaryEntry> matchingEntries = findMatchingEntries(pattern);
                    List<Solution> solutionsForEntry =
                            findSolutionForEntries(matchingEntries, gridContent, key, pattern);

                    solutions.addAll(solutionsForEntry);
                }
            }
        }

        return solutions;
    }

    private List<DictionaryEntry> findMatchingEntries(String pattern) {
        return DictionnaryEntriesFinder.findEntriesByPattern(pattern, grid.getPlayerLetters(), entries);
    }

    private List<Solution> findSolutionForEntries(
            List<DictionaryEntry> entries, GridContent gridContent, int index, String pattern
    ) {
        List<Solution> solutions = new ArrayList<>();

        for (DictionaryEntry entry : entries) {
            List<AdjacentSolution> adjacentSolutions = findAdjacentSolutions(entry, gridContent, index, pattern);

            if (adjacentSolutions != null) {
                Solution solution = new Solution(
                        entry,
                        gridContent.isVertical() ? gridContent.getIndex() : index,
                        gridContent.isVertical() ? index : gridContent.getIndex(),
                        gridContent.isVertical(),
                        0,
                        adjacentSolutions
                );
                solutions.add(solution);
            }
        }

        return solutions;
    }

    private List<AdjacentSolution> findAdjacentSolutions(DictionaryEntry entry, GridContent gridContent, int index, String pattern) {
        List<AdjacentSolution> adjacentSolutions = new ArrayList<>();
        char[] charsArray = gridContent.getContent().toCharArray();

        for (int i = 0; i < pattern.length(); i++) {
            if (charsArray[i + index] == '.') {
                GridContent perpendicularContent = findPerpendicularContent(gridContent, i + index);
                DictionaryEntry adjacentEntry;

                if (perpendicularContent != null) {
                    String adjacentSolutionString =
                            findOverlappingString(perpendicularContent, entry.getWord().charAt(i), gridContent.getIndex());

                    if (adjacentSolutionString.length() > 1) {
                        adjacentEntry = DictionnaryEntriesFinder.findEntryByWord(adjacentSolutionString, entries);

                        if (adjacentEntry != null) {
                            AdjacentSolution adjacentSolution = new AdjacentSolution(adjacentEntry, 0);
                            adjacentSolutions.add(adjacentSolution);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }

        return adjacentSolutions;
    }

    private GridContent findPerpendicularContent(GridContent gridContent, int index) {
        return gridContents.stream()
                .filter(c -> c.isVertical() != gridContent.isVertical() && c.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    private String findOverlappingString(GridContent gridContent, char currentChar, int index) {
        char[] contentCharsArray = gridContent.getContent().toCharArray();
        StringBuilder builder = new StringBuilder();
        builder.append(currentChar);

        int i = index - 1;
        while (i > 0 && contentCharsArray[i] != '.') {
            builder.insert(0, contentCharsArray[i--]);
        }

        i = index + 1;
        while (i < contentCharsArray.length && contentCharsArray[i] != '.') {
            builder.append(contentCharsArray[i++]);
        }

        return builder.toString();
    }
}
