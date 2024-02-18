package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.Bonus;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.LettersValue;

import java.util.*;

public class PointCalculator {
    public static void calculatePointsForSolutions(Solution solution, LettersValue lettersValue) {
        solution.setPoints(calculateBasePoint(solution.getEntry().getWord(), lettersValue));
        calculateAjdacentSolutionBasePoint(solution, lettersValue);
        calculateBonus(solution, lettersValue);
    }

    private static int calculateBasePoint(String word, LettersValue lettersValue) {
        int points = 0;

        for (String letter : word.split("")) {
            points += lettersValue.getPoints().getOrDefault(letter, 0);
        }

        return points;
    }

    private static void calculateAjdacentSolutionBasePoint(Solution solution, LettersValue lettersValue) {
        for (AdjacentSolution adjacentSolution : solution.getAdjacentSolutions().values()) {
            adjacentSolution.setPoints(calculateBasePoint(adjacentSolution.getWord(), lettersValue));
        }
    }

    private static void calculateBonus(Solution solution, LettersValue lettersValue) {
        Map<String, Integer> pointMap = lettersValue.getPoints();
        String[] letters = solution.getEntry().getWord().split("");
        String[] bonus = solution.getPattern().split("");
        int wordMultiplier = 1;

        for (int i = 0; i < letters.length; i++) {
            AdjacentSolution adjacentSolution = solution.getAdjacentSolutions().get(i);
            int points = pointMap.getOrDefault(letters[i], 0);

            switch (bonus[i]) {
                case Bonus.DOUBLE_LETTER:
                    solution.setPoints(solution.getPoints() + points);

                    if (adjacentSolution != null)
                        adjacentSolution.setPoints(adjacentSolution.getPoints() + points);

                    break;

                case Bonus.TRIPLE_LETTER:
                    solution.setPoints(solution.getPoints() + (points * 2));

                    if (adjacentSolution != null)
                        adjacentSolution.setPoints(adjacentSolution.getPoints() + (points * 2));

                    break;

                case Bonus.DOUBLE_WORD:
                    wordMultiplier *= 2;

                    if (adjacentSolution != null)
                        adjacentSolution.setPoints(adjacentSolution.getPoints() * 2);

                    break;

                case Bonus.TRIPLE_WORD:
                    wordMultiplier *= 3;

                    if (adjacentSolution != null)
                        adjacentSolution.setPoints(adjacentSolution.getPoints() * 3);

                    break;
            }
        }

        solution.setPoints(solution.getPoints() * wordMultiplier);
        for (AdjacentSolution adjacentSolution : solution.getAdjacentSolutions().values()) {
            solution.setPoints(solution.getPoints() + adjacentSolution.getPoints());
        }

        if (solution.getPattern().replaceAll("[A-Z]", "").length() == 7)
            solution.setPoints(solution.getPoints() + pointMap.get("*"));
    }

    public static List<Solution> getNBestSolutions(Set<Solution> solutions, int n) {
        List<Solution> bestSolutions = new ArrayList<>();
        Solution minSolution = null;

        for (Solution solution : solutions) {
            if (bestSolutions.size() < n) {
                bestSolutions.add(solution);
            } else if (solution.compareTo(minSolution) < 0) {
                bestSolutions.remove(minSolution);
                bestSolutions.add(solution);
                minSolution = bestSolutions.stream().max(Solution::compareTo).orElse(null);
            }
        }

        bestSolutions.remove(minSolution);
        return bestSolutions;
    }
}
