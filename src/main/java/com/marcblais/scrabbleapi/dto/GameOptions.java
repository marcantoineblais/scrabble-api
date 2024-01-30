package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.GridType;
import com.marcblais.scrabbleapi.entities.Language;

import java.util.List;

public class GameOptions {
    private List<Language> languages;
    private List<GridTypeDTO> gridTypes;

    public GameOptions() {
    }

    public GameOptions(List<Language> languages, List<GridTypeDTO> gridTypes) {
        this.languages = languages;
        this.gridTypes = gridTypes;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<GridTypeDTO> getGridTypes() {
        return gridTypes;
    }

    public void setGridTypes(List<GridTypeDTO> gridTypes) {
        this.gridTypes = gridTypes;
    }
}
