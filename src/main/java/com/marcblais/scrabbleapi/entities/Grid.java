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

    @Column(length = 7, nullable = false)
    private String playerLetters;

    @ManyToOne
    private GridType gridType;

    @Column(length = 200)
    private String blankTiles;

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
            String blankTiles,
            Language language,
            Player player,
            LocalDateTime lastUpdate
    ) {
        this.name = name;
        this.grid = grid;
        this.playerLetters = playerLetters;
        this.gridType = gridType;
        this.blankTiles = blankTiles;
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
        this.grid = gridDTO.toJson(gridDTO.getGrid());
        this.playerLetters = String.join("", gridDTO.getPlayerLetters());
        this.gridType = gridDTO.getGridType().toGridType();
        this.blankTiles = gridDTO.toJson(gridDTO.getBlankTiles());
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

    public String getBlankTiles() {
        return blankTiles;
    }

    public void setBlankTiles(String blankTiles) {
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public <T> T[][] toArray(String value, Class<T[][]> tclass) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(value, tclass);
        } catch (Exception ex) {
            return null;
        }
    }

    public String[] getPlayerLettersArray() {
        String[] playerLettersArray = new String[7];
        String[] partialArray;

        if (playerLetters != null)
            partialArray = playerLetters.split("");
        else
            partialArray = new String[0];

        for (int i = 0; i < playerLettersArray.length; i++) {
            if (i < partialArray.length) {
                playerLettersArray[i] = partialArray[i];
            } else {
                playerLettersArray[i] = "";
            }
        }

        return playerLettersArray;
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
        return Long.hashCode(id);
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
