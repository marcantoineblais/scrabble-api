package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.GameOptions;
import com.marcblais.scrabbleapi.dto.GridTypeDTO;
import com.marcblais.scrabbleapi.entities.GridType;
import com.marcblais.scrabbleapi.entities.Language;
import com.marcblais.scrabbleapi.services.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OptionsController {

    private OptionsService optionsService;

    @Autowired
    public OptionsController(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    @GetMapping("/options")
    public GameOptions getOptions() {
        List<Language> languages = optionsService.findAllLanguages();
        List<GridTypeDTO> gridTypes = optionsService.findAllGridTypes();

        languages.sort(Comparator.comparingLong(Language::getId));
        gridTypes.sort(Comparator.comparingLong(GridTypeDTO::getId));

        return new GameOptions(languages, gridTypes);
    }
}
