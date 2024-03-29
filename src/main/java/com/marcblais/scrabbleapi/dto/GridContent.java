package com.marcblais.scrabbleapi.dto;

import java.util.*;

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

    public GridContent(GridContent gridContent) {
        this.content = gridContent.getContent();
        this.index = gridContent.getIndex();
        this.vertical = gridContent.isVertical();
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

    public Map<Integer, List<String>> testPatterns(String playerLetters) {
        Map<Integer, List<String>> patternsMap = new HashMap<>();
        char[] contentCharArray = content.toCharArray();

        for (int i = 0; i < contentCharArray.length; i++) {
            List<String> patterns = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            boolean containsLetter = false;
            int remainingLetters = playerLetters.length();
            int startIndex = i;

            // Add every letters until reaching a blank space
            if (contentCharArray[i] != '.') {
                while (i < contentCharArray.length && contentCharArray[i] != '.') {
                    builder.append(contentCharArray[i++]);
                }

                containsLetter = true;
            }

            int j = i;

            // Add every pattern that contains at least one letter, every loop adds a new character to the pattern until max length is reached
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

                if (containsLetter && ((j < contentCharArray.length - 1 && contentCharArray[j + 1] == '.') ||
                                j == contentCharArray.length - 1))
                    patterns.add(builder.toString());

                j++;
            }

            if (remainingLetters == 0 && j < contentCharArray.length && contentCharArray[j] != '.') {
                builder.append(contentCharArray[j]);

                while (j < contentCharArray.length - 1 && contentCharArray[j + 1] != '.') {
                    builder.append(contentCharArray[++j]);
                }
                
                patterns.add(builder.toString());
            }

            if (!patterns.isEmpty())
                patternsMap.put(startIndex, patterns);
        }

        return patternsMap;
    }

    public void replaceContent(char newContent, int startIndex) {
        content = content.substring(0, startIndex) + newContent + content.substring(startIndex + 1);
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
