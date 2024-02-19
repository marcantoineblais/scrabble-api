package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.dto.GridDTO;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class GameService {
    private final GridRepo gridRepo;
    private final PlayerRepo playerRepo;

    @Autowired
    public GameService(GridRepo gridRepo, PlayerRepo playerRepo) {
        this.gridRepo = gridRepo;
        this.playerRepo = playerRepo;
    }

    public void saveGrid(Grid grid) {
        gridRepo.save(grid);
    }

    public void deleteGrid(Grid grid) {
        gridRepo.delete(grid);
    }

    public Player findPlayerByUsername(String username) {
        return playerRepo.findById(username).orElse(null);
    }
}
