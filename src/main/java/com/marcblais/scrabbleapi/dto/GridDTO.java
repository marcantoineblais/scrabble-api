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
    private String name;
    private String[][] grid;
    private List<String> playerLetters;
    private GridTypeDTO gridType;
    private Integer[][] blankTiles;
    private Language language;

    @JsonIgnore
    private Player player;

    public GridDTO(Grid grid) {
        this.id = grid.getId();
        this.name = grid.getName();
        this.grid = grid.toArray(grid.getGrid(), String[][].class);
        this.playerLetters = grid.getPlayerLettersList();
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
        this.playerLetters = new ArrayList<>(List.of("","","","","","",""));
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

    public List<GridRowCol> toGridRowColList() {
        List<GridRowCol> gridRowsCols = new ArrayList<>();
        List<GridRowCol> cols = new ArrayList<>();
        List<GridRowCol> rows = new ArrayList<>();

        for (int y = 0; y < grid.length; y++) {
            StringBuilder content = new StringBuilder();

            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x].isEmpty())
                    content.append(bonusOrLetter(y, x));
                else
                    content.append(grid[y][x]);
            }

            buildColsAndRows(rows, y, content, false);
        }

        for (int x = 0; x < grid[0].length; x++) {
            StringBuilder content = new StringBuilder();

            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x].isEmpty())
                    content.append(bonusOrLetter(y, x));
                else
                    content.append(grid[y][x]);
            }

            buildColsAndRows(cols, x, content, true);
        }

        gridRowsCols.addAll(rows);
        gridRowsCols.addAll(cols);
        return gridRowsCols;
    }

    public String bonusOrLetter(int y, int x) {
        String value;

        if (Arrays.stream(gridType.getDoubleLetter())
                .anyMatch(coords -> coords[0] == y && coords[1] == x))
            value = Bonus.DOUBLE_LETTER;
        else if (Arrays.stream(gridType.getTripleLetter())
                .anyMatch(coords -> coords[0] == y && coords[1] == x))
            value = Bonus.TRIPLE_LETTER;
        else if (Arrays.stream(gridType.getDoubleWord())
                .anyMatch(coords -> coords[0] == y && coords[1] == x))
            value = Bonus.DOUBLE_WORD;
        else if (Arrays.stream(gridType.getTripleWord())
                .anyMatch(coords -> coords[0] == y && coords[1] == x))
            value = Bonus.TRIPLE_WORD;
        else
            value = ".";

        return value;
    }

    private void buildColsAndRows(List<GridRowCol> gridRowsOrCols, int index, StringBuilder content, boolean vertical) {
        if (content.toString().matches("^.*[A-Z].*$")) {
            GridRowCol newGridRowCol = GridRowCol.builder()
                    .content(content.toString().split(""))
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
    }

    public Grid toGrid() {
        return new Grid(this);
    }

    @Override
    public String toString() {
        return "GridDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", grid=" + toJson(grid) +
                ", playerLetters='" + String.join("", playerLetters) + '\'' +
                ", gridType=" + gridType +
                ", blankTiles=" + toJson(blankTiles) + '\'' +
                ", language=" + language +
                '}';
    }
}
