package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.repositories.*;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.LettersCounter;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
import com.marcblais.scrabbleapi.utilities.ThreadsRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolutionService {
    private final DictionaryEntryRepo dictionaryEntryRepo;
    private final PointsRepo pointsRepo;
    private final PlayerRepo playerRepo;

    @Autowired
    public SolutionService(
            DictionaryEntryRepo dictionaryEntryRepo,
            PointsRepo pointsRepo,
            PlayerRepo playerRepo
    ) {
        this.dictionaryEntryRepo = dictionaryEntryRepo;
        this.pointsRepo = pointsRepo;
        this.playerRepo = playerRepo;
    }

    public Set<DictionaryEntry> findWordsByLanguage(Language language) {
        return dictionaryEntryRepo.findByLanguages(language);
    }

    public LettersValue findLettersValueByLanguage(Language language) {
        return pointsRepo.findByLanguage(language);
    }

    public Player findPlayerByUsername(String username) {
        return playerRepo.findById(username).orElse(null);
    }

    public List<Solution> solveGrid(GridDTO grid, Set<DictionaryEntry> entries, LettersValue lettersValue) {
        // Creer la liste des lignes de contenu de la grille
        List<GridRowCol> gridRowsCols = grid.toGridRowColList();

        // Creer la liste des patterns pour chaque GridRowsCols
        Map<GridRowCol, Map<Integer, List<List<String>>>> patternsByGridRowsCols = new HashMap<>();
        if (gridRowsCols.isEmpty()) {
            patternsByGridRowsCols = patternForFirstMove(grid);
        } else {
            for (GridRowCol gridRowCol : gridRowsCols) {
                patternsByGridRowsCols.put(gridRowCol, gridRowCol.testPatterns(grid.getPlayerLetters()));
            }
        }

        // Creer la liste des entries qui sont deja sur la grid
        List<GridEntry> gridEntries = createGridEntries(gridRowsCols);

        // Recuperer tous les patterns uniques
        Set<List<String>> uniquePatterns = findUniquePattern(patternsByGridRowsCols);

        // Pour chaque pattern unique, trouver les mots qui sont possibles
        // Creer un nouveau thread pour chaque pattern
        Map<List<String>, Set<DictionaryEntry>> entriesByPattern =
                findWordForEachUniquePattern(grid, uniquePatterns, entries);

        // Assign every list of entries to their matching patterns by grid content
        // Create threads for every gridRowCol
        Map<GridRowCol, Map<Integer, Set<DictionaryEntry>>> entriesByGridRowsCols =
                setEntriesListByMatchingPatterns(patternsByGridRowsCols, entriesByPattern);

        // For every entries in the grid contents, create a solution object and fill their adjacent solution list
        // Create a new thread for every dictionary entry in the set
        Set<Solution> unfilteredSolutions = findUnfilteredSolutions(entriesByGridRowsCols, gridEntries);

        // Create a set of words containing all the adjacent solutions
        // Create threads for every solution to filter
        Set<String> allAdjacentSolutionsWords = findUniqueAdjacentWords(unfilteredSolutions);

        // Test every word in the set and create a set with every valid words
        // Create a new thread for every word to test
        Set<String> validAdjacentWords = findAllValidAdjacentWords(allAdjacentSolutionsWords, entries);

        // Test every solutions and remove the ones with invalid adjacent entry words
        Set<Solution> validSolutions = unfilteredSolutions.stream()
                .filter(s -> s.getAdjacentSolutions()
                        .values()
                        .stream()
                        .allMatch(as -> validAdjacentWords.contains(as.getWord()))
                ).collect(Collectors.toSet());

        // Calculate the points for every valid solutions and keep the 10 best solutions
        // Create threads to compute the scores
        calculatesPointsForSolutions(validSolutions, grid, lettersValue);

        // Find the 10 best solutions and sort them
        List<Solution> solutions = PointCalculator.getNBestSolutions(validSolutions, 10);
        solutions.sort(Solution::compareTo);
        return solutions;
    }

    private List<GridEntry> createGridEntries(List<GridRowCol> gridRowsCols) {
        List<GridEntry> gridEntries = new ArrayList<>();
        for (GridRowCol gridRowCol : gridRowsCols) {
            gridEntries.addAll(gridRowCol.toGridEntriesList());
        }

        return gridEntries;
    }

    private Map<GridRowCol, Map<Integer, List<List<String>>>> patternForFirstMove(GridDTO grid) {
        Map<GridRowCol, Map<Integer, List<List<String>>>> patternsByGridRowsCols = new HashMap<>();
        Map<Integer, List<List<String>>> patternsByIndex = new HashMap<>();
        int gridMiddle = grid.getGrid().length / 2;
        List<String> content = new ArrayList<>();

        for (int i = 0; i < grid.getGrid().length; i++) {
            content.add(grid.bonusOrLetter(gridMiddle, i));
        }

        GridRowCol gridRowCol = GridRowCol.builder()
                .content(content.toArray(String[]::new))
                .index(gridMiddle)
                .vertical(false)
                .build();

        List<String> playerLetters = grid.getPlayerLetters();

        for (int i = gridMiddle - playerLetters.size() + 1; i < playerLetters.size(); i++) {
            List<List<String>> patterns = new ArrayList<>();
            List<String> pattern = new ArrayList<>();
            int j = i;

            while (j < grid.getGrid().length && j < playerLetters.size() + i) {
                pattern.add(grid.bonusOrLetter(gridMiddle, j));

                if (j >= gridMiddle)
                    patterns.add(List.copyOf(pattern));

                j++;
            }

            patternsByIndex.put(i, patterns);
        }

        patternsByGridRowsCols.put(gridRowCol, patternsByIndex);
        return patternsByGridRowsCols;
    }

    private Set<List<String>> findUniquePattern(Map<GridRowCol, Map<Integer, List<List<String>>>> patternsByGridRowsCols) {
        Set<List<String>> uniquePatterns = new HashSet<>();
        for (Map<Integer, List<List<String>>> map : patternsByGridRowsCols.values()) {
            for (List<List<String>> patternsByIndex : map.values()) {
                Set<List<String>> patternsWithoutBonus = new HashSet<>();

                for (List<String> pattern : patternsByIndex) {
                    List<String> patternWithoutBonus = new ArrayList<>();

                    for (String letter : pattern) {
                        patternWithoutBonus.add(letter.replaceAll("[0-4]", "."));
                    }

                    patternsWithoutBonus.add(patternWithoutBonus);
                }

                uniquePatterns.addAll(patternsWithoutBonus);
            }
        }

        return uniquePatterns;
    }

    private Map<List<String>, Set<DictionaryEntry>> findWordForEachUniquePattern(
            GridDTO grid, Set<List<String>> uniquePatterns, Set<DictionaryEntry> entries
    ) {
        Map<List<String>, Set<DictionaryEntry>> entriesByPattern = new HashMap<>();
        Queue<Thread> entriesByPatternThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForEntriesByPattern = new ThreadGroup("entryByPattern");

        for (List<String> pattern : uniquePatterns) {
            Thread thread = new Thread(threadGroupForEntriesByPattern, () -> {
                Set<DictionaryEntry> entriesForPattern = DictionnaryEntriesFinder.findEntriesByPattern(
                        pattern, grid.getPlayerLetters(), entries
                );

                synchronized (entriesByPattern) {
                    entriesByPattern.put(pattern, entriesForPattern);
                }
            });

            entriesByPatternThreads.add(thread);
        }

        try {
            ThreadsRunner.runThreads(entriesByPatternThreads, threadGroupForEntriesByPattern);
        } catch (Exception ignored) {}

        return entriesByPattern;
    }

    private Map<GridRowCol, Map<Integer, Set<DictionaryEntry>>> setEntriesListByMatchingPatterns(
            Map<GridRowCol, Map<Integer, List<List<String>>>> patternsByGridRowsCols,
            Map<List<String>, Set<DictionaryEntry>> entriesByPattern
    ) {
        Map<GridRowCol, Map<Integer, Set<DictionaryEntry>>> entriesByGridRowsCols = new HashMap<>();
        Queue<Thread> patternByGridContentThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForPatternByGridContent = new ThreadGroup("patternByGridContent");
        for (GridRowCol gridRowCol : patternsByGridRowsCols.keySet()) {
            Thread thread = new Thread(threadGroupForPatternByGridContent, () -> {
                Map<Integer, Set<DictionaryEntry>> entriesByIndex = new HashMap<>();
                Map<Integer, List<List<String>>> map = patternsByGridRowsCols.get(gridRowCol);

                for (Integer index : map.keySet()) {
                    Set<DictionaryEntry> entriesList = new HashSet<>();

                    for (List<String> pattern : map.get(index)) {
                        List<String> patternWithoutBonus = new ArrayList<>();
                        for (String letter : pattern) {
                            patternWithoutBonus.add(letter.replaceAll("[0-4]", "."));
                        }

                        entriesList.addAll(entriesByPattern.get(patternWithoutBonus));
                    }

                    entriesByIndex.put(index, entriesList);
                }

                synchronized (entriesByGridRowsCols) {
                    entriesByGridRowsCols.put(gridRowCol, entriesByIndex);
                }
            });

            patternByGridContentThreads.add(thread);
        }

        try {
            ThreadsRunner.runThreads(patternByGridContentThreads, threadGroupForPatternByGridContent);
        } catch (Exception ignored) {}

        return entriesByGridRowsCols;
    }

    private Set<Solution> findUnfilteredSolutions(
            Map<GridRowCol, Map<Integer, Set<DictionaryEntry>>> entriesByGridRowsCols,
            List<GridEntry> gridEntries
    ) {
        Set<Solution> unfilteredSolutions = new HashSet<>();
        Queue<Thread> solutionsBuildingThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForSolutionsBuilding = new ThreadGroup("solutionsBuilding");
        for (GridRowCol gridRowCol : entriesByGridRowsCols.keySet()) {
            Map<Integer, Set<DictionaryEntry>> map = entriesByGridRowsCols.get(gridRowCol);

            for (Integer index : map.keySet()) {
                int y = gridRowCol.isVertical() ? index : gridRowCol.getIndex();
                int x = gridRowCol.isVertical() ? gridRowCol.getIndex() : index;

                for (DictionaryEntry entry : map.get(index)) {
                    Thread thread = new Thread(threadGroupForSolutionsBuilding, () -> {
                        Map<Integer, AdjacentSolution> adjacentSolutions = new HashMap<>();

                        for (int i = 0; i < entry.getWord().length(); i++) {
                            int offSetY = gridRowCol.isVertical() ? i : 0;
                            int offSetX = gridRowCol.isVertical() ? 0 : i;

                            GridEntry beforeEntry = gridEntries.stream()
                                    .filter(e -> e.isBefore(y + offSetY, x + offSetX, gridRowCol.isVertical()))
                                    .findFirst()
                                    .orElse(null);

                            GridEntry afterEntry = gridEntries.stream()
                                    .filter(e -> e.isAfter(y + offSetY, x + offSetX, gridRowCol.isVertical()))
                                    .findFirst()
                                    .orElse(null);

                            String beforeString = beforeEntry == null ? "" : beforeEntry.getEntry();
                            String afterString = afterEntry == null ? "" : afterEntry.getEntry();

                            String adjacentWord = beforeString + entry.getWord().charAt(i) + afterString;

                            if (adjacentWord.length() > 1) {
                                AdjacentSolution adjacentSolution = new AdjacentSolution(adjacentWord, 0, new ArrayList<>());
                                adjacentSolution.assignBlankTiles(beforeEntry, afterEntry);
                                adjacentSolutions.put(i, adjacentSolution);
                            }
                        }

                        String[] pattern = new String[entry.getWord().length()];
                        System.arraycopy(gridRowCol.getContent(), index, pattern, 0, pattern.length);

                        Solution solution = Solution.builder()
                                .entry(entry)
                                .gridRowCol(gridRowCol)
                                .adjacentSolutions(adjacentSolutions)
                                .pattern(pattern)
                                .vertical(gridRowCol.isVertical())
                                .y(y)
                                .x(x)
                                .build();

                        solution.assignBlankTiles();

                        synchronized (unfilteredSolutions) {
                            unfilteredSolutions.add(solution);
                        }
                    });

                    solutionsBuildingThreads.add(thread);
                }
            }
        }
        try {
            ThreadsRunner.runThreads(solutionsBuildingThreads, threadGroupForSolutionsBuilding);
        } catch (Exception ignored) {}

        return unfilteredSolutions;
    }

    private Set<String> findUniqueAdjacentWords(Set<Solution> unfilteredSolutions) {
        Set<String> allAdjacentWords = new HashSet<>();
        Queue<Thread> solutionFilteringThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForSolutionFiltering = new ThreadGroup("solutionFiltering");
        for (Solution solution : unfilteredSolutions) {
            Thread thread = new Thread(threadGroupForSolutionFiltering, () -> {
                for (AdjacentSolution adjacentSolution : solution.getAdjacentSolutions().values()) {
                    synchronized (allAdjacentWords) {
                        allAdjacentWords.add(adjacentSolution.getWord());
                    }
                }
            });

            solutionFilteringThreads.add(thread);
        }

        try {
            ThreadsRunner.runThreads(solutionFilteringThreads, threadGroupForSolutionFiltering);
        } catch (Exception ignored) {}

        return allAdjacentWords;
    }

    private Set<String> findAllValidAdjacentWords(Set<String> allAdjacentSolutionsWords, Set<DictionaryEntry> entries) {
        Set<String> validAdjacentWords = new HashSet<>();
        Queue<Thread> adjacentSolutionsTestingThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForTestingAdjacentSolutions = new ThreadGroup("adjacentSolutionTest");

        for (String word : allAdjacentSolutionsWords) {
            Thread thread = new Thread(threadGroupForTestingAdjacentSolutions , () -> {
                if (entries.stream().anyMatch(e -> e.getWord().equals(word)))
                    synchronized (validAdjacentWords) {
                        validAdjacentWords.add(word);
                    }
            });

            adjacentSolutionsTestingThreads.add(thread);
        }

        try {
            ThreadsRunner.runThreads(adjacentSolutionsTestingThreads, threadGroupForTestingAdjacentSolutions);
        } catch (Exception ignored) {}

        return validAdjacentWords;
    }

    private void calculatesPointsForSolutions(Set<Solution> validSolutions, GridDTO grid, LettersValue lettersValue) {
        Queue<Thread> pointsCalculationThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForPointsCalculation = new ThreadGroup("pointsCalculation");
        List<String> playerLetters = grid.getPlayerLetters();
        Map<String, Integer> lettersMap = LettersCounter.lettersCountMap(playerLetters);

        for (Solution solution : validSolutions) {
            Thread thread = new Thread(threadGroupForPointsCalculation, () -> {
                List<String> jokers = new ArrayList<>();

                if (lettersMap.containsKey("#")) {
                    Map<String, Integer> wordMap = solution.getEntry().getLetters();

                    for (String letter : wordMap.keySet()) {
                        String joker = letter.repeat(wordMap.get(letter) - lettersMap.getOrDefault(letter, 0));
                        if (!joker.isEmpty()) {
                            jokers.addAll(List.of(joker.split("")));
                        }
                    }
                }

                PointCalculator.calculatePointsForSolutions(solution, lettersValue, jokers);
            });

            pointsCalculationThreads.add(thread);
        }

        try {
            ThreadsRunner.runThreads(pointsCalculationThreads, threadGroupForPointsCalculation);
        } catch (Exception ignored) {}
    }
}
