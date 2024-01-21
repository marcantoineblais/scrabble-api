package com.marcblais.scrabbleapi.repositories;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface DictionaryEntryRepo extends JpaRepository<DictionaryEntry, Long> {
    public Set<DictionaryEntry> findByLanguage(Language language);
}
