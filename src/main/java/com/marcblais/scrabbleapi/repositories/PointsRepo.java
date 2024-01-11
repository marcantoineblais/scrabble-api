package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.LettersValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsRepo extends JpaRepository<LettersValue, Long> {
    LettersValue findByLanguage(Language language);
}
