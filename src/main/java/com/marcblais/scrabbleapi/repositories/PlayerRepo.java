package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepo  extends JpaRepository<Player, String> {
    public boolean existsByEmail(String email);
}
