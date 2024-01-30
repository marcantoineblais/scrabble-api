package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepo extends JpaRepository<Language, Long> {
    public Optional<Language> findByName(String name);
}
