package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Language;

import java.util.*;

public class Grid {
    private String[][] grid;
    private PlayerLetters playerLetters;
    private Language language;
    private int[][] doubleLetter;
    private int[][] tripleLetter;
    private int[][] doubleWord;
    private int[][] tripleWord;

    public Grid() {
    }

    public Grid(String[][] grid, PlayerLetters playerLetters, Language language, int[][] doubleLetter, int[][] tripleLetter, int[][] doubleWord, int[][] tripleWord) {
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.language = language;
        this.doubleLetter = doubleLetter;
        this.tripleLetter = tripleLetter;
        this.doubleWord = doubleWord;
        this.tripleWord = tripleWord;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setGrid(String[][] grid) {
        this.grid = grid;
    }

    public PlayerLetters getPlayerLetters() {
        return playerLetters;
    }

    public void setPlayerLetters(PlayerLetters playerLetters) {
        this.playerLetters = playerLetters;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public int[][] getDoubleLetter() {
        return doubleLetter;
    }

    public void setDoubleLetter(int[][] doubleLetter) {
        this.doubleLetter = doubleLetter;
    }

    public int[][] getTripleLetter() {
        return tripleLetter;
    }

    public void setTripleLetter(int[][] tripleLetter) {
        this.tripleLetter = tripleLetter;
    }

    public int[][] getDoubleWord() {
        return doubleWord;
    }

    public void setDoubleWord(int[][] doubleWord) {
        this.doubleWord = doubleWord;
    }

    public int[][] getTripleWord() {
        return tripleWord;
    }

    public void setTripleWord(int[][] tripleWord) {
        this.tripleWord = tripleWord;
    }

    public List<GridContent> toGridContent() {
        List<GridContent> gridContent = new ArrayList<>();

        for (int y = 0; y < grid.length; y++) {
            String[] row = grid[y];
            String horizontal;
            String vertical;

            for (int x = 0; x < row.length; x++) {
                String col = row[x];

                if (!col.isEmpty()) {
                    horizontal = evaluateRow(x, y);
                    vertical = evaluateCol(x, y);

                    if (!horizontal.isBlank())
                        gridContent.add(new GridContent(horizontal, x, y,false));
                    if (!vertical.isBlank())
                        gridContent.add(new GridContent(vertical, x, y,true));
                }
            }
        }

        return gridContent;
    }

    private String evaluateRow(int x, int y) {
        String[] row = grid[y];
        StringBuilder gridContent = new StringBuilder();

        if (x == 0 || row[x - 1].isBlank()) {
            while (x < row.length && !row[x].isEmpty()) {
                gridContent.append(row[x++]);
            }
        }

        return gridContent.toString();
    }

    private String evaluateCol(int x, int y) {
        StringBuilder gridContent = new StringBuilder();

        if (y == 0 || grid[y - 1][x].isBlank()) {
            while (y < grid.length && !grid[y][x].isEmpty()) {
                gridContent.append(grid[y++][x]);
            }
        }

        return gridContent.toString();
    }
}
