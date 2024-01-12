package com.marcblais.scrabbleapi.dto;

import java.util.*;
import java.util.stream.Collectors;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;
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
        Set<String> uniqueContentOnGrid = gridContents.stream().map(GridContent::getContent).collect(Collectors.toSet());

        for (String content : uniqueContentOnGrid) {
            DictionnaryEntriesFinder entriesFinder =
                    new DictionnaryEntriesFinder(entries, grid.getPlayerLetters(), content);

            List<DictionaryEntry> entriesForContent = entriesFinder.getEntries();

            for (DictionaryEntry entry : entriesForContent) {
                // Find where the grid content can overlap over the word
                List<Integer> indexes = findIndexesOfSubstring(content, entry);

                // find every solutions that fit over the content on the grid
                List<GridContent> contentToTest =
                        gridContents.stream().filter(c -> c.getContent().equals(content)).toList();
                solutions.addAll(findSolutionsForEntry(contentToTest, entry, indexes));
            }
        }

        return solutions;
    }

    private List<Integer> findIndexesOfSubstring(String content, DictionaryEntry entry) {
        List<Integer> indexes = new ArrayList<>();
        int index = 0;

        // Find the last index of the grid content in the word
        int lastIndex = entry.getWord().lastIndexOf(content);

        // Find every indexes of the grid content in the word and add them to the list
        while (index <= lastIndex) {
            index = entry.getWord().indexOf(content, index);
            indexes.add(index);
            index ++;
        }

        return indexes;
    }

    private List<Solution> findSolutionsForEntry(List<GridContent> contentToTest, DictionaryEntry entry, List<Integer> indexes) {
        List<Solution> solutions = new ArrayList<>();

        for (GridContent content : contentToTest) {
            for (int index : indexes) {
                // For every possible overlap of a single word over the grid content, find what solutions really fits
                Solution solution = FindEntryThatFitOverContent(content, entry, index);

                if (solution != null) {
                    solutions.add(solution);
                }
            }
        }

        return solutions;
    }

    private Solution FindEntryThatFitOverContent(GridContent content, DictionaryEntry entry, int positionInContent) {
        Solution solution = null;

        if (content.isVertical())
            if (isVerticalFit(content, entry, positionInContent)) {
                List<Solution> adjacentSolutions = findHorizontalAdjacentSolutions(content, entry, positionInContent);

                if (adjacentSolutions != null) {
                    solution = new Solution(
                            entry,
                            content.getX(),
                            content.getY() - positionInContent,
                            content.isVertical(),
                            0,
                            adjacentSolutions,
                            content
                    );
                }
            }
        else
            if (isHorizontalFit(content, entry, positionInContent)) {
                List<Solution> adjacentSolutions = findVerticalAdjacentSolutions(content, entry, positionInContent);

                if (adjacentSolutions != null) {
                    solution = new Solution(
                            entry,
                            content.getX() - positionInContent,
                            content.getY(),
                            content.isVertical(),
                            0,
                            null,
                            content
                    );
                }
            }

        return solution;
    }

    private boolean isVerticalFit(GridContent content, DictionaryEntry entry, int positionInContent) {
        int firstLetterY = content.getY() - positionInContent;
        int lastLetterY = firstLetterY + entry.getWord().length();

        if (firstLetterY < 0)
            return false;

        if (lastLetterY > grid.getGrid().length - 1)
            return false;

        if (firstLetterY > 0 && !grid.getGrid()[firstLetterY - 1][content.getX()].isBlank())
            return false;

        if (lastLetterY < grid.getGrid().length - 2 && !grid.getGrid()[lastLetterY + 1][content.getX()].isBlank())
            return false;

        return true;
    }

    private boolean isHorizontalFit(GridContent content, DictionaryEntry entry, int positionInContent) {
        int firstLetterX = content.getX() - positionInContent;
        int lastLetterX = content.getX() - positionInContent + entry.getWord().length();

        if (firstLetterX < 0)
            return false;

        if (lastLetterX < grid.getGrid().length - 1)
            return false;

        if (firstLetterX > 0 && !grid.getGrid()[content.getY()][firstLetterX - 1].isBlank())
            return false;

        if (lastLetterX < grid.getGrid().length - 2 && !grid.getGrid()[content.getY()][lastLetterX + 1].isBlank())
            return false;

        return true;
    }

    private List<Solution> findHorizontalAdjacentSolutions(
            GridContent content, DictionaryEntry entry, int positionInContent
    ) {
        List<Solution> adjacentSolutions = new ArrayList<>();
        int firstLetterY = content.getY() - positionInContent;
        int x = content.getX();

        for (int i = 0; i < entry.getWord().length(); i++) {
            int y = i + firstLetterY;

            if (grid.getGrid()[y][x].isEmpty()) {
                GridContent adjacentContentLeft = gridContents.stream().filter(c -> {
                    return c.getY() == y && c.getX() + c.getContent().length() == x;
                }).findFirst().orElse(new GridContent());

                GridContent adjacentContentRight = gridContents.stream().filter(c -> {
                    return c.getY() == y && c.getX() == x + 1;
                }).findFirst().orElse(new GridContent());

                String contentToTest = adjacentContentLeft.getContent() +
                        entry.getWord().charAt(i) + adjacentContentRight.getContent();

                if (contentToTest.length() > 1) {
                    DictionaryEntry adjacentEntry = entries.stream().filter(e -> {
                        return e.getWord().equals(contentToTest);
                    }).findFirst().orElse(null);

                    if (adjacentEntry != null) {
                        Solution adjacentSolution = new Solution(
                                entry,
                                x - adjacentContentLeft.getContent().length(),
                                y,
                                false,
                                0,
                                null,
                                null
                        );
                        adjacentSolutions.add(adjacentSolution);
                    } else {
                        return null;
                    }
                }
            }
        }

        return adjacentSolutions;
    }

    private List<Solution> findVerticalAdjacentSolutions(
            GridContent content, DictionaryEntry entry, int positionInContent
    ) {
        List<Solution> adjacentSolutions = new ArrayList<>();
        int firstLetterX = content.getX() - positionInContent;
        int y = content.getY();

        for (int i = 0; i < entry.getWord().length(); i++) {
            int x = i + firstLetterX;

            if (grid.getGrid()[y][x].isEmpty()) {
                GridContent adjacentContentAbove = gridContents.stream().filter(c -> {
                    return c.getX() == x && c.getY() + c.getContent().length() == y;
                }).findFirst().orElse(new GridContent());

                GridContent adjacentContentBelow = gridContents.stream().filter(c -> {
                    return c.getX() == x && c.getY() == y + 1;
                }).findFirst().orElse(new GridContent());

                String contentToTest = adjacentContentAbove.getContent() +
                        entry.getWord().charAt(i) + adjacentContentBelow.getContent();

                if (contentToTest.length() > 1) {
                    DictionaryEntry adjacentEntry = entries.stream().filter(e -> {
                        return e.getWord().equals(contentToTest);
                    }).findFirst().orElse(null);

                    if (adjacentEntry != null) {
                        Solution adjacentSolution = new Solution(
                                entry,
                                x,
                                y - adjacentContentAbove.getContent().length(),
                                false,
                                0,
                                null,
                                null
                        );
                        adjacentSolutions.add(adjacentSolution);
                    } else {
                        return null;
                    }
                }
            }
        }

        return adjacentSolutions;
    }
}
