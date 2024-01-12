package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DictionaryEntryRepo extends JpaRepository<DictionaryEntry, Long> {

    public List<DictionaryEntry> findByLanguage(Language language);
}
