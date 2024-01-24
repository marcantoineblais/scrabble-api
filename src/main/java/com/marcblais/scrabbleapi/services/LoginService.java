package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.dto.PlayerLogin;
import com.marcblais.scrabbleapi.encryption.PasswordEncoder;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.repositories.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private PlayerRepo playerRepo;

    @Autowired
    public LoginService(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Player findPlayerByUsername(String username) {
        return playerRepo.findById(username).orElse(null);
    }

    public Player findPlayerByPlayerLogin(PlayerLogin loginRequest) {
        Player player = playerRepo.findById(loginRequest.getUsername()).orElse(null);

        if (player != null && player.isEnabled() &&
                PasswordEncoder.isEqual(loginRequest.getPassword(), player.getPassword()))
            return player;

        return null;
    }

    public void savePlayer(Player player) {
        player.setPassword(PasswordEncoder.encode(player.getPassword()));

        if (player.getPassword() != null)
            playerRepo.save(player);
    }
}
