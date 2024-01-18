package com.marcblais.scrabbleapi.dto;

import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Integer, List<String>> testPatterns() {
        Map<Integer, List<String>> patternsMap = new HashMap<>();
        char[] contentCharArray = content.toCharArray();

        for (int i = 0; i < contentCharArray.length; i++) {
            List<String> patterns = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            boolean containsLetter = false;
            int remainingLetters = 7;
            int startIndex = i;

            if (contentCharArray[i] != '.') {
                while (i < contentCharArray.length && contentCharArray[i] != '.') {
                    builder.append(contentCharArray[i++]);
                }

                containsLetter = true;
            }

            int j = i;

            while (remainingLetters > 0 && j < contentCharArray.length) {
                builder.append(contentCharArray[j]);

                if (contentCharArray[j] == '.')
                    remainingLetters -= 1;
                else {
                    while (j < contentCharArray.length - 1 && contentCharArray[j + 1] != '.') {
                        builder.append(contentCharArray[++j]);
                    }

                    containsLetter = true;
                }

                if (
                        containsLetter &&
                        (
                                (j < contentCharArray.length - 1 && contentCharArray[j + 1] == '.') ||
                                j == contentCharArray.length - 1
                        )
                )
                    patterns.add(builder.toString());

                j++;
            }

            if (!patterns.isEmpty())
                patternsMap.put(startIndex, patterns);
        }

        return patternsMap;
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
                builder.delete(0, builder.length());
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
