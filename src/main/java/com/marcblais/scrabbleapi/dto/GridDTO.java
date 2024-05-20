package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Player;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridDTO {
    private long id;
    private String uuid;
    private String name;
    private String[][] grid;
    private String[] playerLetters;
    private GridTypeDTO gridType;
    private Integer[][] blankTiles;
    private Language language;

    @JsonIgnore
    private Player player;

    public GridDTO(Grid grid) {
        this.id = grid.getId();
        this.uuid = grid.getUuid();
        this.name = grid.getName();
        this.grid = grid.toArray(grid.getGrid(), String[][].class);
        this.playerLetters = grid.getPlayerLettersArray();
        this.player = grid.getPlayer();
        this.gridType = new GridTypeDTO(grid.getGridType());
        this.blankTiles = grid.toArray(grid.getBlankTiles(), Integer[][].class);
        this.language = grid.getLanguage();
    }

    public void buildGrid() {
        String[] cols = new String[15];
        String[][] rows = new String[15][];

        Arrays.fill(cols, "");
        for (int i = 0; i < rows.length; i++) {
            rows[i] = Arrays.copyOf(cols, cols.length);
        }

        this.grid = rows;
        this.playerLetters = new String[]{"","","","","","",""};
        this.blankTiles = new Integer[][]{};
    }

    public <T> String toJson(T value) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(value);
        } catch (Exception ex) {
            return "";
        }
    }

    public String[][] getGridWithBonusPatterns() {
        String[][] gridWithBonusPatterns = new String[15][15];
        for (int y = 0; y < gridWithBonusPatterns.length; y++) {
            for (int x = 0; x < gridWithBonusPatterns[y].length; x++) {
                if (grid[y][x].isEmpty())
                    gridWithBonusPatterns[y][x] = ".";
                else
                    gridWithBonusPatterns[y][x] = grid[y][x];
            }
        }

        for (Integer[] coord : gridType.getDoubleLetter()) {
            Integer y = coord[0];
            Integer x = coord[1];

            if (gridWithBonusPatterns[y][x].equals("."))
                gridWithBonusPatterns[y][x] = Bonus.DOUBLE_LETTER;
        }

        for (Integer[] coord : gridType.getTripleLetter()) {
            Integer y = coord[0];
            Integer x = coord[1];

            if (gridWithBonusPatterns[y][x].equals("."))
                gridWithBonusPatterns[y][x] = Bonus.TRIPLE_LETTER;
        }

        for (Integer[] coord : gridType.getDoubleWord()) {
            Integer y = coord[0];
            Integer x = coord[1];

            if (gridWithBonusPatterns[y][x].equals("."))
                gridWithBonusPatterns[y][x] = Bonus.DOUBLE_WORD;
        }

        for (Integer[] coord : gridType.getTripleWord()) {
            Integer y = coord[0];
            Integer x = coord[1];

            if (gridWithBonusPatterns[y][x].equals("."))
                gridWithBonusPatterns[y][x] = Bonus.TRIPLE_WORD;
        }

        return gridWithBonusPatterns;
    }

    public List<GridRowCol> toGridRowColList() {
        List<GridRowCol> gridRowsCols = new ArrayList<>();
        List<GridRowCol> cols = new ArrayList<>();
        List<GridRowCol> rows = new ArrayList<>();
        String[][] gridWithBonusPatterns = getGridWithBonusPatterns();

        for (int y = 0; y < grid.length; y++) {
            String[] content = new String[15];
            String[] bonus = new String[15];

            for (int x = 0; x < grid[y].length; x++) {
                content[x] = grid[y][x].isEmpty() ? "." : grid[y][x];
                bonus[x] = gridWithBonusPatterns[y][x];
            }

            buildColsAndRows(rows, y, content, bonus, false);
        }

        for (int x = 0; x < grid[0].length; x++) {
            String[] content = new String[15];
            String[] bonus = new String[15];

            for (int y = 0; y < grid.length; y++) {
                content[x] = grid[y][x].isEmpty() ? "." : grid[y][x];
                bonus[x] = gridWithBonusPatterns[y][x];
            }

            buildColsAndRows(cols, x, content, bonus, true);
        }

        gridRowsCols.addAll(rows);
        gridRowsCols.addAll(cols);
        return gridRowsCols;
    }

    private void buildColsAndRows(List<GridRowCol> gridRowsOrCols, int index, String[] content, String[] bonus, boolean vertical) {
        GridRowCol newGridRowCol = GridRowCol.builder()
                .content(content)
                .bonusContent(bonus)
                .index(index)
                .vertical(vertical)
                .build();

        newGridRowCol.setBlankTiles(Arrays.stream(blankTiles)
                .filter(bt -> newGridRowCol.isVertical() ? newGridRowCol.getIndex() == bt[1] : newGridRowCol.getIndex() == bt[0])
                .map(bt -> newGridRowCol.isVertical() ? bt[0] : bt[1])
                .toList()
        );

        if (!gridRowsOrCols.isEmpty()) {
            GridRowCol previousGridRowCol = gridRowsOrCols.getLast();
            previousGridRowCol.setNextGridRowCol(newGridRowCol);
            newGridRowCol.setPreviousGridRowCol(previousGridRowCol);
        }

        gridRowsOrCols.add(newGridRowCol);
    }

    public Grid toGrid() {
        return new Grid(this);
    }
}
