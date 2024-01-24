package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.LoginResponse;
import com.marcblais.scrabbleapi.dto.PlayerLogin;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.entities.Role;
import com.marcblais.scrabbleapi.services.LoginService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    private PlayerToken playerToken;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
        this.playerToken = new PlayerToken();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody PlayerLogin loginRequest) {
        Player player = loginService.findPlayerByPlayerLogin(loginRequest);
        if (player == null)
            return null;

        String token = playerToken.createJwtForPlayer(player.getUsername());
        if (token == null)
            return null;

        return new LoginResponse(token, player);
    }

    @PostMapping("/signin")
    public String signin(@RequestBody PlayerLogin loginRequest) {
        Player player = new Player(loginRequest.getUsername(), loginRequest.getPassword());

        player.getRoles().add(new Role(player, "PLAYER"));
        loginService.savePlayer(player);

        return "OK";
    }

    @PostMapping("/authenticate")
    public LoginResponse authenticate(@RequestBody String token) {
        String username = playerToken.getUsernameFromJwt(token);
        if (username == null)
            return null;

        Player player = loginService.findPlayerByUsername(username);
        if (player == null)
            return null;

        String updatedToken = playerToken.createJwtForPlayer(username);
        return new LoginResponse(updatedToken, player);
    }
}
