package com.marcblais.scrabbleapi.utilities;

import com.marcblais.scrabbleapi.dto.AdjacentSolution;
import com.marcblais.scrabbleapi.dto.GridDTO;
import com.marcblais.scrabbleapi.dto.Solution;
import com.marcblais.scrabbleapi.entities.LettersValue;

import java.util.ArrayList;
import java.util.Arrays;
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
            List<Integer> doubleLetters = getBonusInSolution(solution, grid.getGridType().getDoubleLetter(), true);
            List<Integer> tripleLetters = getBonusInSolution(solution, grid.getGridType().getTripleLetter(), true);
            List<Integer> doubleWords = getBonusInSolution(solution, grid.getGridType().getDoubleWord(), true);
            List<Integer> tripleWords = getBonusInSolution(solution, grid.getGridType().getTripleWord(), true);

            // Add blanktiles to solution
            solution.getBlankTiles().addAll(getBonusInSolution(solution, grid.getBlankTiles(), false));

            // Get the solution position
            int x = solution.getX();
            int y = solution.getY();
            int letterUsed = 0;

            // Calculate the base points for solution and adjacent solution
            calculateBasePoints(solution, solution.getBlankTiles());

            // Increments points for every bonus tiles. Order of operation is important.
            calculateBonus(doubleLetters, solution, 1, true, solution.getBlankTiles());
            calculateBonus(tripleLetters, solution, 2, true, solution.getBlankTiles());
            calculateBonus(doubleWords, solution, 1, false, solution.getBlankTiles());
            calculateBonus(tripleWords, solution, 2, false, solution.getBlankTiles());

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

    private void calculateBasePoints(Solution solution, List<Integer> blankTiles) {
        String[] solutionLetters = solution.getEntry().getWord().split("");
        int solutionPoints = 0;

        for (Integer i : blankTiles) {
            solutionLetters[i] = ".";
        }

        for (String letter : solutionLetters) {
            solutionPoints += lettersValue.getPoints().getOrDefault(letter, 0);
        }

        solution.setPoints(solutionPoints);

        for (int key : solution.getAdjacentSolutions().keySet()) {
            AdjacentSolution adjacentSolution = solution.getAdjacentSolutions().get(key);
            String word = adjacentSolution.getEntry().getWord();

            if (blankTiles.stream().anyMatch(i -> key == i)) {
                String jokerLetter = solution.getEntry().getWord().substring(key, key + 1);
                word = word.replaceFirst(jokerLetter, ".");
            }

            String[] letters = word.split("");
            int points = 0;

            for (String letter : letters) {
                points += lettersValue.getPoints().getOrDefault(letter, 0);
            }

            adjacentSolution.setPoints(points);
        }
    }

    private List<Integer> getBonusInSolution(Solution solution, Integer[][] bonus, Boolean onEmptyGrid) {
        List<Integer> bonusList = new ArrayList<>();

        // Find all bonus in the list that are under a letter that the player played
        if (solution.isVertical()) {
            for (Integer[] coordinates : bonus) {
                int y = coordinates[0];
                int x = coordinates[1];

                if (x == solution.getX() &&
                        y >= solution.getY() &&
                        y < solution.getY() + solution.getEntry().getWord().length() &&
                        (!onEmptyGrid || grid.getGrid()[y][x].isEmpty()))
                    bonusList.add(y - solution.getY());
            }
        } else {
            for (Integer[] coordinates : bonus) {
                int y = coordinates[0];
                int x = coordinates[1];

                if (y == solution.getY() &&
                        x >= solution.getX() &&
                        x < solution.getX() + solution.getEntry().getWord().length() &&
                        (!onEmptyGrid || grid.getGrid()[y][x].isEmpty()))
                    bonusList.add(x - solution.getX());
            }
        }

        return bonusList;
    }

    private void calculateBonus(
            List<Integer> bonusLocation, Solution solution, int ratio, boolean isLetter, List<Integer> blankTiles
    ) {
        for (int i : bonusLocation) {
            AdjacentSolution adjacentSolution;

            String letter = solution.getEntry().getWord().substring(i, i + 1);

            // Remove points from letter bonus if the tile is blank
            if (blankTiles.stream().anyMatch(index -> i == index))
                letter = ".";

            // Chose the value to increment based on bonus type
            int bonus = isLetter ? lettersValue.getPoints().getOrDefault(letter, 0) : solution.getPoints();

            // Increment score with bonus, ratio is 1 for double and 2 for triple
            solution.setPoints(solution.getPoints() + (bonus * ratio));

            // Check if bonus also affects an adjacent word formed by the player
            adjacentSolution = solution.getAdjacentSolutions().get(i);

            // Apply the bonus again if an adjacent word is present
            if (adjacentSolution != null) {
                bonus = isLetter ? lettersValue.getPoints().getOrDefault(letter, 0) : adjacentSolution.getPoints();
                adjacentSolution.setPoints(adjacentSolution.getPoints() + (bonus * ratio));
            }
        }
    }
}
