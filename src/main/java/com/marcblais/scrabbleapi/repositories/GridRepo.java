package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Grid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GridRepo extends JpaRepository<Grid, Long> {
}
