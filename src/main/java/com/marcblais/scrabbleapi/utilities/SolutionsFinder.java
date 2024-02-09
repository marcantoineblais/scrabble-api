package com.marcblais.scrabbleapi.utilities;

import java.util.*;
import java.util.stream.Collectors;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.GridContent;
import com.marcblais.scrabbleapi.dto.GridDTO;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;

public class SolutionsFinder {
    private GridDTO grid;
    private Set<DictionaryEntry> entries;
    private List<GridContent> gridContents;
    private final Map<String, Set<DictionaryEntry>> foundEntriesMap;

    public SolutionsFinder() {
        this.foundEntriesMap = new HashMap<>();
    }

    public SolutionsFinder(GridDTO grid, Set<DictionaryEntry> entries, List<GridContent> gridContents) {
        this.grid = grid;
        this.entries = entries;
        this.gridContents = gridContents;
        this.foundEntriesMap = new HashMap<>();
    }

    public Set<Solution> toSolutions() {
        // Initialize list of working solutions
        Set<Solution> solutions = new HashSet<>();

        for (GridContent gridContent : gridContents) {
            solutions.addAll(findSolutionsForGridContent(
                    gridContent, null, null, ""));
        }

        solutions.addAll(findParallelSolution(solutions));
        return solutions;
    }

    private Set<DictionaryEntry> findMatchingEntries(String pattern, String ignoredLetter) {
        return DictionnaryEntriesFinder.findEntriesByPattern(pattern, grid.getPlayerLetters(), entries, ignoredLetter);
    }

    private List<Solution> findSolutionsForGridContent(
            GridContent gridContent, GridContent oldGridContent, AdjacentSolution adjacentSolution, String ignoredLetter
    ) {
        List<Solution> solutions = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        // Get a list of regexp pattern to find words, sorted by index of grid content characters array
        Map<Integer, List<String>> testPatterns =findOverlappingString()
                gridContent.testPatterns(grid.getPlayerLetters().replaceFirst(ignoredLetter, ""));

        if (oldGridContent != null) {
            Map<Integer, List<String>> oldPatterns = oldGridContent.testPatterns(grid.getPlayerLetters());
            for (int key : testPatterns.keySet()) {
                if (oldPatterns.containsKey(key)) {
                    testPatterns.get(key).removeAll(oldPatterns.get(key));
                }
            }
        }

        for (int key : testPatterns.keySet()) {
            for (String pattern : testPatterns.get(key)) {
                threads.add(new Thread(() -> {
                    // initialize a list of entries that matches the regexp pattern and the players letters
                    Set<DictionaryEntry> matchingEntries;

                    // check if pattern was found before, if so get its matches
                    // else find the matches in the dictionnary and add it to the map
                    if (foundEntriesMap.containsKey(pattern) && oldGridContent == null) {
                        matchingEntries = foundEntriesMap.get(pattern);
                    } else {
                        matchingEntries = findMatchingEntries(pattern, ignoredLetter);

                        synchronized (foundEntriesMap) {
                            foundEntriesMap.put(pattern, matchingEntries);
                        }
                    }

                    // test every matches to make sure they can be played on the grid
                    Set<Solution> solutionsForEntries =
                            findSolutionForEntries(matchingEntries, gridContent, key, pattern, adjacentSolution);

                    // to avoid missing data because of the multi-threading
                    synchronized (solutions) {
                        solutions.addAll(solutionsForEntries);
                    }
                }));
            }
        }

        ThreadsRunner.runThreads(threads);

        return solutions;
    }

    private Set<Solution> findSolutionForEntries(
            Set<DictionaryEntry> entries,
            GridContent gridContent,
            int index,
            String pattern,
            AdjacentSolution adjacentSolution
    ) {
        Set<Solution> solutions = new HashSet<>();
        for (DictionaryEntry entry : entries) {
            // find the words that are formed perpendicular to the solution
            Map<Integer, AdjacentSolution> adjacentSolutions =
                    findAdjacentSolutions(entry, gridContent, index, pattern);

            // Only add the solution if its adjacent solutions are all valid words
            if (adjacentSolutions != null) {
                if (adjacentSolution != null) {
                    int i = 0;
                    while (pattern.charAt(i) == '.') {
                        i++;
                    }
                    adjacentSolutions.put(i, adjacentSolution);
                }

                Solution solution = new Solution(
                        entry,
                        gridContent,
                        adjacentSolutions,
                        pattern,
                        gridContent.isVertical(),
                        gridContent.isVertical() ? gridContent.getIndex() : index,
                        gridContent.isVertical() ? index : gridContent.getIndex()
                );

                solutions.add(solution);
            }
        }

        return solutions;
    }

    private Set<Solution> findParallelSolution(Set<Solution> solutions) {
        Set<Solution> parallelSolutions = new HashSet<>();
        Set<Solution> solutionsToTest = solutions.stream()
                .filter(s -> {
                    int wordLength = s.getEntry().getWord().length();
                    int nbLettersUsed = wordLength - s.getPattern().replace(".", "").length();
                    return nbLettersUsed == 1 && s.getAdjacentSolutions().isEmpty();
                })
                .collect(Collectors.toSet());

        for (Solution solution : solutionsToTest) {
            GridContent gridContent = solution.getGridContent();
            int index = solution.isVertical() ? solution.getY() : solution.getX();
            int startIndex = index;

            if (gridContent.getContent().charAt(index) != '.')
                startIndex++;

            GridContent perpendicularContent = findPerpendicularContent(gridContent, startIndex);
            GridContent tempGridContent = new GridContent(perpendicularContent);
            AdjacentSolution adjacentSolution = new AdjacentSolution(solution.getEntry());
            String word = solution.getEntry().getWord();
            char newContent = startIndex == index ? word.charAt(0) : word.charAt(1);

            tempGridContent.replaceContent(newContent, gridContent.getIndex());
            parallelSolutions.addAll(findSolutionsForGridContent(
                            tempGridContent, perpendicularContent, adjacentSolution, String.valueOf(newContent)));
        }

        return parallelSolutions;
    }

    private Map<Integer, AdjacentSolution> findAdjacentSolutions(
            DictionaryEntry entry, GridContent gridContent, int index, String pattern
    ) {
        Map<Integer, AdjacentSolution> adjacentSolutions = new HashMap<>();
        char[] charsArray = gridContent.getContent().toCharArray();

        for (int i = 0; i < pattern.length(); i++) {
            // if the spot on the grid was not filled before, check its surrounding
            if (charsArray[i + index] == '.') {
                // find the GridContent that is perpendicular from the solution on the current character
                GridContent perpendicularContent = findPerpendicularContent(gridContent, i + index);
                DictionaryEntry adjacentEntry;

                // if there is no letters in this grid content, then there will not be any adjacent solution here
                if (perpendicularContent != null) {
                    // get the string formed by the players letter and the surrounding words
                    String adjacentSolutionString = findOverlappingString(
                            perpendicularContent, entry.getWord().charAt(i), gridContent.getIndex()
                    );

                    // if there is at least 2 letters, make sure they form a valid word
                    // else reject this solution
                    if (adjacentSolutionString.length() > 1) {
                        adjacentEntry = DictionnaryEntriesFinder.findEntryByWord(adjacentSolutionString, entries);

                        if (adjacentEntry != null) {
                            AdjacentSolution adjacentSolution = new AdjacentSolution(adjacentEntry);
                            adjacentSolutions.put(i, adjacentSolution);
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
