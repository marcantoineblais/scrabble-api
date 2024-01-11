package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Word;
import com.marcblais.scrabbleapi.services.WordService;

import java.util.*;

public class SolutionFinder {
//    private WordService wordService;
//    private Grid grid;
//    private Map<String, List<Word>> possibleWords;
//    private Map<String, List<Word>> adjacentWords;
//    private List<GridContent> gridContent;
//    private List<Word> validAdjacentWords;
//    private Set<String> invalidAdjacentWords;
//
//    public SolutionFinder() {
//    }
//
//    public SolutionFinder(WordService wordService, Grid grid, Map<String, List<Word>> possibleWords, List<GridContent> gridContent) {
//        this.wordService = wordService;
//        this.grid = grid;
//        this.possibleWords = possibleWords;
//        this.adjacentWords = new HashMap<>();
//        this.gridContent = gridContent;
//        this.validAdjacentWords = new ArrayList<>();
//        this.invalidAdjacentWords = new HashSet<>();
//    }
//
//    public List<Solution> findSolutions() {
//        List<Solution> solutions = new ArrayList<>();
//
//        for (String key : possibleWords.keySet()) {
//            // Find every words and letter on the grid that have the same string as every key of possible words
//            List<GridContent> possiblePlacements = gridContent.stream().filter(c -> c.getContent().equals(key)).toList();
//
//            // Grab all the GridContent that match the key
//            List<Word> words = possibleWords.get(key);
//
//            for (Word word : words) {
//                // Find where the grid content can overlap over the word
//                List<Integer> indexes = findIndexesOfSubstring(key, word);
//
//                // find every solutions that fit over the content on the grid
//                solutions.addAll(findSolutionForWord(possiblePlacements, word, indexes));
//            }
//        }
//
//        return solutions;
//    }
//
//    private List<Integer> findIndexesOfSubstring(String key, Word word) {
//        List<Integer> indexes = new ArrayList<>();
//        int index = 0;
//
//        // Find the last index of the grid content in the word
//        int lastIndex = word.getWord().lastIndexOf(key);
//
//        // Find every indexes of the grid content in the word and add them to the list
//        while (index <= lastIndex) {
//            index = word.getWord().indexOf(key, index);
//            indexes.add(index);
//            index ++;
//        }
//
//        return indexes;
//    }
//
//    private List<Solution> findSolutionForWord(List<GridContent> possiblePlacements, Word word, List<Integer> indexes) {
//        List<Solution> solutions = new ArrayList<>();
//        String[] chars = word.getWord().split("");
//        List<Solution> adjacentSolutions = null;
//
//        for (GridContent content : possiblePlacements) {
//            for (int index : indexes) {
//                // For every possible overlap of a single word over the grid content, find what solutions really fits
//                Solution solution = findSolutionForSubstring(content, word, index, chars);
//
//                if (solution != null) {
//                    solutions.add(solution);
//
//                    if (possiblePlacements.size() != 1) // Remove recursion when called from within findAdjacentWord()
//                        // Find solution that are adjacent of the grid content instead of overlapped
//                        adjacentSolutions = findAdjacentSolutions(solution, content);
//
//                    if (adjacentSolutions != null)
//                        solutions.addAll(adjacentSolutions);
//                }
//            }
//        }
//
//        return solutions;
//    }
//
//    private Solution findSolutionForSubstring(GridContent content, Word word, int positionInContent, String[] chars) {
//        Map<Integer, Word> adjacentWord = new HashMap<>();
//        Word possibleWord;
//        int x = content.getX();
//        int y = content.getY();
//
//        // Check the orientation of the grid content and if the word can fit inside the grid
//        if (content.isVertical() && isVerticalFit(x, y, positionInContent, chars.length)) {
//            for (int i = 0; i < chars.length; i++) {
//                // Calculate the position of the first letter of the word when overlapped over the grid content
//                int verticalPosition = i + y - positionInContent;
//
//                // check if a letter on the grid is different from the grid content at that position
//                if (
//                        !grid.getGrid().get(verticalPosition).get(x).equals(chars[i]) &&
//                        !grid.getGrid().get(verticalPosition).get(x).isBlank()
//                )
//                    return null;
//
//                // check if the word surroundings are free
//                if (
//                        !grid.getGrid().get(verticalPosition).get(x).equals(chars[i]) &&
//                        !isHorizontalSurroundingsFree(x, verticalPosition, chars[i])
//                ) {
//                    // If the surrounding are not free, find what grid content is there and check if it can form a word
//                    possibleWord = findHorizontalAdjacentWord(x, verticalPosition, chars[i]);
//
//                    if (possibleWord == null)
//                        return null;
//                    else
//                        adjacentWord.put(i, possibleWord);
//                }
//            }
//        } else if (!content.isVertical() && isHorizontalFit(x, y, positionInContent, chars.length)) { // if word fits inside grid
//            for (int i = 0; i < chars.length; i++) {
//                // Calculate the position of the first letter of the word when overlapped over the grid content
//                int horizontalPosition = i + x - positionInContent;
//
//                // check if a letter on the grid is different from the grid content at that position
//                if (
//                        !grid.getGrid().get(y).get(horizontalPosition).equals(chars[i]) &&
//                        !grid.getGrid().get(y).get(horizontalPosition).isBlank()
//                )
//                    return null;
//
//                // check if the word surroundings are free
//                if (
//                        !grid.getGrid().get(y).get(horizontalPosition).equals(chars[i]) &&
//                        !isVerticalSurroundingsFree(horizontalPosition, y, chars[i])
//                ) {
//                    // If the surrounding are not free, find what grid content is there and check if it can form a word
//                    possibleWord = findVerticalAdjacentWord(horizontalPosition, y, chars[i]);
//
//                    if (possibleWord == null)
//                        return null;
//                    else
//                        adjacentWord.put(i, possibleWord);
//                }
//            }
//        } else {
//            return null;
//        }
//
//        // When creation solution, calculate the new coordinates on grid based on the overlapped content orientation
//        return new Solution(
//                word.getWord(),
//                content.isVertical() ? x : x - positionInContent,
//                content.isVertical() ? y - positionInContent : y,
//                content.isVertical(),
//                word.getPoints(),
//                adjacentWord,
//                content
//        );
//    }
//
//    private boolean isVerticalFit(int x, int y, int i, int length) {
//        // Calculate begin and end position of the word when overlapped over grid content
//        int verticalPosition = y - i;
//        int lastLetterPosition = verticalPosition + length - 1;
//
//        if (verticalPosition < 0) // is word outside of grid
//            return false;
//        if (lastLetterPosition > grid.getGrid().size() - 1) // is word outside of grid
//            return false;
//        if (verticalPosition > 0 && !grid.getGrid().get(verticalPosition - 1).get(x).isBlank()) // is word next to other letter
//            return false;
//        if (lastLetterPosition < grid.getGrid().size() - 1 && !grid.getGrid().get(lastLetterPosition + 1).get(x).isBlank()) // is word next to other letter
//            return false;
//
//        return true;
//    }
//
//    private boolean isHorizontalFit(int x, int y, int i, int length) {
//        // Calculate begin and end position of the word when overlapped over grid content
//        int horizontalPosition = x - i;
//        int lastLetterPosition = horizontalPosition + length - 1;
//
//        if (horizontalPosition < 0) // is word outside of grid
//            return false;
//        if (lastLetterPosition > grid.getGrid().get(y).size() - 1) // is word outside of grid
//            return false;
//        if (horizontalPosition > 0 && !grid.getGrid().get(y).get(horizontalPosition - 1).isBlank()) // is word next to other letter
//            return false;
//        if (lastLetterPosition < grid.getGrid().get(y).size() - 1 && !grid.getGrid().get(y).get(lastLetterPosition + 1).isBlank()) // is word next to other letter
//            return false;
//
//        return true;
//    }
//
//    private boolean isVerticalSurroundingsFree(int x, int y, String letter) {
//        // Check if there is content on the grid above and below the letter position when overlapped over grid content
//        if (y != 0 && !grid.getGrid().get(y - 1).get(x).isBlank())
//            return false;
//        if (y != grid.getGrid().size() - 1 && !grid.getGrid().get(y + 1).get(x).isBlank())
//            return false;
//
//        return true;
//    }
//
//    private boolean isHorizontalSurroundingsFree(int x, int y, String letter) {
//        // Check if there is content on the grid above and below the letter position when overlapped over grid content
//        if (x != 0 && !grid.getGrid().get(y).get(x - 1).isBlank())
//            return false;
//        if (x != grid.getGrid().get(y).size() - 1 && !grid.getGrid().get(y).get(x + 1).isBlank())
//            return false;
//
//        return true;
//    }
//
//    private Word findVerticalAdjacentWord(int x, int y, String letter) {
//
//        // Find the grid content that is above the letter and is vertical
//        Optional<GridContent> wordAbove = gridContent.stream().filter(c -> {
//            return c.getX() == x && c.getY() + c.getContent().length() == y && c.isVertical();
//        }).findFirst();
//
//        // Find the grid content that is below the letter and is vertical
//        Optional<GridContent> wordBelow = gridContent.stream().filter(c -> {
//            return c.getX() == x && c.getY() == y + 1 && c.isVertical();
//        }).findFirst();
//
//        // Concatenate the grid content together
//        String wordToTest = "";
//
//        if (wordAbove.isPresent())
//            wordToTest += wordAbove.get().getContent();
//
//        wordToTest += letter;
//
//        if (wordBelow.isPresent())
//            wordToTest += wordBelow.get().getContent();
//
//        // Return a word if it exists, else return null
//        return getValidAdjacentWord(wordToTest);
//    }
//
//    private Word findHorizontalAdjacentWord(int x, int y, String letter) {
//        // Find the grid content that is above the letter and is horizontal
//        Optional<GridContent> wordBefore = gridContent.stream().filter(c -> {
//            return c.getY() == y && c.getX() + c.getContent().length() - 1 == x - 1 && !c.isVertical();
//        }).findFirst();
//
//        // Find the grid content that is above the letter and is horizontal
//        Optional<GridContent> wordAfter = gridContent.stream().filter(c -> {
//            return c.getY() == y && c.getX() == x + 1 && !c.isVertical();
//        }).findFirst();
//
//        // Concatenate the grid content together
//        String wordToTest = "";
//        if (wordBefore.isPresent())
//            wordToTest += wordBefore.get().getContent();
//
//        wordToTest += letter;
//
//        if (wordAfter.isPresent())
//            wordToTest += wordAfter.get().getContent();
//
//        // Return a word if it exists, else return null
//        return getValidAdjacentWord(wordToTest);
//    }
//
//    private Word getValidAdjacentWord(String wordToTest) {
//        Optional<Word> adjacentWord;
//        List<String> vowels = Arrays.asList("A", "E", "I", "O", "U","Y");
//
//        // Check if the word was already tested and invalid
//        if (invalidAdjacentWords.contains(wordToTest))
//            return null;
//
//        // Check if the word are only consonants with the vowels array
//        if (wordToTest.length() <= 3 && vowels.stream().noneMatch(wordToTest::contains)) {
//            invalidAdjacentWords.add(wordToTest);
//            return null;
//        }
//
//        // Check if the word was tested before and solutions where found
//        adjacentWord = validAdjacentWords.stream().filter(v -> v.getWord().equals(wordToTest)).findFirst();
//
//        // If the word was not found before, query the database to find it
//        if (adjacentWord.isEmpty()) {
//            adjacentWord = wordService.findByWord(wordToTest);
//
//            // If it does not exist, add it to the invalid words list, else add it to the valid words list
//            if (adjacentWord.isEmpty()) {
//                invalidAdjacentWords.add(wordToTest);
//            } else {
//                validAdjacentWords.add(adjacentWord.get());
//            }
//        }
//
//        // returns a word if it is valid, else returns null
//        return adjacentWord.orElse(null);
//    }
//
//    private List<Solution> findAdjacentSolutions(Solution solution, GridContent content) {
//       GridContent newGridContent;
//       List<Solution> solutions = new ArrayList<>();
//
//        // If the solution uses more than 1 letter, do not check for adjacent solutions
//        if (solution.getWord().length() - content.getContent().length() != 1)
//            return null;
//
//        // Create GridContent based on the letter that was played based on the letter position relative to the grid content
//        if (solution.getX() == content.getX() && solution.getY() == content.getY()) {
//            newGridContent = new GridContent(
//                solution.getWord().replaceFirst(content.getContent(), ""),
//                solution.isVertical() ? solution.getX() : solution.getX() + solution.getWord().length() - 1,
//                solution.isVertical() ? solution.getY() + solution.getWord().length() - 1 : solution.getY(),
//                !solution.isVertical()
//            );
//        } else {
//            newGridContent = new GridContent(
//                solution.getWord().replace(content.getContent(), ""),
//                solution.getX(),
//                solution.getY(),
//                !solution.isVertical()
//            );
//        }
//
//        // If the solutions was not tested before, query the database using only the 6 remaining letters
//        if (!adjacentWords.containsKey(newGridContent.getContent())) {
//            adjacentWords.put(newGridContent.getContent(), wordService.findByLetters(
//                    grid.getPlayerLetters().replaceFirst(newGridContent.getContent(), ""),
//                    newGridContent.getContent()
//            ));
//        }
//
//        for (Word word : adjacentWords.get(newGridContent.getContent())) {
//            // Find where the word can overlap over the grid content
//            List<Integer> indexes = findIndexesOfSubstring(newGridContent.getContent(), word);
//
//            // Find the solutions that are valid using this new list of grid content
//            solutions.addAll(findSolutionForWord(new ArrayList<>(List.of(newGridContent)), word, indexes));
//        }
//
//        return solutions;
//    }
}
