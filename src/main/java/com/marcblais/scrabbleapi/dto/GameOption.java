package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.GridType;
import com.marcblais.scrabbleapi.entities.Language;

public class GameOption {
    private GridTypeDTO gridType;
    private Language language;
    private String name;

    public GameOption() {
    }

    public GameOption(GridTypeDTO gridType, Language language, String name) {
        this.gridType = gridType;
        this.language = language;
        this.name = name;
    }

    public GridTypeDTO getGridType() {
        return gridType;
    }

    public void setGridType(GridTypeDTO gridType) {
        this.gridType = gridType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GameOption{" +
                "gridType=" + gridType +
                ", language=" + language +
                ", name='" + name + '\'' +
                '}';
    }
}
