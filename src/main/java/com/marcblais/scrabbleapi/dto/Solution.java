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
public class Solution implements Comparable<Solution> {
    private DictionaryEntry entry;

    @JsonIgnore
    private GridRowCol gridRowCol;

    @JsonIgnore
    private Map<Integer, AdjacentSolution> adjacentSolutions;

    @JsonIgnore
    private String[] pattern;

    private boolean vertical;
    private int x;
    private int y;
    private int points;

    @Builder.Default
    private List<Integer> blankTiles = new ArrayList<>();


    public int getLastY() {
        return isVertical() ? y + entry.getWord().length() - 1 : y;
    }

    public int getLastX() {
        return isVertical() ? x : x + entry.getWord().length() - 1;
    }

    public void assignBlankTiles() {
        if (gridRowCol.getBlankTiles().isEmpty())
            return;

        for (Integer i : gridRowCol.getBlankTiles()) {
            if (vertical && i >= y && i <= getLastY())
                blankTiles.add(i - y);
            else if (!vertical && i >= x && i <= getLastX())
                blankTiles.add(i - x);
        }
    }

    @Override
    public int compareTo(Solution o) {
        if (o == null)
            return -1;

        if (points == o.getPoints())
            return entry.getWord().compareTo(o.getEntry().getWord());
        else
            return Integer.compare(o.getPoints(), points);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Solution solution)) return false;
        return vertical == solution.vertical &&
                x == solution.x && y == solution.y &&
                Objects.equals(entry, solution.entry) &&
                Objects.equals(blankTiles, solution.blankTiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, vertical, x, y, blankTiles);
    }

    @Override
    public String toString() {
        return "Solution{" +
                "entry=" + entry +
                ", gridRowsCols=" + gridRowCol +
                ", adjacentSolutions=" + adjacentSolutions +
                ", pattern='" + Arrays.toString(pattern) +
                ", vertical=" + vertical +
                ", x=" + x +
                ", y=" + y +
                ", points=" + points +
                ", blankTiles=" + blankTiles +
                '}';
    }
}
