package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GridDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Grid implements Comparable<Grid> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 30)
    private String uuid;

    @Column(length = 30)
    private String name;

    @Column(length = 1000)
    private String grid;

    @Column(length = 50)
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

    public Grid(GridDTO gridDTO) {
        this.id = gridDTO.getId();
        this.uuid = gridDTO.getUuid();
        this.name = gridDTO.getName();
        this.grid = gridDTO.toJson(gridDTO.getGrid());
        this.playerLetters = gridDTO.toJson(gridDTO.getPlayerLetters());
        this.gridType = gridDTO.getGridType().toGridType();
        this.blankTiles = gridDTO.toJson(gridDTO.getBlankTiles());
        this.player = gridDTO.getPlayer();
        this.language = gridDTO.getLanguage();
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
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(playerLetters, String[].class);
        } catch (Exception ex) {
            return null;
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
