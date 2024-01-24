package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Player {
    @Id
    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 77, nullable = false)
    @JsonIgnore
    private String password;

    @OneToMany
    @JoinColumn
    private List<Grid> grids;

    @OneToMany
    @JoinColumn(referencedColumnName = "username", name = "player_username")
    @JsonIgnore
    private List<Role> roles;

    private boolean enabled;

    public Player() {
    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.grids = new ArrayList<>();
        this.roles = new ArrayList<>();
        this.enabled = false;
    }

    public Player(String username, String password, List<Grid> grids, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.grids = grids;
        this.roles = roles;
        this.enabled = false;
    }

    public Player(String username, String password, List<Grid> grids, List<Role> roles, boolean enabled) {
        this.username = username;
        this.password = password;
        this.grids = grids;
        this.roles = roles;
        this.enabled = enabled;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Grid> getGrids() {
        return grids;
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", grids=" + grids +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
