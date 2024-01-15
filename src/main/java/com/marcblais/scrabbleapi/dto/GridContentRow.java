package com.marcblais.scrabbleapi.dto;

import java.util.List;

public class GridContentRow {
    List<GridContent> gridContents;
    int index;

    public GridContentRow() {
    }

    public GridContentRow(List<GridContent> gridContents, int index) {
        this.gridContents = gridContents.stream().filter(c -> {
            return c.isVertical() && c.getX() == index;
        }).toList();

        this.index = index;
    }

    public List<GridContent> getGridContents() {
        return gridContents;
    }

    public void setGridContents(List<GridContent> gridContents) {
        this.gridContents = gridContents;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    
}
