package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.GridRowCol;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermutationFinder {
    public static List<Solution> findPermutationOfJokerTiles(
            String[] jokerLetters,
            DictionaryEntry entry,
            GridRowCol gridRowCol,
            Map<Integer, AdjacentSolution> adjacentSolutions,
            int index,
            String[] pattern
    ) {
        List<Solution> solutions = new ArrayList<>();
        if (jokerLetters.length == 2) {
            int i = 0;
            int lastIndex1 = entry.getWord().lastIndexOf(jokerLetters[0]);
            int lastIndex2 = entry.getWord().lastIndexOf(jokerLetters[1]);

            while (lastIndex1 >= 0 && i <= lastIndex1) {
                int j = 0;

                while (lastIndex2 >= 0 && j <= lastIndex2) {
                    i = entry.getWord().indexOf(jokerLetters[0], i);
                    j = entry.getWord().indexOf(jokerLetters[1], j);

                    if (!pattern[i].equals(jokerLetters[0]) ||
                            !pattern[j].equals(jokerLetters[1])) {
                        Solution solutionWithJoker = Solution.builder()
                                .entry(entry)
                                .gridRowCol(gridRowCol)
                                .adjacentSolutions(adjacentSolutions)
                                .pattern(pattern)
                                .vertical(gridRowCol.isVertical())
                                .y(gridRowCol.isVertical() ? gridRowCol.getIndex() : index)
                                .x(gridRowCol.isVertical() ? index : gridRowCol.getIndex())
                                .build();

                        solutionWithJoker.getBlankTiles().add(i);
                        solutionWithJoker.getBlankTiles().add(j);
                        solutions.add(solutionWithJoker);
                    }

                    j++;
                }

                i++;
            }
        } else if (jokerLetters.length == 1) {
            int i = 0;
            int lastIndex = entry.getWord().lastIndexOf(jokerLetters[0]);

            while (lastIndex >= 0 && i <= lastIndex) {
                i = entry.getWord().indexOf(jokerLetters[0], i);
                if (!pattern[i].equals(jokerLetters[0])) {
                    Solution solutionWithJoker = Solution.builder()
                            .entry(entry)
                            .gridRowCol(gridRowCol)
                            .adjacentSolutions(adjacentSolutions)
                            .pattern(pattern)
                            .vertical(gridRowCol.isVertical())
                            .y(gridRowCol.isVertical() ? gridRowCol.getIndex() : index)
                            .x(gridRowCol.isVertical() ? index : gridRowCol.getIndex())
                            .build();

                    solutionWithJoker.getBlankTiles().add(i);
                    solutions.add(solutionWithJoker);
                }

                i++;
            }
        } else {
            Solution solution = Solution.builder()
                    .entry(entry)
                    .gridRowCol(gridRowCol)
                    .adjacentSolutions(adjacentSolutions)
                    .pattern(pattern)
                    .vertical(gridRowCol.isVertical())
                    .y(gridRowCol.isVertical() ? gridRowCol.getIndex() : index)
                    .x(gridRowCol.isVertical() ? index : gridRowCol.getIndex())
                    .build();
            solutions.add(solution);
        }

        return solutions;
    }
}
