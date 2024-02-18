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

    public boolean isBefore(int y, int x, boolean vertical) {
        if (this.vertical == vertical)
            return false;
        else if (this.vertical)
            return this.x == x && this.getLastY() == y - 1;
        else
            return this.y == y && this.getLastX() == x - 1;
    }

    public boolean isAfter(int y, int x, boolean vertical) {
        if (this.vertical == vertical)
            return false;
        else if (this.vertical)
            return this.x == x && this.getY() == y + 1;
        else
            return this.y == y && this.getX() == x + 1;
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
