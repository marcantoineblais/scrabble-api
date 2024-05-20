package com.marcblais.scrabbleapi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjacentSolution {
    private String word;
    private int points;

    @Builder.Default
    List<Integer> blankTiles = new ArrayList<>();

    public void assignBlankTiles(GridEntry before, GridEntry after) {
        if (before != null)
            blankTiles.addAll(before.getBlankTiles());

        if (after != null) {
            for (Integer i : after.getBlankTiles()) {
                blankTiles.add(i + word.length() - after.getEntry().length());
            }
        }
    }

    @Override
    public String toString() {
        return "AdjacentSolution{" +
                "word='" + word + '\'' +
                ", points=" + points +
                ", blankTiles=" + blankTiles +
                '}';
    }
}
