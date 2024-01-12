package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Dictionary;

import java.util.Map;
import java.util.Objects;

public class Solution implements Comparable<Solution> {
    private Dictionary dictionary;
    private int x;
    private int y;
    private boolean vertical;
    private int points;
    private Map<Integer, Dictionary> adjacentWords;
    private GridContent gridContent;

    public Solution() {
    }

    public Solution(Dictionary dictionary, int x, int y, boolean vertical, int points, Map<Integer, Dictionary> adjacentWords, GridContent gridContent) {
        this.dictionary = dictionary;
        this.x = x;
        this.y = y;
        this.vertical = vertical;
        this.points = points;
        this.adjacentWords = adjacentWords;
        this.gridContent = gridContent;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
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

    public Map<Integer, Dictionary> getAdjacentWords() {
        return adjacentWords;
    }

    public void setAdjacentWords(Map<Integer, Dictionary> adjacentWords) {
        this.adjacentWords = adjacentWords;
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
            return dictionary.getWord().compareTo(o.getDictionary().getWord());
        else
            return Integer.compare(o.getPoints(), points);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solution solution)) return false;
        return x == solution.x && y == solution.y && vertical == solution.vertical && Objects.equals(dictionary, solution.dictionary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dictionary, x, y, vertical);
    }
}
