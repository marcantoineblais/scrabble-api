package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.GridType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GridTypeRepo extends JpaRepository<GridType, Long> {
}
