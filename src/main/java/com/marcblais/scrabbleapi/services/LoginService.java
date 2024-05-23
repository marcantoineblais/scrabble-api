package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.dto.PlayerLogin;
import com.marcblais.scrabbleapi.encryption.PasswordEncoder;
import com.marcblais.scrabbleapi.entities.Player;
import com.marcblais.scrabbleapi.repositories.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final PlayerRepo playerRepo;
    private final JavaMailSender mailSender;

    @Autowired
    public LoginService(PlayerRepo playerRepo, JavaMailSender mailSender) {
        this.playerRepo = playerRepo;
        this.mailSender = mailSender;
    }

    public Player findPlayerByUsername(String username) {
        return playerRepo.findById(username).orElse(null);
    }

    public Player findPlayerByPlayerLogin(PlayerLogin loginRequest) {
        Player player = playerRepo.findById(loginRequest.getUsername()).orElse(null);

        if (player != null && PasswordEncoder.isEqual(loginRequest.getPassword(), player.getPassword()))
            return player;

        return null;
    }

    public boolean isUsernameTaken(String username) {
        return playerRepo.existsById(username);
    }

    public boolean isEmailTaken(String email) {
        return playerRepo.existsByEmail(email);
    }

    public void savePlayer(Player player) {
        if (player.getPassword() != null)
            playerRepo.save(player);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
