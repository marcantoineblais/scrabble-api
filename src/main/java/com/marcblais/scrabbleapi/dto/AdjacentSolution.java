package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.DictionaryEntry;

import java.util.List;

public class AdjacentSolution {
    private DictionaryEntry entry;
    private List<GridContent> gridContents;
    private int x;
    private int y;
    private boolean isVertical;
    private int points;

    public AdjacentSolution() {
    }

    public AdjacentSolution(DictionaryEntry entry, List<GridContent> gridContents, int x, int y, boolean isVertical, int points) {
        this.entry = entry;
        this.gridContents = gridContents;
        this.x = x;
        this.y = y;
        this.isVertical = isVertical;
        this.points = points;
    }

    public DictionaryEntry getEntry() {
        return entry;
    }

    public void setEntry(DictionaryEntry entry) {
        this.entry = entry;
    }

    public List<GridContent> getGridContents() {
        return gridContents;
    }

    public void setGridContents(List<GridContent> gridContents) {
        this.gridContents = gridContents;
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
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
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
                ", gridContents=" + gridContents +
                ", x=" + x +
                ", y=" + y +
                ", isVertical=" + isVertical +
                ", points=" + points +
                '}';
    }
}
