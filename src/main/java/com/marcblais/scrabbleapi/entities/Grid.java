package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GridContent;
import jakarta.persistence.*;

import java.util.*;
import java.util.function.Supplier;

@Entity
public class Grid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 30)
    private String name;

    @Column(length = 1000)
    private String grid;

    @Column(length = 7)
    private String playerLetters;

    @ManyToOne
    private GridType gridType;

    @ManyToOne
    private Language language;

    @OneToOne
    @JsonIgnore
    private Player player;

    public Grid() {
    }

    public Grid(String name, String grid, String playerLetters, GridType gridType, Language language, Player player) {
        this.name = name;
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.gridType = gridType;
        this.language = language;
        this.player = player;
    }

    public Grid(long id, String name, String grid, String playerLetters, GridType gridType, Language language, Player player) {
        this.id = id;
        this.name = name;
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.gridType = gridType;
        this.language = language;
        this.player = player;
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
        String[] rows = new String[15];

        Arrays.fill(cols, "");
        Arrays.fill(rows, Arrays.toString(cols));

        this.grid = Arrays.toString(rows);
    }

    public List<GridContent> toGridContent() {
        List<GridContent> gridContents = new ArrayList<>();
        String[][] grid = getGrid();

        for (int y = 0; y < grid.length; y++) {
            StringBuilder content = new StringBuilder();

            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x].isEmpty()) {
                    content.append(".");
                } else {
                    content.append(grid[y][x]);
                }
            }

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

            gridContents.add(new GridContent(content.toString(), x, true));
        }

        return gridContents;
    }

    @Override
    public String toString() {
        return "Grid{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", grid='" + grid + '\'' +
                ", playerLetters='" + playerLetters + '\'' +
                ", gridType=" + gridType +
                ", language=" + language +
                '}';
    }
}
