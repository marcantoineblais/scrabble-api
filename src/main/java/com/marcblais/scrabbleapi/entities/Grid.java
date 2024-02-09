package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GridDTO;
import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
public class Grid implements Comparable<Grid> {

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

    @ManyToOne
    @JsonIgnore
    private Player player;

    @JsonIgnore
    private LocalDateTime lastUpdate;

    public Grid() {
    }

    public Grid(
            String name,
            String grid,
            String playerLetters,
            GridType gridType,
            Language language,
            Player player,
            LocalDateTime lastUpdate
    ) {
        this.name = name;
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.gridType = gridType;
        this.language = language;
        this.player = player;
        this.lastUpdate = lastUpdate;
    }

    public Grid(
            long id,
            String name,
            String grid,
            String playerLetters,
            GridType gridType,
            Language language,
            Player player,
            LocalDateTime lastUpdate
    ) {
        this.id = id;
        this.name = name;
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.gridType = gridType;
        this.language = language;
        this.player = player;
        this.lastUpdate = lastUpdate;
    }

    public Grid(GridDTO gridDTO) {
        this.id = gridDTO.getId();
        this.name = gridDTO.getName();
        this.grid = gridDTO.gridToString();
        this.playerLetters = gridDTO.getPlayerLetters();
        this.gridType = gridDTO.getGridType().toGridType();
        this.player = gridDTO.getPlayer();
        this.language = gridDTO.getLanguage();
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

    public String getGrid() {
        return grid;
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grid grid)) return false;

        return id == grid.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int compareTo(Grid other) {
        return other.getLastUpdate().compareTo(lastUpdate);
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
