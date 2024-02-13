package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.entities.GridType;

public class GridTypeDTO {
    long id;
    private Integer[][] doubleLetter;
    private Integer[][] tripleLetter;
    private Integer[][] doubleWord;
    private Integer[][] tripleWord;

    public GridTypeDTO() {
    }

    public GridTypeDTO(long id, Integer[][] doubleLetter, Integer[][] tripleLetter, Integer[][] doubleWord, Integer[][] tripleWord) {
        this.id = id;
        this.doubleLetter = doubleLetter;
        this.tripleLetter = tripleLetter;
        this.doubleWord = doubleWord;
        this.tripleWord = tripleWord;
    }

    public GridTypeDTO(GridType gridType) {
        this.id = gridType.getId();
        this.doubleLetter = gridType.bonusToIntArray(gridType.getDoubleLetter());
        this.tripleLetter = gridType.bonusToIntArray(gridType.getTripleLetter());
        this.doubleWord = gridType.bonusToIntArray(gridType.getDoubleWord());
        this.tripleWord = gridType.bonusToIntArray(gridType.getTripleWord());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer[][] getDoubleLetter() {
        return doubleLetter;
    }

    public void setDoubleLetter(Integer[][] doubleLetter) {
        this.doubleLetter = doubleLetter;
    }

    public Integer[][] getTripleLetter() {
        return tripleLetter;
    }

    public void setTripleLetter(Integer[][] tripleLetter) {
        this.tripleLetter = tripleLetter;
    }

    public Integer[][] getDoubleWord() {
        return doubleWord;
    }

    public void setDoubleWord(Integer[][] doubleWord) {
        this.doubleWord = doubleWord;
    }

    public Integer[][] getTripleWord() {
        return tripleWord;
    }

    public void setTripleWord(Integer[][] tripleWord) {
        this.tripleWord = tripleWord;
    }

    public String bonusToString(Integer[][] bonus) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(bonus);
        } catch (Exception ex) {
            return "";
        }
    }

    public GridType toGridType() {
        return new GridType(this);
    }

    @Override
    public String toString() {
        return "GridTypeDTO{" +
                "id=" + id +
                ", doubleLetter=" + bonusToString(doubleLetter) +
                ", tripleLetter=" + bonusToString(tripleLetter) +
                ", doubleWord=" + bonusToString(doubleWord) +
                ", tripleWord=" + bonusToString(tripleWord) +
                '}';
    }
}
