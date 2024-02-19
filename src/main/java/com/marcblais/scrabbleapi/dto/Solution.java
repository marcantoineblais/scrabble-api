package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.*;

public class Solution implements Comparable<Solution> {
    private DictionaryEntry entry;

    @JsonIgnore
    private GridRowCol gridRowCol;

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
            GridRowCol gridRowCol,
            Map<Integer, AdjacentSolution> adjacentSolutions,
            String pattern,
            boolean vertical,
            int x,
            int y
    ) {
        this.entry = entry;
        this.gridRowCol = gridRowCol;
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

    public GridRowCol getGridRowCol() {
        return gridRowCol;
    }

    public void setGridRowCol(GridRowCol gridRowCol) {
        this.gridRowCol = gridRowCol;
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

    public int getLastY() {
        return isVertical() ? y + entry.getWord().length() - 1 : y;
    }

    public int getLastX() {
        return isVertical() ? x : x + entry.getWord().length() - 1;
    }

    public void assignBlankTiles() {
        if (gridRowCol.getBlankTiles().isEmpty())
            return;

        for (Integer i : gridRowCol.getBlankTiles()) {
            if (vertical && i >= y && i <= getLastY())
                blankTiles.add(i - y);
            else if (!vertical && i >= x && i <= getLastX())
                blankTiles.add(i - x);
        }
    }

    @Override
    public int compareTo(Solution o) {
        if (o == null)
            return -1;

        if (points == o.getPoints())
            return entry.getWord().compareTo(o.getEntry().getWord());
        else
            return Integer.compare(o.getPoints(), points);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solution solution)) return false;
        return vertical == solution.vertical &&
                x == solution.x && y == solution.y &&
                Objects.equals(entry, solution.entry) &&
                Objects.equals(blankTiles, solution.blankTiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, vertical, x, y, blankTiles);
    }

    @Override
    public String toString() {
        return "Solution{" +
                "entry=" + entry +
                ", gridRowsCols=" + gridRowCol +
                ", adjacentSolutions=" + adjacentSolutions +
                ", pattern='" + pattern + '\'' +
                ", vertical=" + vertical +
                ", x=" + x +
                ", y=" + y +
                ", points=" + points +
                ", blankTiles=" + blankTiles +
                '}';
    }
}
