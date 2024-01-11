package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepo extends JpaRepository<Word, Long> {

    public List<Word> findByLanguage(Language language);
}
