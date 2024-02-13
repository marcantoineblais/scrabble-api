package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.GridContent;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermutationFinder {
    public static List<Solution> findPermutationOfJokerTiles(
            String jokerLetter,
            DictionaryEntry entry,
            GridContent gridContent,
            Map<Integer, AdjacentSolution> adjacentSolutions,
            int index,
            String pattern
    ) {
        List<Solution> solutions = new ArrayList<>();
        if (jokerLetter.length() == 2) {
            String[] jokerLetters = jokerLetter.split("");
            int i = 0;
            int lastIndex1 = entry.getWord().lastIndexOf(jokerLetters[0]);
            int lastIndex2 = entry.getWord().lastIndexOf(jokerLetters[1]);

            while (lastIndex1 >= 0 && i <= lastIndex1) {
                int j = 0;

                while (lastIndex2 >= 0 && j <= lastIndex2) {
                    i = entry.getWord().indexOf(jokerLetters[0], i);
                    j = entry.getWord().indexOf(jokerLetters[1], j);

                    if (!pattern.substring(i, i + 1).equals(jokerLetters[0]) ||
                            !pattern.substring(j, j + 1).equals(jokerLetters[1])) {
                        Solution solutionWithJoker = new Solution(
                                entry,
                                gridContent,
                                adjacentSolutions,
                                pattern,
                                gridContent.isVertical(),
                                gridContent.isVertical() ? gridContent.getIndex() : index,
                                gridContent.isVertical() ? index : gridContent.getIndex()
                        );

                        solutionWithJoker.getBlankTiles().add(i);
                        solutionWithJoker.getBlankTiles().add(j);
                        solutions.add(solutionWithJoker);
                    }

                    j++;
                }

                i++;
            }
        } else if (jokerLetter.length() == 1) {
            int i = 0;
            int lastIndex = entry.getWord().lastIndexOf(jokerLetter);

            while (lastIndex >= 0 && i <= lastIndex) {
                i = entry.getWord().indexOf(jokerLetter, i);
                if (!pattern.substring(i, i + 1).equals(jokerLetter)) {
                    Solution solutionWithJoker = new Solution(
                            entry,
                            gridContent,
                            adjacentSolutions,
                            pattern,
                            gridContent.isVertical(),
                            gridContent.isVertical() ? gridContent.getIndex() : index,
                            gridContent.isVertical() ? index : gridContent.getIndex()
                    );


                    solutionWithJoker.getBlankTiles().add(i);
                    solutions.add(solutionWithJoker);
                }

                i++;
            }
        } else {
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

        return solutions;
    }
}
