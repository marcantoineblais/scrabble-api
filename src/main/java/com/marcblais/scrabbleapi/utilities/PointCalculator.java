package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.GridDTO;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.LettersValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PointCalculator {
    private GridDTO grid;
    private Set<Solution> solutions;
    private LettersValue lettersValue;

    public PointCalculator() {
    }

    public PointCalculator(GridDTO grid, Set<Solution> solutions, LettersValue lettersValue) {
        this.grid = grid;
        this.solutions = solutions;
        this.lettersValue = lettersValue;
    }

    public List<Solution> findTopSolutions(int size) {
        List<Solution> bestSolutions = new ArrayList<>();
        Solution minPoints = null;

        for (Solution solution : solutions) {


            if (minPoints == null || solution.compareTo(minPoints) < 0) {
                bestSolutions.add(solution);

                if (bestSolutions.size() > size) {
                    bestSolutions.remove(minPoints);
                    minPoints = bestSolutions.stream().max(Solution::compareTo).orElse(null);
                }
            }
        }
        
        bestSolutions.remove(minPoints);
        bestSolutions.sort(Solution::compareTo);
        return bestSolutions;
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

            // Add all the points from the adjacent solutions to the solution
            for (int key : solution.getAdjacentSolutions().keySet()) {
                solution.setPoints(solution.getPoints() + solution.getAdjacentSolutions().get(key).getPoints());
            }

            // Count how many letters were used
            if (solution.isVertical()) {
                for (int i = 0; i < solution.getEntry().getWord().length(); i++) {
                    if (grid.getGrid()[y + i][x].isEmpty())
                        letterUsed++;
                }
            } else {
                for (int i = 0; i < solution.getEntry().getWord().length(); i++) {
                    if (grid.getGrid()[y][x + i].isEmpty())
                        letterUsed++;
                }
            }

            // Add the bonus if player has a Scrabble
            if (letterUsed == 7)
                solution.setPoints(solution.getPoints() + lettersValue.getPoints().get("*"));
        }
    }

    private void calculateBasePoints(Solution solution) {
        String[] solutionLetters = solution.getEntry().getWord().split("");
        int solutionPoints = 0;

        for (String letter : solutionLetters) {
            solutionPoints += lettersValue.getPoints().get(letter);
        }

        solution.setPoints(solutionPoints);

        for (int key : solution.getAdjacentSolutions().keySet()) {
            AdjacentSolution adjacentSolution = solution.getAdjacentSolutions().get(key);
            String[] letters = adjacentSolution.getEntry().getWord().split("");
            int points = 0;

            for (String letter : letters) {
                points += lettersValue.getPoints().get(letter);
            }

            adjacentSolution.setPoints(points);
        }
    }

    private List<Integer> getBonusInSolution(Solution solution, int[][] bonus) {
        List<Integer> bonusList = new ArrayList<>();

        // Find all bonus in the list that are under a letter that the player played
        if (solution.isVertical()) {
            for (int[] coordinates : bonus) {
                int x = coordinates[0];
                int y = coordinates[1];

                if (x == solution.getX() &&
                        y >= solution.getY() &&
                        y < solution.getY() + solution.getEntry().getWord().length() &&
                        grid.getGrid()[y][x].isEmpty())
                    bonusList.add(y);
            }
        } else {
            for (int[] coordinates : bonus) {
                int x = coordinates[0];
                int y = coordinates[1];

                if (y == solution.getY() &&
                        x >= solution.getX() &&
                        x < solution.getX() + solution.getEntry().getWord().length() &&
                        grid.getGrid()[y][x].isEmpty())
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
            String letter = solution.getEntry().getWord().substring(positionInWord, positionInWord + 1);

            // Chose the value to increment based on bonus type
            int bonus = isLetter ? lettersValue.getPoints().get(letter) : solution.getPoints();

            // Increment score with bonus, ratio is 1 for double and 2 for triple
            solution.setPoints(solution.getPoints() + (bonus * ratio));

            // Check if bonus also affects an adjacent word formed by the player
            adjacentSolution = solution.getAdjacentSolutions().get(positionInWord);

            // Apply the bonus again if an adjacent word is present
            if (adjacentSolution != null) {
                bonus = isLetter ? lettersValue.getPoints().get(letter) : adjacentSolution.getPoints();
                adjacentSolution.setPoints(adjacentSolution.getPoints() + (bonus * ratio));
            }
        }
    }
}
