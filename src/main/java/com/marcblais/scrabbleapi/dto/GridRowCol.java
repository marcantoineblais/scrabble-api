package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
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

    public Map<Integer,  List<Pattern>> testPatterns(String[] playerLetters) {
        return testPatterns(playerLetters, false);
    }

    public Map<Integer, List<Pattern>> testPatterns(String[] playerLetters, boolean alwaysValid) {
        Map<Integer, List<Pattern>> patternsMap = new HashMap<>();
        String[] previousLettersArray = previousGridRowCol == null ? null : previousGridRowCol.getContent();
        String[] nextLettersArray = nextGridRowCol == null ? null : nextGridRowCol.getContent();

        for (int i = 0; i < content.length; i++) {
            List<Pattern> patterns = new ArrayList<>();
            StringBuilder regexBuilder = new StringBuilder();
            StringBuilder bonusBuilder = new StringBuilder();
            boolean isValid = alwaysValid;
            int remainingLetters = playerLetters.length;
            int startIndex = i;

            // Add every letters until reaching a blank space
            if (!content[i].equals(".")) {
                while (i < content.length && !content[i].equals(".")) {
                    bonusBuilder.append(bonusContent[i]);
                    regexBuilder.append(content[i]);
                    i++;
                }

                isValid = true;
            }

            int j = i;

            // Add every pattern that contains at least one letter, every loop adds a new character to the pattern until max length is reached
            while (remainingLetters > 0 && j < content.length) {
                bonusBuilder.append(bonusContent[j]);
                regexBuilder.append(content[j]);

                if (content[j].equals(".")) {
                    remainingLetters -= 1;

                    if (((previousLettersArray != null && !previousLettersArray[j].equals(".")) ||
                            (nextLettersArray != null && !nextLettersArray[j].equals("."))) &&
                            ((j < content.length - 1 && content[j + 1].equals(".")) ||
                            j == content.length - 1))
                        isValid = true;
                } else {
                    while (j < content.length - 1 && !content[j + 1].equals(".")) {
                        j++;
                        regexBuilder.append(content[j]);
                        bonusBuilder.append(bonusContent[j]);
                    }

                    isValid = true;
                }

                if (isValid && ((j < content.length - 1 && content[j + 1].equals(".")) ||
                                j == content.length - 1)) {
                    patterns.add(Pattern.builder()
                            .regex(regexBuilder.toString())
                            .bonus(bonusBuilder.toString())
                            .build());
                }

                j++;
            }

            if (remainingLetters == 0 && j < content.length && !content[j].equals(".")) {
                bonusBuilder.append(bonusContent[j]);
                regexBuilder.append(content[j]);

                while (j < content.length - 1 && !content[j + 1].equals(".")) {
                    regexBuilder.append(content[++j]);
                    bonusBuilder.append(bonusContent[j]);
                }

                patterns.add(Pattern.builder()
                        .regex(regexBuilder.toString())
                        .bonus(bonusBuilder.toString())
                        .build());
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
                StringBuilder builder = new StringBuilder(content[i]);
                List<Integer> entryBlankTiles = new ArrayList<>();
                int y = vertical ? i : index;
                int x = vertical ? index : i;

                if (blankTiles.contains(i))
                    entryBlankTiles.add(0);

                i++;

                while (i < content.length && !content[i].equals(".")) {
                    builder.append(content[i]);

                    if (blankTiles.contains(i))
                        entryBlankTiles.add(vertical ? i - y : i - x);

                    i++;
                }

                entries.add(GridEntry.builder()
                        .entry(builder.toString())
                        .y(y)
                        .x(x)
                        .vertical(vertical)
                        .blankTiles(entryBlankTiles)
                        .build()
                );
            }

            i++;
        }

        return entries;
    }

    @Override
    public String toString() {
        String previous = previousGridRowCol == null ? null : String.valueOf(previousGridRowCol.getIndex());
        String next = nextGridRowCol == null ? null : String.valueOf(nextGridRowCol.getIndex());

        return "GridRowCol{" +
                "content='" + Arrays.toString(content) +
                ", index=" + index +
                ", vertical=" + vertical +
                ", blankTiles=" + blankTiles +
                ", previousGridRowCol=" + previous +
                ", nextGridRowCol=" + next +
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
