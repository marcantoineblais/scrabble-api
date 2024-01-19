package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.List;

public class AdjacentSolution {
    private DictionaryEntry entry;
    private int points;

    public AdjacentSolution() {
    }

    public AdjacentSolution(DictionaryEntry entry) {
        this.entry = entry;
        this.points = 0;
    }

    public DictionaryEntry getEntry() {
        return entry;
    }

    public void setEntry(DictionaryEntry entry) {
        this.entry = entry;
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
                "entry=" + entry +
                ", points=" + points +
                '}';
    }
}
