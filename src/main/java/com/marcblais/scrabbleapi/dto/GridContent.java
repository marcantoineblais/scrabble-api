package com.marcblais.scrabbleapi.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridContent {
    private String content;
    private int index;
    private boolean vertical;

    public GridContent() {
    }

    public GridContent(String content, int index, boolean vertical) {
        this.content = content;
        this.index = index;
        this.vertical = vertical;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public List<String> testPatterns() {
        List<String> patterns = new ArrayList<>();
        List<String> splits = splitContent();

        for (int i = 0; i < splits.size(); i++) {
            
        }

        return null;
    }

    public List<String> splitContent() {
        List<String> splits = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        for (char s : content.toCharArray()) {
            if (builder.isEmpty())
                builder.append(s);
            else if (builder.charAt(0) == ' ' && s == ' ')
                builder.append(s);
            else if (builder.charAt(0) != ' ' && s != ' ')
                builder.append(s);
            else {
                splits.add(builder.toString());
                builder = new StringBuilder();;
                builder.append(s);
            }
        }
        splits.add(builder.toString());

        return splits;
    }

    @Override
    public String toString() {
        return "GridContent{" +
                "content='" + content + '\'' +
                ", index=" + index +
                ", vertical=" + vertical +
                '}';
    }
}
