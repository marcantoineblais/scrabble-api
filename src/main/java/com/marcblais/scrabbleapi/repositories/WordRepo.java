package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepo extends JpaRepository<Dictionary, Long> {

    public List<Dictionary> findByLanguage(Language language);
}
