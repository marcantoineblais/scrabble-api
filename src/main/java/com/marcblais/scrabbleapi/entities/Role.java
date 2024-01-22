package com.marcblais.scrabbleapi.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Player player;

    @Column(length = 20)
    private String name;

    public Role() {
    }

    public Role(long id, Player player, String name) {
        this.id = id;
        this.player = player;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
