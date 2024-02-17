package com.marcblais.scrabbleapi.utilities;

import java.util.*;
import java.util.stream.Collectors;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.GridRowsCols;
import com.marcblais.scrabbleapi.dto.GridDTO;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;

public class SolutionsFinder {
    private GridDTO grid;
    private Set<DictionaryEntry> entries;
    private List<GridRowsCols> gridRowsCols;
    private final Map<String, Set<DictionaryEntry>> foundEntriesMap;

    public SolutionsFinder() {
        this.foundEntriesMap = new HashMap<>();
    }

    public SolutionsFinder(GridDTO grid, Set<DictionaryEntry> entries, List<GridRowsCols> gridRowsCols) {
        this.grid = grid;
        this.entries = entries;
        this.gridRowsCols = gridRowsCols;
        this.foundEntriesMap = new HashMap<>();
    }

    public Set<Solution> toSolutions() {
        // Initialize list of working solutions
        Set<Solution> solutions = new HashSet<>();

        if (gridRowsCols.isEmpty()) {
            GridRowsCols gridRowsCols =
                    new GridRowsCols(".".repeat(grid.getGrid().length), grid.getGrid().length / 2, false);

            Set<DictionaryEntry> validEntries =
                    DictionnaryEntriesFinder.findEntriesByPlayerLetters(grid.getPlayerLetters(), entries);

            for (DictionaryEntry entry : validEntries) {
                solutions.addAll(findSolutionsForFirstWord(entry, gridRowsCols));
            }
        } else {

            for (GridRowsCols gridRowsCols : this.gridRowsCols) {
                solutions.addAll(findSolutionsForGridContent(
                        gridRowsCols, null, null, ""));
            }

            solutions.addAll(findParallelSolution(solutions));
        }

        return solutions;
    }

    private List<Solution> findSolutionsForFirstWord(DictionaryEntry entry, GridRowsCols gridRowsCols) {
        List<Solution> solutions = new ArrayList<>();

        int index = grid.getGrid().length / 2 - (entry.getWord().length() - 1);
        int y = 0;

        while (y < entry.getWord().length()) {
            String pattern = ".".repeat(entry.getWord().length());
            String letterToRemove = grid.getPlayerLetters().replace(".", "");

            if (grid.getPlayerLetters().contains(".")) {
                String jokerLetter = entry.getWord();

                for (String letter : letterToRemove.split("")) {
                    jokerLetter = jokerLetter.replaceFirst(letter, "");
                }

                List<Solution> solutionsWithJoker = PermutationFinder.findPermutationOfJokerTiles(
                        jokerLetter,
                        entry,
                        gridRowsCols,
                        new HashMap<>(),
                        index + y++,
                        pattern
                );

                solutions.addAll(solutionsWithJoker);
            } else {
                solutions.add(new Solution(
                        entry,
                        gridRowsCols,
                        new HashMap<>(),
                        pattern,
                        false,
                        index + y++,
                        gridRowsCols.getIndex()
                ));
            }
        }

        return solutions;
    }

    private Set<DictionaryEntry> findMatchingEntries(String pattern, String ignoredLetter) {
        return DictionnaryEntriesFinder.findEntriesByPattern(pattern, grid.getPlayerLetters(), entries, ignoredLetter);
    }

    private List<Solution> findSolutionsForGridContent(
            GridRowsCols gridRowsCols, GridRowsCols oldGridRowsCols, AdjacentSolution adjacentSolution, String ignoredLetter
    ) {
        List<Solution> solutions = new ArrayList<>();
        Queue<Thread> threads = new ArrayDeque<>();
        ThreadGroup threadGroup = new ThreadGroup("main");

        // Get a list of regexp pattern to find words, sorted by index of grid content characters array
        Map<Integer, List<String>> testPatterns =
                gridRowsCols.testPatterns(grid.getPlayerLetters().replaceFirst(ignoredLetter, ""));

        if (oldGridRowsCols != null) {
            Map<Integer, List<String>> oldPatterns = oldGridRowsCols.testPatterns(grid.getPlayerLetters());
            for (int key : testPatterns.keySet()) {
                if (oldPatterns.containsKey(key)) {
                    testPatterns.get(key).removeAll(oldPatterns.get(key));
                }
            }
        }

        for (int key : testPatterns.keySet()) {
            for (String pattern : testPatterns.get(key)) {
                threads.add(new Thread(threadGroup, () -> {
                    // initialize a list of entries that matches the regexp pattern and the players letters
                    Set<DictionaryEntry> matchingEntries;
                    Thread nextThread = null;
                    // check if pattern was found before, if so get its matches
                    // else find the matches in the dictionnary and add it to the map
                    if (foundEntriesMap.containsKey(pattern) && oldGridRowsCols == null) {
                        matchingEntries = foundEntriesMap.get(pattern);
                    } else {
                        matchingEntries = findMatchingEntries(pattern, ignoredLetter);

                        synchronized (foundEntriesMap) {
                            foundEntriesMap.put(pattern, matchingEntries);
                        }
                    }

                    // test every matches to make sure they can be played on the grid
                    Set<Solution> solutionsForEntries =
                            findSolutionForEntries(matchingEntries, gridRowsCols, key, pattern, adjacentSolution);

                    // to avoid missing data because of the multi-threading
                    synchronized (solutions) {
                        solutions.addAll(solutionsForEntries);
                    }
                }));
            }
        }

        try {
            ThreadsRunner.runThreads(threads, threadGroup);
        } catch (Exception ignored) {}

        return solutions;
    }

    private Set<Solution> findSolutionForEntries(
            Set<DictionaryEntry> entries,
            GridRowsCols gridRowsCols,
            int index,
            String pattern,
            AdjacentSolution adjacentSolution
    ) {
        Set<Solution> solutions = new HashSet<>();
        String patternLetters = pattern.replace(".", "");
        String playerLetters = grid.getPlayerLetters().replace(".", "");
        String letterToRemove = playerLetters + patternLetters;

        for (DictionaryEntry entry : entries) {
            // find the words that are formed perpendicular to the solution
            Map<Integer, AdjacentSolution> adjacentSolutions =
                    findAdjacentSolutions(entry, gridRowsCols, index, pattern);

            // Only add the solution if its adjacent solutions are all valid words
            if (adjacentSolutions != null) {
                // find the index of the given adjacent solution when ran from findParallelSolutions
                if (adjacentSolution != null) {
                    int i = 0;
                    while (pattern.charAt(i) == '.') {
                        i++;
                    }
                    adjacentSolutions.put(i, adjacentSolution);
                }

                if (grid.getPlayerLetters().contains(".")) {
                    String jokerLetter = entry.getWord();

                    for (String letter : letterToRemove.split("")) {
                        jokerLetter = jokerLetter.replaceFirst(letter, "");
                    }

                    List<Solution> solutionsWithJoker = PermutationFinder.findPermutationOfJokerTiles(
                            jokerLetter, entry, gridRowsCols, adjacentSolutions, index, pattern
                    );

                    solutions.addAll(solutionsWithJoker);
                } else {
                    Solution solution = new Solution(
                            entry,
                            gridRowsCols,
                            adjacentSolutions,
                            pattern,
                            gridRowsCols.isVertical(),
                            gridRowsCols.isVertical() ? gridRowsCols.getIndex() : index,
                            gridRowsCols.isVertical() ? index : gridRowsCols.getIndex()
                    );
                    solutions.add(solution);
                }
            }
        }

        return solutions;
    }

    private Set<Solution> findParallelSolution(Set<Solution> solutions) {
        // Filter the solutions to find potentiel candidates for places where a word could be inserted next to another one
        Set<Solution> parallelSolutions = new HashSet<>();
        Set<Solution> solutionsToTest = solutions.stream()
                .filter(s -> {
                    int wordLength = s.getEntry().getWord().length();
                    int nbLettersUsed = wordLength - s.getPattern().replace(".", "").length();
                    return nbLettersUsed == 1 && s.getAdjacentSolutions().isEmpty();
                })
                .collect(Collectors.toSet());

        for (Solution solution : solutionsToTest) {
            GridRowsCols gridRowsCols = solution.getGridContent();
            int index = solution.isVertical() ? solution.getY() : solution.getX();
            int startIndex = index;

            if (gridRowsCols.getContent().charAt(index) != '.')
                startIndex++;

            GridRowsCols perpendicularContent = findPerpendicularContent(gridRowsCols, startIndex);
            if (perpendicularContent != null) {
                GridRowsCols tempGridRowsCols = new GridRowsCols(perpendicularContent);
                AdjacentSolution adjacentSolution = new AdjacentSolution(solution.getEntry());
                String word = solution.getEntry().getWord();
                char newContent = startIndex == index ? word.charAt(0) : word.charAt(1);

                tempGridRowsCols.replaceContent(newContent, gridRowsCols.getIndex());
                parallelSolutions.addAll(findSolutionsForGridContent(
                        tempGridRowsCols, perpendicularContent, adjacentSolution, String.valueOf(newContent)));
            }
        }

        return parallelSolutions;
    }

    private Map<Integer, AdjacentSolution> findAdjacentSolutions(
            DictionaryEntry entry, GridRowsCols gridRowsCols, int index, String pattern
    ) {
        Map<Integer, AdjacentSolution> adjacentSolutions = new HashMap<>();
        char[] charsArray = gridRowsCols.getContent().toCharArray();

        for (int i = 0; i < pattern.length(); i++) {
            // if the spot on the grid was not filled before, check its surrounding
            if (charsArray[i + index] == '.') {
                // find the GridContent that is perpendicular from the solution on the current character
                GridRowsCols perpendicularContent = findPerpendicularContent(gridRowsCols, i + index);

                // if there is no letters in this grid content, then there will not be any adjacent solution here
                if (perpendicularContent != null) {
                    // get the string formed by the players letter and the surrounding words
                    String adjacentSolutionString = findOverlappingString(
                            perpendicularContent, entry.getWord().charAt(i), gridRowsCols.getIndex());

                    // if there is at least 2 letters, make sure they form a valid word
                    // else reject this solution
                    if (adjacentSolutionString.length() > 1) {
                        AdjacentSolution adjacentSolution =
                                findValidAdjacentSolution(adjacentSolutionString, entry);
                        if (adjacentSolution == null)
                            return null;
                        else
                            adjacentSolutions.put(i, adjacentSolution);
                    }
                }
            }
        }

        return adjacentSolutions;
    }

    private AdjacentSolution findValidAdjacentSolution(
            String adjacentSolutionString, DictionaryEntry entry
    ) {
        AdjacentSolution adjacentSolution = null;
        DictionaryEntry adjacentEntry = DictionnaryEntriesFinder.findEntryByWord(adjacentSolutionString, entries);

        if (adjacentEntry != null) {
            adjacentSolution = new AdjacentSolution(adjacentEntry);
        }

        return adjacentSolution;
    }

    private GridRowsCols findPerpendicularContent(GridRowsCols gridRowsCols, int index) {
        //find the gridContent that intersect at this particular index
        return this.gridRowsCols.stream()
                .filter(c -> c.isVertical() != gridRowsCols.isVertical() && c.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    private String findOverlappingString(GridRowsCols gridRowsCols, char currentChar, int index) {
        // Build a string with all the characters directly next to the given letters on the grid
        char[] contentCharsArray = gridRowsCols.getContent().toCharArray();
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
