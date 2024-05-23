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

        // Creer la liste des entries qui sont deja sur la grid
        List<GridEntry> gridEntries = createGridEntries(gridRowsCols);

        // Creer la liste des patterns pour chaque GridRowsCols
        Map<GridRowCol, Map<Integer, List<Pattern>>> patternsByGridRowsCols = new HashMap<>();
        if (gridEntries.isEmpty()) {
            Map<Integer, List<Pattern>> untestedPatterns =
                    gridRowsCols.get(7).testPatterns(grid.getPlayerLetters(), true);
            Map<Integer, List<Pattern>> validPatternsByIndex = new HashMap<>();

            for (Map.Entry<Integer, List<Pattern>> entry : untestedPatterns.entrySet()) {
                Integer index = entry.getKey();
                List<Pattern> patterns = entry.getValue();
                List<Pattern> validPatterns = new ArrayList<>();

                for (Pattern pattern : patterns) {
                    if (index <= 7 && index + pattern.getRegex().length() > 7) {
                        validPatterns.add(pattern);
                    }

                    if (!validPatterns.isEmpty())
                        validPatternsByIndex.put(index, validPatterns);
                }
            }

            patternsByGridRowsCols.put(gridRowsCols.get(7), validPatternsByIndex);
        } else {
            for (GridRowCol gridRowCol : gridRowsCols) {
                patternsByGridRowsCols.put(gridRowCol, gridRowCol.testPatterns(grid.getPlayerLetters()));
            }
        }

        // Recuperer tous les patterns uniques
        Set<Pattern> uniquePatterns = findUniquePattern(patternsByGridRowsCols);

        // Pour chaque pattern unique, trouver les mots qui sont possibles
        // Creer un nouveau thread pour chaque pattern
        Map<Pattern, Set<DictionaryEntry>> entriesByPattern =
                findWordForEachUniquePattern(grid, uniquePatterns, entries);

        // Assign every list of entries to their matching patterns by grid content
        // Create threads for every gridRowCol
        Map<GridRowCol, Map<Integer, Map<Pattern, Set<DictionaryEntry>>>> entriesByGridRowsCols =
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

    private Set<Pattern> findUniquePattern(Map<GridRowCol, Map<Integer, List<Pattern>>> patternsByGridRowsCols) {
        Set<Pattern> uniquePatterns = new HashSet<>();

        for (Map<Integer, List<Pattern>> map : patternsByGridRowsCols.values()) {
            for (List<Pattern> patternsByIndex : map.values()) {
                uniquePatterns.addAll(patternsByIndex);
            }
        }

        return uniquePatterns;
    }

    private Map<Pattern, Set<DictionaryEntry>> findWordForEachUniquePattern(
            GridDTO grid, Set<Pattern> uniquePatterns, Set<DictionaryEntry> entries
    ) {
        Map<Pattern, Set<DictionaryEntry>> entriesByPattern = new HashMap<>();
        Map<String, Integer> playerLettersMap = LettersCounter.lettersCountMap(grid.getPlayerLetters());
        Queue<Thread> entriesByPatternThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForEntriesByPattern = new ThreadGroup("entryByPattern");

        for (Pattern pattern : uniquePatterns) {
            Thread thread = new Thread(threadGroupForEntriesByPattern, () -> {
                Set<DictionaryEntry> entriesForPattern = DictionnaryEntriesFinder.findEntriesByPattern(
                        pattern.getRegex(), playerLettersMap, entries
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

    private Map<GridRowCol, Map<Integer, Map<Pattern, Set<DictionaryEntry>>>> setEntriesListByMatchingPatterns(
            Map<GridRowCol, Map<Integer, List<Pattern>>> patternsByGridRowsCols,
            Map<Pattern, Set<DictionaryEntry>> entriesByPattern
    ) {
        Map<GridRowCol, Map<Integer, Map<Pattern, Set<DictionaryEntry>>>> entriesByGridRowsCols = new HashMap<>();
        Queue<Thread> patternByGridContentThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForPatternByGridContent = new ThreadGroup("patternByGridContent");

        for (Map.Entry<GridRowCol, Map<Integer, List<Pattern>>> entry : patternsByGridRowsCols.entrySet()) {
            Thread thread = new Thread(threadGroupForPatternByGridContent, () -> {
                GridRowCol gridRowCol = entry.getKey();
                Map<Integer, List<Pattern>> patternsMap = entry.getValue();
                Map<Integer, Map<Pattern, Set<DictionaryEntry>>> entriesByIndex = new HashMap<>();

                for (Map.Entry<Integer, List<Pattern>> patternsEntry : patternsMap.entrySet()) {
                    Integer index = patternsEntry.getKey();
                    List<Pattern> patterns = patternsEntry.getValue();
                    Map<Pattern, Set<DictionaryEntry>> entriesMap = new HashMap<>();

                    for (Pattern pattern : patterns) {
                        entriesMap.put(pattern, entriesByPattern.get(pattern));
                    }

                    entriesByIndex.put(index, entriesMap);
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
            Map<GridRowCol, Map<Integer, Map<Pattern, Set<DictionaryEntry>>>> entriesByGridRowsCols,
            List<GridEntry> gridEntries
    ) {
        Set<Solution> unfilteredSolutions = new HashSet<>();
        Queue<Thread> solutionsBuildingThreads = new ArrayDeque<>();
        ThreadGroup threadGroupForSolutionsBuilding = new ThreadGroup("solutionsBuilding");
        for (Map.Entry<GridRowCol, Map<Integer, Map<Pattern, Set<DictionaryEntry>>>> mapEntry : entriesByGridRowsCols.entrySet()) {
            GridRowCol gridRowCol = mapEntry.getKey();
            Map<Integer, Map<Pattern, Set<DictionaryEntry>>> map = mapEntry.getValue();

            for (Map.Entry<Integer, Map<Pattern, Set<DictionaryEntry>>> entriesEntry : map.entrySet()) {
                Integer index = entriesEntry.getKey();
                Map<Pattern, Set<DictionaryEntry>> entriesMap = entriesEntry.getValue();
                int y = gridRowCol.isVertical() ? index : gridRowCol.getIndex();
                int x = gridRowCol.isVertical() ? gridRowCol.getIndex() : index;

                for (Map.Entry<Pattern, Set<DictionaryEntry>> entry : entriesMap.entrySet()) {
                    Pattern pattern = entry.getKey();
                    Set<DictionaryEntry> entries = entry.getValue();

                    for (DictionaryEntry dictionaryEntry : entries) {
                        Thread thread = new Thread(threadGroupForSolutionsBuilding, () -> {
                            Map<Integer, AdjacentSolution> adjacentSolutions = new HashMap<>();

                            for (int i = 0; i < dictionaryEntry.getWord().length(); i++) {
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
                                String adjacentWord = beforeString + dictionaryEntry.getWord().charAt(i) + afterString;

                                if (adjacentWord.length() > 1) {
                                    AdjacentSolution adjacentSolution = AdjacentSolution.builder()
                                            .word(adjacentWord)
                                            .build();

                                    adjacentSolution.assignBlankTiles(beforeEntry, afterEntry);
                                    adjacentSolutions.put(i, adjacentSolution);
                                }
                            }

                            Solution solution = Solution.builder()
                                    .entry(dictionaryEntry)
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
                if (entries.stream().anyMatch(e -> e.getWord().equals(word))) {
                    synchronized (validAdjacentWords) {
                        validAdjacentWords.add(word);
                    }
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
        Map<String, Integer> lettersMap = LettersCounter.lettersCountMap(grid.getPlayerLetters());
        boolean isJokersPresent = lettersMap.containsKey("#");

        for (Solution solution : validSolutions) {
            Thread thread = new Thread(threadGroupForPointsCalculation, () -> {
                List<String> jokers = new ArrayList<>();

                if (isJokersPresent) {
                    Map<String, Integer> wordMap = solution.getEntry().getLetters();

                    for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
                        String letter = entry.getKey();
                        int repeatCount = entry.getValue() - lettersMap.getOrDefault(letter, 0);

                        for (int i = 0; i < repeatCount; i++) {
                            jokers.add(letter);
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
