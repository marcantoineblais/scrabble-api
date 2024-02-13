package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridDTO {
    private long id;
    private String name;
    private String[][] grid;
    private String playerLetters;
    private GridTypeDTO gridType;
    private Integer[][] blankTiles;
    private Language language;

    @JsonIgnore
    private Player player;

    public GridDTO() {
    }

    public GridDTO(
            long id,
            String name,
            String[][] grid,
            String playerLetters,
            GridTypeDTO gridType,
            Integer[][] blankTiles,
            Language language,
            Player player
    ) {
        this.id = id;
        this.name = name;
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.gridType = gridType;
        this.blankTiles = blankTiles;
        this.language = language;
        this.player = player;
    }

    public GridDTO(Grid grid) {
        this.id = grid.getId();
        this.name = grid.getName();
        this.grid = grid.toArray(grid.getGrid(), String[][].class);
        this.playerLetters = grid.getPlayerLetters();
        this.player = grid.getPlayer();
        this.gridType = new GridTypeDTO(grid.getGridType());
        this.blankTiles = grid.toArray(grid.getBlankTiles(), Integer[][].class);
        this.language = grid.getLanguage();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setGrid(String[][] grid) {
        this.grid = grid;
    }

    public String getPlayerLetters() {
        return playerLetters;
    }

    public void setPlayerLetters(String playerLetters) {
        this.playerLetters = playerLetters;
    }

    public GridTypeDTO getGridType() {
        return gridType;
    }

    public void setGridType(GridTypeDTO gridType) {
        this.gridType = gridType;
    }

    public Integer[][] getBlankTiles() {
        return blankTiles;
    }

    public void setBlankTiles(Integer[][] blankTiles) {
        this.blankTiles = blankTiles;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void buildGrid() {
        String[] cols = new String[15];
        String[][] rows = new String[15][];

        Arrays.fill(cols, "");
        for (int i = 0; i < rows.length; i++) {
            rows[i] = Arrays.copyOf(cols, cols.length);
        }

        this.grid = rows;
    }

    public <T> String toJson(T value) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(value);
        } catch (Exception ex) {
            return "";
        }
    }

    public List<GridContent> toGridContent() {
        List<GridContent> gridContents = new ArrayList<>();

        for (int y = 0; y < grid.length; y++) {
            StringBuilder content = new StringBuilder();

            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x].isEmpty()) {
                    content.append(".");
                } else {
                    content.append(grid[y][x]);
                }
            }

            if (!content.toString().replace(".", "").isEmpty())
                gridContents.add(new GridContent(content.toString(), y, false));
        }

        for (int x = 0; x < grid[0].length; x++) {
            StringBuilder content = new StringBuilder();

            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x].isEmpty()) {
                    content.append(".");
                } else {
                    content.append(grid[y][x]);
                }
            }

            if (!content.toString().replace(".", "").isEmpty())
                gridContents.add(new GridContent(content.toString(), x, true));
        }

        return gridContents;
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
                ", playerLetters='" + playerLetters + '\'' +
                ", gridType=" + gridType +
                ", blankTiles=" + toJson(blankTiles) + '\'' +
                ", language=" + language +
                '}';
    }
}
