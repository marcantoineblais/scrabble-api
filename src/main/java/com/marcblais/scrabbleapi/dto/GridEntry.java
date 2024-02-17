package com.marcblais.scrabbleapi.dto;

public class GridEntry {
    private String entry;
    private int y;
    private int x;
    private boolean vertical;

    public GridEntry() {
    }

    public GridEntry(String entry, int y, int x, boolean vertical) {
        this.entry = entry;
        this.y = y;
        this.x = x;
        this.vertical = vertical;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getLastX() {
        return vertical ? x : x + entry.length() - 1;
    }

    public int getLastY() {
        return vertical ? y + entry.length() - 1 : y;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    @Override
    public String toString() {
        return "GridEntry{" +
                "entry='" + entry + '\'' +
                ", y=" + y +
                ", x=" + x +
                ", vertical=" + vertical +
                '}';
    }
}
