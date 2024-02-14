package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.PlayerDTO;
import com.marcblais.scrabbleapi.dto.PlayerLogin;
import com.marcblais.scrabbleapi.dto.SignInRequest;
import com.marcblais.scrabbleapi.encryption.EmailToken;
import com.marcblais.scrabbleapi.encryption.PasswordEncoder;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.Grid;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.entities.Role;
import com.marcblais.scrabbleapi.services.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class LoginController {
    private LoginService loginService;

    @Value("${backend.url}")
    private String domain;

    @Value("${email.address}")
    private String senderAddress;

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
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        if (loginRequest.isRememberMe())
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 jours
        response.addCookie(cookie);

        player.getGrids().sort(Grid::compareTo);
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
    public ResponseEntity<Void> signIn(@RequestBody SignInRequest request) {
        if (loginService.isUsernameTaken(request.getUsername()))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        Player player = new Player(request.getUsername(), request.getPassword());
        player.setPassword(PasswordEncoder.encode(player.getPassword()));
        player.getRoles().add(new Role(player, "PLAYER"));

        String token = EmailToken.createJwtForEmail(request.getUsername(), request.getEmail());
        String url = domain + "/validate?token=" + token;
        String subject = "Nouvelle demande d'inscription - Scrabble Cheetah";
        String body = "Vous avez reçu une nouvelle demande d'accès à Scrabble Cheetah: \n\n" +
                request.getInfo() + "\n\n" +
                "Accepter la demande : " + url;

        loginService.savePlayer(player);
        loginService.sendEmail(senderAddress, subject, body);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<PlayerDTO> authenticate(@CookieValue(value = "token", required = false) String token) {
        if (token == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        String username = PlayerToken.getUsernameFromJwt(token);
        if (username == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        Player player = loginService.findPlayerByUsername(username);
        if (player == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        player.getGrids().sort(Grid::compareTo);
        return new ResponseEntity<>(new PlayerDTO(player), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validatePlayer(@RequestParam("token") String token) {
        String email = EmailToken.getEmailFromJwt(token);
        String username = EmailToken.getUsernameFromJwt(token);

        if (email == null || username == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Player player = loginService.findPlayerByUsername(username);
        if (player == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String subject = "Demande d'accès à Scrabble Cheetah";
        String body = "Bonjour,\n\nVotre demande d'inscription à été approuvée.\n\n" +
                "Si vous avez des questions ou commentaires, vous pouvez me contacter à " + senderAddress + ".\n\n" +
                "Vous pouvez essayer l'application dès maintenant en cliquant ici : " + domain + "\n\n" +
                "Merci pour l'intérêt que vous démontrer à Scrabble Cheetah, la meilleure app pour tricher au Scrabble.";

        player.setEnabled(true);
        loginService.savePlayer(player);
        loginService.sendEmail(email, subject, body);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
