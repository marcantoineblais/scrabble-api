package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.entities.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    private String username;

    @Builder.Default
    private List<GridDTO> grids = new ArrayList<>();

    public PlayerDTO(Player player) {
        this.username = player.getUsername();
        this.grids = player.getGrids().stream().map(Grid::toGridDTO).collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", grids=" + grids +
                '}';
    }
}
