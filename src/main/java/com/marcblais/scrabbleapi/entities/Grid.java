package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GridContent;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Grid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String grid;
    private String playerLetters;

    @ManyToOne
    private GridType gridType;

    public Grid() {
    }

    public Grid(String grid, String playerLetters, GridType gridType) {
        this.grid = grid;
        this.playerLetters = playerLetters.toUpperCase();
        this.gridType = gridType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[][] getGrid() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(grid, String[][].class);
        } catch (Exception ex) {
            return new String[][]{};
        }
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public void setGrid(String[][] grid) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            setGrid(mapper.writeValueAsString(grid));
        } catch (Exception ex) {
            return;
        }
    }

    public String getPlayerLetters() {
        return playerLetters;
    }

    public void setPlayerLetters(String playerLetters) {
        this.playerLetters = playerLetters.toUpperCase();
    }

    public GridType getGridType() {
        return gridType;
    }

    public void setGridType(GridType gridType) {
        this.gridType = gridType;
    }

    public List<GridContent> toGridContent() {
        List<GridContent> gridContent = new ArrayList<>();
        String[][] grid = getGrid();

        for (int y = 0; y < grid.length; y++) {
            String[] row = grid[y];
            String horizontal;
            String vertical;

            for (int x = 0; x < row.length; x++) {
                String col = row[x];

                if (!col.isEmpty()) {
                    horizontal = evaluateRow(grid, x, y);
                    vertical = evaluateCol(grid, x, y);

                    if (!horizontal.isBlank())
                        gridContent.add(new GridContent(horizontal, x, y,false));
                    if (!vertical.isBlank())
                        gridContent.add(new GridContent(vertical, x, y,true));
                }
            }
        }

        return gridContent;
    }

    private String evaluateRow(String[][] grid, int x, int y) {
        String[] row = grid[y];
        StringBuilder gridContent = new StringBuilder();

        if (x == 0 || row[x - 1].isBlank()) {
            while (x < row.length && !row[x].isEmpty()) {
                gridContent.append(row[x++]);
            }
        }

        return gridContent.toString();
    }

    private String evaluateCol(String[][] grid, int x, int y) {
        StringBuilder gridContent = new StringBuilder();

        if (y == 0 || grid[y - 1][x].isBlank()) {
            while (y < grid.length && !grid[y][x].isEmpty()) {
                gridContent.append(grid[y++][x]);
            }
        }

        return gridContent.toString();
    }

    @Override
    public String toString() {
        return "Grid{" +
                "grid=" + grid +
                ", playerLetters=" + playerLetters +
                ", gridType=" + gridType +
                '}';
    }
}
