package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class LoginController {
    private LoginService loginService;

    @Autowired

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public Player login(@RequestBody HashMap<String, String> credentials) {
        return (Player) loginService.loadPlayerByUsername(credentials.get("username"), credentials.get("password"));
    }
}
