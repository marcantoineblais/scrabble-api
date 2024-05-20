package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridRowCol {
    private String[] content;
    private String[] bonusContent;
    private int index;
    private boolean vertical;

    @JsonIgnore
    private GridRowCol previousGridRowCol;

    @JsonIgnore
    private GridRowCol nextGridRowCol;

    @JsonIgnore
    @Builder.Default
    private List<Integer> blankTiles = new ArrayList<>();


    public Map<Integer, List<List<String>>> testPatterns(String[] playerLetters) {
        Map<Integer, List<List<String>>> patternsMap = new HashMap<>();
        String[] previousLettersArray = previousGridRowCol == null ? null : previousGridRowCol.getContent();
        String[] nextLettersArray = nextGridRowCol == null ? null : nextGridRowCol.getContent();

        for (int i = 0; i < content.length; i++) {
            List<List<String>> patterns = new ArrayList<>();
            List<String> pattern = new ArrayList<>();
            boolean isValid = false;
            int remainingLetters = playerLetters.length;
            int startIndex = i;

            // Add every letters until reaching a blank space
            if (!content[i].equals(".")) {
                while (i < content.length && !content[i].equals(".")) {
                    pattern.add(content[i++]);
                }

                isValid = true;
            }

            int j = i;

            // Add every pattern that contains at least one letter, every loop adds a new character to the pattern until max length is reached
            while (remainingLetters > 0 && j < content.length) {
                pattern.add(content[j]);

                if (content[j].equals(".")) {
                    remainingLetters -= 1;

                    if (((previousLettersArray != null && !previousLettersArray[j].equals(".")) ||
                            (nextLettersArray != null && !nextLettersArray[j].equals("."))) &&
                            ((j < content.length - 1 && content[j + 1].equals(".")) ||
                            j == content.length - 1))
                        isValid = true;
                } else {
                    while (j < content.length - 1 && !content[j + 1].equals(".")) {
                        pattern.add(content[++j]);
                    }

                    isValid = true;
                }

                if (isValid && ((j < content.length - 1 && content[j + 1].equals(".")) ||
                                j == content.length - 1))
                    patterns.add(List.copyOf(pattern));

                j++;
            }

            if (remainingLetters == 0 && j < content.length && !content[j].equals(".")) {
                pattern.add(content[j]);

                while (j < content.length - 1 && !content[j + 1].equals(".")) {
                    pattern.add(content[++j]);
                }
                
                patterns.add(List.copyOf(pattern));
            }

            if (!patterns.isEmpty())
                patternsMap.put(startIndex, patterns);
        }

        return patternsMap;
    }


    public List<GridEntry> toGridEntriesList() {
        List<GridEntry> entries = new ArrayList<>();
        int i = 0;

        while (i < content.length) {
            if (!content[i].equals(".")) {
                GridEntry entry = GridEntry.builder()
                        .y(vertical ? i : index)
                        .x(vertical ? index : i)
                        .vertical(vertical)
                        .build();

                StringBuilder builder = new StringBuilder();
                builder.append(content[i]);

                if (blankTiles.contains(i))
                    entry.getBlankTiles().add(entry.isVertical() ? i - entry.getY() : i - entry.getX());

                i++;

                while (i < content.length && !content[i].equals(".")) {
                    builder.append(content[i]);

                    if (blankTiles.contains(i))
                        entry.getBlankTiles().add(entry.isVertical() ? i - entry.getY() : i - entry.getX());

                    i++;
                }

                entry.setEntry(builder.toString());
                entries.add(entry);
            }

            i++;
        }

        return entries;
    }

    @Override
    public String toString() {
        return "GridRowCol{" +
                "content='" + Arrays.toString(content) +
                ", index=" + index +
                ", vertical=" + vertical +
                ", blankTiles=" + blankTiles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridRowCol that)) return false;

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
