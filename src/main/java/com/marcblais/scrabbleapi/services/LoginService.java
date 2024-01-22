package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.repositories.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private PlayerRepo playerRepo;
    private BCryptPasswordEncoder encoder;


    @Autowired
    public LoginService(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
        this.encoder = new BCryptPasswordEncoder(10);
    }

    public UserDetails loadPlayerByUsername(String username, String password) {
        UserDetails player = playerRepo.findById(username).orElse(null);

        if (player != null && player.getPassword().equals(encoder.encode(password)))
            return player;

        return null;
    }
}
