package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Player;

import java.util.List;

public class LoginResponse {
    private String token;
    private Player player;

    public LoginResponse() {
    }

    public LoginResponse(String token, Player player) {
        this.token = token;
        this.player = player;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
