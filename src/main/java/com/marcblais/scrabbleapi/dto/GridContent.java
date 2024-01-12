package com.marcblais.scrabbleapi.dto;

public class GridContent {
    private String content;
    private int x;
    private int y;
    private boolean isVertical;

    public GridContent() {
        this.content = "";
    }

    public GridContent(String content, int x, int y, boolean isVertical) {
        this.content = content;
        this.x = x;
        this.y = y;
        this.isVertical = isVertical;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
