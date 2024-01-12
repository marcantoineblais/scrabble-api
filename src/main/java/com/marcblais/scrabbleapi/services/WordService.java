package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.entities.LettersValue;
import com.marcblais.scrabbleapi.entities.Dictionary;
import com.marcblais.scrabbleapi.repositories.LanguageRepo;
import com.marcblais.scrabbleapi.repositories.PointsRepo;
import com.marcblais.scrabbleapi.repositories.WordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {

    private final WordRepo wordRepo;
    private final LanguageRepo languageRepo;
    private final PointsRepo pointsRepo;

    @Autowired
    public WordService(WordRepo wordRepo, LanguageRepo languageRepo, PointsRepo pointsRepo) {
        this.wordRepo = wordRepo;
        this.languageRepo = languageRepo;
        this.pointsRepo = pointsRepo;
    }

    public void saveWord(Dictionary dictionary) {
        wordRepo.save(dictionary);
    }

    public List<Dictionary> findWordsByLanguage(Language language) {
        return wordRepo.findByLanguage(language);
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
