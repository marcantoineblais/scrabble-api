package com.marcblais.scrabbleapi.dto;

public class GridContent {
    private String content;
    private int x;
    private int y;
    private boolean vertical;

    public GridContent() {
        this.content = "";
    }

    public GridContent(String content, int x, int y, boolean vertical) {
        this.content = content;
        this.x = x;
        this.y = y;
        this.vertical = vertical;
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
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public int lastX() {
        return vertical ? x : x + content.length() - 1;
    }

    public int lastY() {
        return vertical ? y + content.length() - 1 : y;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
