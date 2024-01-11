package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepo extends JpaRepository<Language, Long> {
}
