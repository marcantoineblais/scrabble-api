package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Solution {
//    private String word;
//    private int x;
//    private int y;
//    private boolean vertical;
//    private int points;
//    private Map<Integer, Word> adjacentWords;
//    private GridContent gridContent;
//
//    public Solution() {
//    }
//
//    public Solution(String word, int x, int y, boolean vertical, int points, Map<Integer, Word> adjacentWords, GridContent gridContent) {
//        this.word = word;
//        this.x = x;
//        this.y = y;
//        this.vertical = vertical;
//        this.points = points;
//        this.adjacentWords = adjacentWords;
//        this.gridContent = gridContent;
//    }
//
//    public String getWord() {
//        return word;
//    }
//
//    public void setWord(String word) {
//        this.word = word;
//    }
//
//    public int getX() {
//        return x;
//    }
//
//    public void setX(int x) {
//        this.x = x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public void setY(int y) {
//        this.y = y;
//    }
//
//    public boolean isVertical() {
//        return vertical;
//    }
//
//    public void setVertical(boolean vertical) {
//        this.vertical = vertical;
//    }
//
//    public int getPoints() {
//        return points;
//    }
//
//    public void setPoints(int points) {
//        this.points = points;
//    }
//
//    public Map<Integer, Word> getAdjacentWords() {
//        return adjacentWords;
//    }
//
//    public void setAdjacentWords(Map<Integer, Word> adjacentWords) {
//        this.adjacentWords = adjacentWords;
//    }
//
//    public GridContent getGridContent() {
//        return gridContent;
//    }
//
//    public void setGridContent(GridContent gridContent) {
//        this.gridContent = gridContent;
//    }
//
//    @Override
//    public int compareTo(Solution o) {
//        if (points == o.getPoints())
//            return word.compareTo(o.getWord());
//        else
//            return Integer.compare(o.getPoints(), points);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Solution solution)) return false;
//        return x == solution.x && y == solution.y && vertical == solution.vertical && Objects.equals(word, solution.word);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(word, x, y, vertical);
//    }
}
