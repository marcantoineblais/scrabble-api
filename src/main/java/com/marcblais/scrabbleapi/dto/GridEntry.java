package com.marcblais.scrabbleapi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridEntry {
    private String entry;
    private int y;
    private int x;
    private boolean vertical;

    @Builder.Default
    private List<Integer> blankTiles = new ArrayList<>();


    public int getLastX() {
        return vertical ? x : x + entry.length() - 1;
    }

    public int getLastY() {
        return vertical ? y + entry.length() - 1 : y;
    }

    public boolean isBefore(int y, int x, boolean vertical) {
        if (this.vertical == vertical)
            return false;
        else if (this.vertical)
            return this.x == x && this.getLastY() == y - 1;
        else
            return this.y == y && this.getLastX() == x - 1;
    }

    public boolean isAfter(int y, int x, boolean vertical) {
        if (this.vertical == vertical)
            return false;
        else if (this.vertical)
            return this.x == x && this.getY() == y + 1;
        else
            return this.y == y && this.getX() == x + 1;
    }

    public void assignBlankTiles(GridRowCol gridRowCol) {
        for (Integer i : gridRowCol.getBlankTiles()) {
            if (vertical && i >= y && i <= getLastY())
                blankTiles.add(i - y);
            else if (!vertical && i >= x && i <= getLastX())
                blankTiles.add(i - x);
        }
    }

    @Override
    public String toString() {
        return "GridEntry{" +
                "entry='" + entry + '\'' +
                ", y=" + y +
                ", x=" + x +
                ", vertical=" + vertical +
                '}';
    }
}
