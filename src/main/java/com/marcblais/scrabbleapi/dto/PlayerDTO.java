package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.entities.Role;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDTO {
    private String username;
    private List<GridDTO> grids;

    public PlayerDTO() {
    }

    public PlayerDTO(String username, List<GridDTO> grids) {
        this.username = username;
        this.grids = grids;
    }

    public PlayerDTO(Player player) {
        this.username = player.getUsername();
        this.grids = player.getGrids().stream().map(Grid::toGridDTO).collect(Collectors.toList());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<GridDTO> getGrids() {
        return grids;
    }

    public void setGrids(List<GridDTO> grids) {
        this.grids = grids;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", grids=" + grids +
                '}';
    }
}
