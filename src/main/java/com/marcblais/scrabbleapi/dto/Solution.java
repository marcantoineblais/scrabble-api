package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.entities.Grid;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Solution implements Comparable<Solution> {
    private DictionaryEntry dictionaryEntry;
    private int x;
    private int y;
    private boolean vertical;
    private int points;
    private List<AdjacentSolution> adjacentSolutions;
    private GridContent gridContent;

    public Solution() {
    }

    public Solution(DictionaryEntry dictionaryEntry, int x, int y, boolean vertical, int points, List<AdjacentSolution> adjacentSolutions, GridContent gridContent) {
        this.dictionaryEntry = dictionaryEntry;
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        this.points = points;
        this.adjacentSolutions = adjacentSolutions;
        this.gridContent = gridContent;
    }

    public DictionaryEntry getDictionaryEntry() {
        return dictionaryEntry;
    }

    public void setDictionaryEntry(DictionaryEntry dictionaryEntry) {
        this.dictionaryEntry = dictionaryEntry;
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

    public List<AdjacentSolution> getAdjacentSolutions() {
        return adjacentSolutions;
    }

    public void setAdjacentSolutions(List<AdjacentSolution> adjacentSolutions) {
        this.adjacentSolutions = adjacentSolutions;
    }

    public GridContent getGridContent() {
        return gridContent;
    }

    public void setGridContent(GridContent gridContent) {
        this.gridContent = gridContent;
    }

    @Override
    public int compareTo(Solution o) {
        if (points == o.getPoints())
            return dictionaryEntry.getWord().compareTo(o.getDictionaryEntry().getWord());
        else
            return Integer.compare(o.getPoints(), points);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solution solution)) return false;
        return x == solution.x && y == solution.y && vertical == solution.vertical && Objects.equals(dictionaryEntry, solution.dictionaryEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dictionaryEntry, x, y, vertical);
    }

    @Override
    public String toString() {
        return "Solution{" +
                "dictionaryEntry=" + dictionaryEntry +
                ", x=" + x +
                ", y=" + y +
                ", vertical=" + vertical +
                ", points=" + points +
                ", adjacentSolutions=" + adjacentSolutions +
                ", gridContent=" + gridContent +
                '}';
    }
}
