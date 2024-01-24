package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.GridType;
import com.marcblais.scrabbleapi.entities.Language;

import java.util.List;

public class GameOptions {
    private List<Language> languages;
    private List<GridType> gridTypes;

    public GameOptions() {
    }

    public GameOptions(List<Language> languages, List<GridType> gridTypes) {
        this.languages = languages;
        this.gridTypes = gridTypes;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<GridType> getGridTypes() {
        return gridTypes;
    }

    public void setGridTypes(List<GridType> gridTypes) {
        this.gridTypes = gridTypes;
    }
}
