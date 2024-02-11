package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.PlayerDTO;
import com.marcblais.scrabbleapi.dto.PlayerLogin;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.entities.Role;
import com.marcblais.scrabbleapi.services.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class LoginController {
    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<PlayerDTO> login(@RequestBody PlayerLogin loginRequest, HttpServletResponse response) {
        Player player = loginService.findPlayerByPlayerLogin(loginRequest);

        if (player == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        String token = PlayerToken.createJwtForPlayer(player.getUsername());
        if (token == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        Cookie cookie = new Cookie("token", token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        if (loginRequest.isRememberMe())
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 jours
        response.addCookie(cookie);

        return new ResponseEntity<>(new PlayerDTO(player), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI("/"));

            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody PlayerLogin loginRequest) {
        Player player = new Player(loginRequest.getUsername(), loginRequest.getPassword());

        player.getRoles().add(new Role(player, "PLAYER"));
        loginService.savePlayer(player);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<Player> authenticate(@CookieValue(value = "token", required = false) String token) {
        if (token == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        String username = PlayerToken.getUsernameFromJwt(token);
        if (username == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        Player player = loginService.findPlayerByUsername(username);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }
}
