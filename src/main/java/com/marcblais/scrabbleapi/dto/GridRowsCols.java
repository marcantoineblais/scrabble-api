package com.marcblais.scrabbleapi.dto;

import java.util.*;

public class GridRowsCols {
    private String content;
    private int index;
    private boolean vertical;

    public GridRowsCols() {
    }

    public GridRowsCols(String content, int index, boolean vertical) {
        this.content = content;
        this.index = index;
        this.vertical = vertical;
    }

    public GridRowsCols(GridRowsCols gridRowsCols) {
        this.content = gridRowsCols.getContent();
        this.index = gridRowsCols.getIndex();
        this.vertical = gridRowsCols.isVertical();
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
        String[] lettersArray = content.split("");

        for (int i = 0; i < lettersArray.length; i++) {
            List<String> patterns = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            boolean containsLetter = false;
            int remainingLetters = playerLetters.length();
            int startIndex = i;

            // Add every letters until reaching a blank space
            if (lettersArray[i].matches("[A-Z]")) {
                while (i < lettersArray.length && lettersArray[i].matches("[A-Z]")) {
                    builder.append(lettersArray[i++]);
                }

                containsLetter = true;
            }

            int j = i;

            // Add every pattern that contains at least one letter, every loop adds a new character to the pattern until max length is reached
            while (remainingLetters > 0 && j < lettersArray.length) {
                builder.append(lettersArray[j]);

                if (!lettersArray[j].matches("[A-Z]"))
                    remainingLetters -= 1;
                else {
                    while (j < lettersArray.length - 1 && lettersArray[j + 1].matches("[A-Z]")) {
                        builder.append(lettersArray[++j]);
                    }

                    containsLetter = true;
                }

                if (containsLetter && ((j < lettersArray.length - 1 && !lettersArray[j + 1].matches("[A-Z]")) ||
                                j == lettersArray.length - 1))
                    patterns.add(builder.toString());

                j++;
            }

            if (remainingLetters == 0 && j < lettersArray.length && lettersArray[j].matches("[A-Z]")) {
                builder.append(lettersArray[j]);

                while (j < lettersArray.length - 1 && lettersArray[j + 1].matches("[A-Z]")) {
                    builder.append(lettersArray[++j]);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridRowsCols that)) return false;

        if (index != that.index) return false;
        return vertical == that.vertical;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (vertical ? 1 : 0);
        return result;
    }
}
