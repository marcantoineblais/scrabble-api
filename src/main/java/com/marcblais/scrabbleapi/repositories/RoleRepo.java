package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
