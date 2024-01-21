package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.entities.Grid;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Solution implements Comparable<Solution> {
    private DictionaryEntry entry;
    private GridContent gridContent;
    private Map<Integer, AdjacentSolution> adjacentSolutions;
    private String pattern;
    private boolean vertical;
    private int x;
    private int y;
    private int points;

    public Solution() {
    }

    public Solution(
            DictionaryEntry entry,
            GridContent gridContent,
            Map<Integer, AdjacentSolution> adjacentSolutions,
            String pattern,
            boolean vertical,
            int x,
            int y
    ) {
        this.entry = entry;
        this.gridContent = gridContent;
        this.adjacentSolutions = adjacentSolutions;
        this.pattern = pattern;
        this.vertical = vertical;
        this.x = x;
        this.y = y;
        this.points = 0;
    }

    public DictionaryEntry getEntry() {
        return entry;
    }

    public void setEntry(DictionaryEntry entry) {
        this.entry = entry;
    }

    public GridContent getGridContent() {
        return gridContent;
    }

    public void setGridContent(GridContent gridContent) {
        this.gridContent = gridContent;
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
                ", gridContent=" + gridContent +
                '}';
    }
}
