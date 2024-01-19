package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.LettersValue;

import java.util.ArrayList;
import java.util.List;

public class PointCalculator {
    private Grid grid;
    private List<Solution> solutions;
    private LettersValue lettersValue;

    public PointCalculator() {
    }

    public PointCalculator(Grid grid, List<Solution> solutions, LettersValue lettersValue) {
        this.grid = grid;
        this.solutions = solutions;
        this.lettersValue = lettersValue;
    }

    public void calculatePoints() {
        for (Solution solution : solutions) {
            // Find every bonus tiles on which the player placed a letter
            List<Integer> doubleLetters = getBonusInSolution(solution, grid.getGridType().getDoubleLetter());
            List<Integer> tripleLetters = getBonusInSolution(solution, grid.getGridType().getTripleLetter());
            List<Integer> doubleWords = getBonusInSolution(solution, grid.getGridType().getDoubleWord());
            List<Integer> tripleWords = getBonusInSolution(solution, grid.getGridType().getTripleWord());

            // Get the solution position
            int x = solution.getX();
            int y = solution.getY();
            int letterUsed = 0;

            // Calculate the base points for solution and adjacent solution
            calculateBasePoints(solution);

            // Increments points for every bonus tiles. Order of operation is important.
            calculateBonus(doubleLetters, solution, 1, true);
            calculateBonus(tripleLetters, solution, 2, true);
            calculateBonus(doubleWords, solution, 1, false);
            calculateBonus(tripleWords, solution, 2, false);

            // Count how many letters were used
            if (solution.isVertical()) {
                for (int i = 0; i < solution.getDictionaryEntry().getWord().length(); i++) {
                    if (grid.getGrid()[y + i][x].isEmpty())
                        letterUsed++;
                }
            } else {
                for (int i = 0; i < solution.getDictionaryEntry().getWord().length(); i++) {
                    if (grid.getGrid()[y][x + i].isEmpty())
                        letterUsed++;
                }
            }

            // Add the bonus if player has a Scrabble
            if (letterUsed == 7)
                solution.setPoints(solution.getPoints() + 50);
        }
    }

    private void calculateBasePoints(Solution solution) {
        String[] letters = solution.getDictionaryEntry().getWord().split("");
        int points = 0;

        for (String letter : letters) {
            points += lettersValue.getPoints().get(letter);
        }

        solution.setPoints(points);

        for (int key : solution.getAdjacentSolutions().keySet()) {
            AdjacentSolution adjacentSolution = solution.getAdjacentSolutions().get(key);
            letters = adjacentSolution.getEntry().getWord().split("");
            points = 0;

            for (String letter : letters) {
                points += lettersValue.getPoints().get(letter);
            }

            adjacentSolution.setPoints(points);
            solution.setPoints(solution.getPoints() + points);
        }
    }

    private List<Integer> getBonusInSolution(Solution solution, int[][] bonus) {
        List<Integer> bonusList = new ArrayList<>();

        // Find all bonus in the list that are under a letter that the player played
        if (solution.isVertical()) {
            for (int[] coordinates : bonus) {
                int x = coordinates[0];
                int y = coordinates[1];

                if (
                        x == solution.getX() && y >= solution.getY() &&
                        y < solution.getY() + solution.getDictionaryEntry().getWord().length() &&
                        grid.getGrid()[y][x].isEmpty()
                )
                    bonusList.add(y);
            }
        } else {
            for (int[] coordinates : bonus) {
                int x = coordinates[0];
                int y = coordinates[1];

                if (
                        y == solution.getY() && x >= solution.getX() &&
                        x < solution.getX() + solution.getDictionaryEntry().getWord().length() &&
                        grid.getGrid()[y][x].isEmpty()
                )
                    bonusList.add(x);
            }
        }

        return bonusList;
    }

    private void calculateBonus(List<Integer> bonusLocation, Solution solution, int ratio, boolean isLetter) {
        for (int i : bonusLocation) {
            AdjacentSolution adjacentSolution;

            // Find under what letter the bonus is
            int positionInWord = solution.isVertical() ? i - solution.getY() : i - solution.getX();
            String letter = solution.getDictionaryEntry().getWord().substring(positionInWord, positionInWord + 1);

            // Chose the value to increment based on bonus type
            int bonus = isLetter ? lettersValue.getPoints().get(letter) : solution.getPoints();

            // Increment score with bonus, ratio is 1 for double and 2 for triple
            solution.setPoints(solution.getPoints() + (bonus * ratio));

            // Check if bonus also affects an adjacent word formed by the player
            if (solution.isVertical()) {
                adjacentSolution = solution.getAdjacentSolutions().get(i - solution.getY());
                int x = solution.getX();

                // Apply the bonus again if an adjacent word is present
                if (adjacentSolution != null) {
                    bonus = isLetter ? lettersValue.getPoints().get(letter) : adjacentSolution.getPoints();
                    solution.setPoints(solution.getPoints() + (bonus * ratio));
                }
            } else {
                adjacentSolution = solution.getAdjacentSolutions().get(i - solution.getX());
                int y = solution.getY();

                // Apply the bonus again if an adjacent word is present
                if (adjacentSolution != null) {
                    bonus = isLetter ? lettersValue.getPoints().get(letter) : adjacentSolution.getPoints();
                    solution.setPoints(solution.getPoints() + (bonus * ratio));
                }
            }
        }
    }
}
