package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GridContent;
import com.marcblais.scrabbleapi.dto.GridDTO;
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

    public Grid(GridDTO grid) {
        this.id = grid.getId();
        this.name = grid.getName();
        this.grid = grid.gridToString();
        this.playerLetters = grid.getPlayerLetters();
        this.player = grid.getPlayer();
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


    public void setGrid(String grid) {
        this.grid = grid;
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

    public String[][] gridToArray() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(grid, String[][].class);
        } catch (Exception ex) {
            return new String[][]{};
        }
    }

    public GridDTO toGridDTO() {
        return new GridDTO(this);
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
