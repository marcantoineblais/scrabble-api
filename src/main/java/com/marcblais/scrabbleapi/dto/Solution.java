package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.*;

public class Solution implements Comparable<Solution> {
    private DictionaryEntry entry;
    @JsonIgnore
    private GridRowsCols gridRowsCols;

    @JsonIgnore
    private Map<Integer, AdjacentSolution> adjacentSolutions;

    @JsonIgnore
    private String pattern;
    private boolean vertical;
    private int x;
    private int y;
    private int points;
    private List<Integer> blankTiles;

    public Solution() {
        this.adjacentSolutions = new HashMap<>();
        this.blankTiles = new ArrayList<>();
    }

    public Solution(
            DictionaryEntry entry,
            GridRowsCols gridRowsCols,
            Map<Integer, AdjacentSolution> adjacentSolutions,
            String pattern,
            boolean vertical,
            int x,
            int y
    ) {
        this.entry = entry;
        this.gridRowsCols = gridRowsCols;
        this.adjacentSolutions = adjacentSolutions;
        this.pattern = pattern;
        this.vertical = vertical;
        this.x = x;
        this.y = y;
        this.blankTiles = new ArrayList<>();
        this.points = 0;
    }

    public DictionaryEntry getEntry() {
        return entry;
    }

    public void setEntry(DictionaryEntry entry) {
        this.entry = entry;
    }

    public GridRowsCols getGridContent() {
        return gridRowsCols;
    }

    public void setGridContent(GridRowsCols gridRowsCols) {
        this.gridRowsCols = gridRowsCols;
    }

    public Map<Integer, AdjacentSolution> getAdjacentSolutions() {
        return adjacentSolutions;
    }

    public void setAdjacentSolutions(Map<Integer, AdjacentSolution> adjacentSolutions) {
        this.adjacentSolutions = adjacentSolutions;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
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

    @Override
    public int compareTo(Solution o) {
        if (points == o.getPoints())
            return entry.getWord().compareTo(o.getEntry().getWord());
        else
            return Integer.compare(o.getPoints(), points);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solution solution)) return false;
        return x == solution.x && y == solution.y && vertical == solution.vertical && Objects.equals(entry, solution.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, x, y, vertical);
    }

    @Override
    public String toString() {
        return "Solution{" +
                "entry=" + entry +
                ", x=" + x +
                ", y=" + y +
                ", vertical=" + vertical +
                ", points=" + points +
                ", adjacentSolutions=" + adjacentSolutions +
                ", gridContent=" + gridRowsCols +
                ", blankTiles=" + blankTiles +
                '}';
    }
}
