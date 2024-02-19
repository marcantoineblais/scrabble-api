package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.Bonus;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.LettersValue;

import java.util.*;

public class PointCalculator {
    public static void calculatePointsForSolutions(Solution solution, LettersValue lettersValue, String jokers) {
        chooseBestPositionForJokers(solution, jokers);
        solution.setPoints(calculateBasePoint(solution.getBlankTiles(), solution.getEntry().getWord(), lettersValue));
        calculateAjdacentSolutionBasePoint(solution, lettersValue);
        calculateBonus(solution, lettersValue);
    }

    private static void chooseBestPositionForJokers(Solution solution, String jokers) {
        String word = solution.getEntry().getWord();
        String[] bonus = solution.getPattern().split("");

        if (jokers.isBlank())
            return;

        for (String joker : jokers.split("")) {
            List<Integer> availablePositions = new ArrayList<>();
            Integer bestPosition = null;
            int bestPositionScore = 0;

            int lastIndex = word.lastIndexOf(joker);
            int i = 0;

            while (i >= 0 && i <= lastIndex) {
                i = word.indexOf(joker, i);
                availablePositions.add(i);
                i++;
            }

            for (Integer index : availablePositions) {
                if (bonus[index].matches("[A-Z]") || solution.getBlankTiles().contains(index))
                    continue;

                int currentPositionScore;

                switch (bonus[index]) {
                    case Bonus.DOUBLE_LETTER -> currentPositionScore = 1;
                    case Bonus.TRIPLE_LETTER -> currentPositionScore = 2;
                    default -> currentPositionScore = 3;
                }

                if (solution.getAdjacentSolutions().containsKey(index) && currentPositionScore < 3)
                    currentPositionScore = 0;

                if (bestPosition == null || currentPositionScore > bestPositionScore) {
                    bestPosition = index;
                    bestPositionScore = currentPositionScore;
                }
            }

            solution.getBlankTiles().add(bestPosition);
        }
    }

    private static int calculateBasePoint(List<Integer> blankTiles, String word, LettersValue lettersValue) {
        String[] letters = word.split("");
        int points = 0;

        for (int i = 0; i < letters.length; i++) {
            if (!blankTiles.contains(i))
                points += lettersValue.getPoints().getOrDefault(letters[i], 0);
        }

        return points;
    }

    private static void calculateAjdacentSolutionBasePoint(Solution solution, LettersValue lettersValue) {
        for (AdjacentSolution adjacentSolution : solution.getAdjacentSolutions().values()) {
            adjacentSolution.setPoints(
                    calculateBasePoint(adjacentSolution.getBlankTiles(), adjacentSolution.getWord(), lettersValue)
            );
        }
    }

    private static void calculateBonus(Solution solution, LettersValue lettersValue) {
        Map<String, Integer> pointMap = lettersValue.getPoints();
        String[] letters = solution.getEntry().getWord().split("");
        String[] bonus = solution.getPattern().split("");
        int wordMultiplier = 1;

        for (int i = 0; i < letters.length; i++) {
            AdjacentSolution adjacentSolution = solution.getAdjacentSolutions().get(i);
            int points = !solution.getBlankTiles().contains(i) ? pointMap.getOrDefault(letters[i], 0) : 0;

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
