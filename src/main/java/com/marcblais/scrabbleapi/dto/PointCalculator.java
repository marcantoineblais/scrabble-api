package com.marcblais.scrabbleapi.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointCalculator {
//    private final Grid grid;
//    private final List<Solution> solutions;
//    private final Map<String, Integer> letters;
//
//    public PointCalculator(Grid grid, List<Solution> solutions, Map<String, Integer> letters) {
//        this.grid = grid;
//        this.solutions = solutions;
//        this.letters = letters;
//    }
//
//    public void calculatePoints() {
//        for (Solution solution : solutions) {
//            // Find every bonus tiles on which the player placed a letter
//            List<Integer> doubleLetters = getBonusInSolution(solution, grid.getDoubleLetter());
//            List<Integer> tripleLetters = getBonusInSolution(solution, grid.getTripleLetter());
//            List<Integer> doubleWords = getBonusInSolution(solution, grid.getDoubleWord());
//            List<Integer> tripleWords = getBonusInSolution(solution, grid.getTripleWord());
//
//            // Get the solution position
//            int x = solution.getX();
//            int y = solution.getY();
//            int letterUsed = 0;
//
//            // Increments points for every bonus tiles. Order of operation is important.
//            calculateBonus(doubleLetters, solution, 1, true);
//            calculateBonus(tripleLetters, solution, 2, true);
//            calculateBonus(doubleWords, solution, 1, false);
//            calculateBonus(tripleWords, solution, 2, false);
//
//            // Count how many letters were used
//            if (solution.isVertical()) {
//                for (int i = 0; i < solution.getWord().length(); i++) {
//                    if (grid.getGrid().get(y + i).get(x).isEmpty())
//                        letterUsed++;
//                }
//            } else {
//                for (int i = 0; i < solution.getWord().length(); i++) {
//                    if (grid.getGrid().get(y).get(x + i).isEmpty())
//                        letterUsed++;
//                }
//            }
//
//            // Add the bonus if player has a Scrabble
//            if (letterUsed == 7)
//                solution.setPoints(solution.getPoints() + 50);
//        }
//    }
//
//    private List<Integer> getBonusInSolution(Solution solution, List<List<Integer>> bonus) {
//        List<Integer> bonusList;
//
//        // Find all bonus in the list that are under a letter that the player played
//        if (solution.isVertical()) {
//            bonusList = bonus.stream().filter(c -> {
//                int x = c.get(0);
//                int y = c.get(1);
//
//                return (
//                    x == solution.getX() &&
//                    y >= solution.getY() &&
//                    y < solution.getY() + solution.getWord().length() &&
//                    grid.getGrid().get(y).get(x).isEmpty()
//                );
//            }).map(d -> d.get(1)).toList(); // Only keep vertical value since word is vertical
//        } else {
//            bonusList = bonus.stream().filter(c -> {
//                int x = c.get(0);
//                int y = c.get(1);
//
//                return (
//                    y == solution.getY() &&
//                    x >= solution.getX() &&
//                    x < solution.getX() + solution.getWord().length() &&
//                    grid.getGrid().get(y).get(x).isEmpty()
//                );
//            }).map(d -> d.get(0)).toList(); // Only keep horizontal value since word is horizontal
//        }
//
//        return bonusList;
//    }
//
//    private void calculateBonus(List<Integer> bonusLocation, Solution solution, int ratio, boolean isLetter) {
//        for (Integer i : bonusLocation) {
//            FrenchWord adjacentWord;
//
//            // Find under what letter the bonus is
//            int positionInWord = solution.isVertical() ? i - solution.getY() : i - solution.getX();
//            String letter = solution.getWord().substring(positionInWord, positionInWord + 1);
//
//            // Chose the value to increment based on bonus type
//            int bonus = isLetter ? letters.get(letter) : solution.getPoints();
//
//            // Increment score with bonus, ratio is 1 for double and 2 for triple
//            solution.setPoints(solution.getPoints() + (bonus * ratio));
//
//            // Check if bonus also affects an adjacent word formed by the player
//            if (solution.isVertical()) {
//                adjacentWord = solution.getAdjacentWords().get(i - solution.getY());
//                int x = solution.getX();
//
//                // Apply the bonus again if an adjacent word is present
//                if (adjacentWord != null) {
//                    bonus = isLetter ? letters.get(letter) : adjacentWord.getPoints();
//                    solution.setPoints(solution.getPoints() + (bonus * ratio));
//                }
//            } else {
//                adjacentWord = solution.getAdjacentWords().get(i - solution.getX());
//                int y = solution.getY();
//
//                // Apply the bonus again if an adjacent word is present
//                if (adjacentWord != null) {
//                    bonus = isLetter ? letters.get(letter) : adjacentWord.getPoints();
//                    solution.setPoints(solution.getPoints() + (bonus * ratio));
//                }
//            }
//        }
//    }
}
