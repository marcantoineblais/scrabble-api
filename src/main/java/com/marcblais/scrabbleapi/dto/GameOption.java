package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.GridType;
import com.marcblais.scrabbleapi.entities.Language;

public class GameOption {
    private GridType gridType;
    private Language language;
    private String name;

    public GameOption() {
    }

    public GameOption(GridType gridType, Language language, String name) {
        this.gridType = gridType;
        this.language = language;
        this.name = name;
    }

    public GridType getGridType() {
        return gridType;
    }

    public void setGridType(GridType gridType) {
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
