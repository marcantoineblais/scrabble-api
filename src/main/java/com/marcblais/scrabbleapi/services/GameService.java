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
    private final PlayerRepo playerRepo;

    @Autowired
    public GameService(
            DictionaryEntryRepo dictionaryEntryRepo,
            LanguageRepo languageRepo,
            PointsRepo pointsRepo,
            GridRepo gridRepo,
            GridTypeRepo gridTypeRepo,
            PlayerRepo playerRepo
    ) {
        this.dictionaryEntryRepo = dictionaryEntryRepo;
        this.languageRepo = languageRepo;
        this.pointsRepo = pointsRepo;
        this.gridRepo = gridRepo;
        this.gridTypeRepo = gridTypeRepo;
        this.playerRepo = playerRepo;
    }

    public Set<DictionaryEntry> findWordsByLanguage(Language language) {
        return dictionaryEntryRepo.findByLanguage(language);
    }

    public LettersValue findLettersValueByLanguage(Language language) {
        return pointsRepo.findByLanguage(language);
    }

    public void saveGrid(Grid grid) {
        gridRepo.save(grid);
    }

    public Grid findGridById(long id) {
        return gridRepo.findById(id).orElse(null);
    }

    public Player findPlayerByUsername(String username) {
        return playerRepo.findById(username).orElse(null);
    }

    public Language findLanguageByName(String name) {
        return languageRepo.findByName(name).orElse(null);
    }
}
