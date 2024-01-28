package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class GameService {

    private final DictionaryEntryRepo dictionaryEntryRepo;
    private final LanguageRepo languageRepo;
    private final PointsRepo pointsRepo;
    private final GridRepo gridRepo;
    private final GridTypeRepo gridTypeRepo;

    @Autowired
    public GameService(
            DictionaryEntryRepo dictionaryEntryRepo,
            LanguageRepo languageRepo,
            PointsRepo pointsRepo,
            GridRepo gridRepo,
            GridTypeRepo gridTypeRepo
    ) {
        this.dictionaryEntryRepo = dictionaryEntryRepo;
        this.languageRepo = languageRepo;
        this.pointsRepo = pointsRepo;
        this.gridRepo = gridRepo;
        this.gridTypeRepo = gridTypeRepo;
    }

    public void saveWord(DictionaryEntry dictionaryEntry) {
        dictionaryEntryRepo.save(dictionaryEntry);
    }

    public Set<DictionaryEntry> findAllEntries() {
        return new HashSet<>(dictionaryEntryRepo.findAll());
    }

    public Set<DictionaryEntry> findWordsByLanguage(Language language) {
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

    public void saveGrid(Grid grid) {
        gridRepo.save(grid);
    }

    public void saveGridType(GridType gridType) {
        gridTypeRepo.save(gridType);
    }

    public Grid findGridById(long id) {
        return gridRepo.findById(id).orElse(null);
    }
}
