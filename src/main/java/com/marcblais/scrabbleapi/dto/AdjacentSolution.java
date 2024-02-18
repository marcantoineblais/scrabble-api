package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

public class AdjacentSolution {
    private String word;
    private int points;

    public AdjacentSolution() {
    }

    public AdjacentSolution(String word) {
        this.word = word;
        this.points = 0;
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


    @Override
    public String toString() {
        return "AdjacentSolution{" +
                "word='" + word + '\'' +
                ", points=" + points +
                '}';
    }
}
