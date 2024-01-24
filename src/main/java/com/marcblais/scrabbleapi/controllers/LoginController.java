package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.entities.Role;
import com.marcblais.scrabbleapi.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class LoginController {
    private LoginService loginService;

    @Autowired

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    @CrossOrigin("http://localhost:3000")
    public Player login(@RequestBody HashMap<String, String> credentials) {
        return loginService.loadPlayerByUsername(credentials.get("username"), credentials.get("password"));
    }

    @PostMapping("/signin")
    @CrossOrigin("http://localhost:3000")
    public Player signin(@RequestBody HashMap<String, String> credentials) {
        Player player = new Player(
                credentials.get("username"),
                credentials.get("password")
        );

        player.getRoles().add(new Role(player, "PLAYER"));
        loginService.savePlayer(player);

        return player;
    }
}
