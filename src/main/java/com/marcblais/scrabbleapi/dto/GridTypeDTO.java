package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.entities.GridType;

public class GridTypeDTO {
    long id;
    private int[][] doubleLetter;
    private int[][] tripleLetter;
    private int[][] doubleWord;
    private int[][] tripleWord;

    public GridTypeDTO() {
    }

    public GridTypeDTO(long id, int[][] doubleLetter, int[][] tripleLetter, int[][] doubleWord, int[][] tripleWord) {
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

    public int[][] getDoubleLetter() {
        return doubleLetter;
    }

    public void setDoubleLetter(int[][] doubleLetter) {
        this.doubleLetter = doubleLetter;
    }

    public int[][] getTripleLetter() {
        return tripleLetter;
    }

    public void setTripleLetter(int[][] tripleLetter) {
        this.tripleLetter = tripleLetter;
    }

    public int[][] getDoubleWord() {
        return doubleWord;
    }

    public void setDoubleWord(int[][] doubleWord) {
        this.doubleWord = doubleWord;
    }

    public int[][] getTripleWord() {
        return tripleWord;
    }

    public void setTripleWord(int[][] tripleWord) {
        this.tripleWord = tripleWord;
    }

    public String bonusToString(int[][] bonus) {
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
