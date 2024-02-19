package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;

public class AdjacentSolution {
    private String word;
    private int points;
    List<Integer> blankTiles;

    public AdjacentSolution() {
        this.blankTiles = new ArrayList<>();
    }

    public AdjacentSolution(String word) {
        this.word = word;
        this.points = 0;
        this.blankTiles = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Integer> getBlankTiles() {
        return blankTiles;
    }

    public void setBlankTiles(List<Integer> blankTiles) {
        this.blankTiles = blankTiles;
    }

    public void assignBlankTiles(GridEntry before, GridEntry after) {
        if (before != null)
            blankTiles.addAll(before.getBlankTiles());

        if (after != null) {
            for (Integer i : after.getBlankTiles()) {
                blankTiles.add(i + word.length() - after.getEntry().length());
            }
        }
    }

    @Override
    public String toString() {
        return "AdjacentSolution{" +
                "word='" + word + '\'' +
                ", points=" + points +
                ", blankTiles=" + blankTiles +
                '}';
    }
}
