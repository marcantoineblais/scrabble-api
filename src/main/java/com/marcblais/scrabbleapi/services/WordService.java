package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.LettersValue;
import com.marcblais.scrabbleapi.entities.DictionaryEntry;
import com.marcblais.scrabbleapi.repositories.LanguageRepo;
import com.marcblais.scrabbleapi.repositories.PointsRepo;
import com.marcblais.scrabbleapi.repositories.DictionaryEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {

    private final DictionaryEntryRepo dictionaryEntryRepo;
    private final LanguageRepo languageRepo;
    private final PointsRepo pointsRepo;

    @Autowired
    public WordService(DictionaryEntryRepo dictionaryEntryRepo, LanguageRepo languageRepo, PointsRepo pointsRepo) {
        this.dictionaryEntryRepo = dictionaryEntryRepo;
        this.languageRepo = languageRepo;
        this.pointsRepo = pointsRepo;
    }

    public void saveWord(DictionaryEntry dictionaryEntry) {
        dictionaryEntryRepo.save(dictionaryEntry);
    }

    public List<DictionaryEntry> findAllEntries() {
        return dictionaryEntryRepo.findAll();
    }

    public List<DictionaryEntry> findWordsByLanguage(Language language) {
        return dictionaryEntryRepo.findByLanguage(language);
    }

    public void saveLanguage(Language language) {
        languageRepo.save(language);
    }

    public Language findLanguageById(long id) {
        return languageRepo.findById(id).orElse(null);
    }

    public void saveLettersValue(LettersValue lettersValue) {
        pointsRepo.save(lettersValue);
    }

    public LettersValue findLettersValueByLanguage(Language language) {
        return pointsRepo.findByLanguage(language);
    }
}
