package com.marcblais.scrabbleapi.services;

import com.marcblais.scrabbleapi.dto.GridTypeDTO;
import com.marcblais.scrabbleapi.entities.GridType;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.repositories.GridTypeRepo;
import com.marcblais.scrabbleapi.repositories.LanguageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionsService {
    private LanguageRepo languageRepo;
    private GridTypeRepo gridTypeRepo;

    @Autowired
    public OptionsService(LanguageRepo languageRepo, GridTypeRepo gridTypeRepo) {
        this.languageRepo = languageRepo;
        this.gridTypeRepo = gridTypeRepo;
    }

    public List<Language> findAllLanguages() {
        return languageRepo.findAll();
    }

    public List<GridTypeDTO> findAllGridTypes() {
        return gridTypeRepo.findAll().stream().map(GridType::toGridTypeDTO).collect(Collectors.toList());
    }
}
