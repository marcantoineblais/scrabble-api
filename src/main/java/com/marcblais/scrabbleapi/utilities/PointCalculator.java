package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.Bonus;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.LettersValue;

import java.util.*;

public class PointCalculator {
    public static void calculatePointsForSolutions(Solution solution, LettersValue lettersValue, List<String> jokers) {
        chooseBestPositionForJokers(solution, jokers);
        solution.setPoints(calculateBasePoint(solution.getBlankTiles(), solution.getEntry().getWord(), lettersValue));
        calculateAjdacentSolutionBasePoint(solution, lettersValue);
        calculateBonus(solution, lettersValue);
    }

    private static void chooseBestPositionForJokers(Solution solution, List<String> jokers) {
        String word = solution.getEntry().getWord();
        String[] bonus = solution.getPattern();

        for (String joker : jokers) {
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
                if (!bonus[index].matches("[0-4.]") || solution.getBlankTiles().contains(index))
                    continue;

                int currentPositionScore = getPositionScore(solution, index, bonus);

                if (bestPosition == null || currentPositionScore > bestPositionScore) {
                    bestPosition = index;
                    bestPositionScore = currentPositionScore;
                }
            }

            solution.getBlankTiles().add(bestPosition);
        }
    }

    private static int getPositionScore(Solution solution, Integer index, String[] bonus) {
        int currentPositionScore;

        switch (bonus[index]) {
            case Bonus.DOUBLE_LETTER, Bonus.DOUBLE_WORD -> currentPositionScore = 2;
            case Bonus.TRIPLE_LETTER, Bonus.TRIPLE_WORD -> currentPositionScore = 1;
            default -> currentPositionScore = 3;
        }

        if (solution.getAdjacentSolutions().containsKey(index)) {
            currentPositionScore -= 1;
        }
        return currentPositionScore;
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
        for (Integer index : solution.getAdjacentSolutions().keySet()) {
            AdjacentSolution adjacentSolution = solution.getAdjacentSolutions().get(index);
            adjacentSolution.setPoints(
                    calculateBasePoint(adjacentSolution.getBlankTiles(), adjacentSolution.getWord(), lettersValue)
            );

            if (solution.getBlankTiles().contains(index)) {
                String letter = solution.getEntry().getWord().substring(index, index + 1);
                int pointsToRemove = lettersValue.getPoints().getOrDefault(letter, 0);
                adjacentSolution.setPoints(adjacentSolution.getPoints() - pointsToRemove);
            }
        }
    }

    private static void calculateBonus(Solution solution, LettersValue lettersValue) {
        Map<String, Integer> pointMap = lettersValue.getPoints();
        String[] letters = solution.getEntry().getWord().split("");
        String[] bonus = solution.getPattern();
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

        if (solution.getEntry().getWord().length()
                - String.join("", solution.getPattern()).replaceAll("[0-4.]", "").length()
                == 7) {
            solution.setPoints(solution.getPoints() + pointMap.get("*"));
        }
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
