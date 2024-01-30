package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GameOptions;
import com.marcblais.scrabbleapi.dto.GridTypeDTO;
import jakarta.persistence.*;

import java.util.Arrays;

@Entity
public class GridType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String doubleLetter;
    private String tripleLetter;
    private String doubleWord;
    private String tripleWord;

    public GridType() {
    }

    public GridType(long id, String doubleLetter, String tripleLetter, String doubleWord, String tripleWord) {
        this.id = id;
        this.doubleLetter = doubleLetter;
        this.tripleLetter = tripleLetter;
        this.doubleWord = doubleWord;
        this.tripleWord = tripleWord;
    }

    public GridType(GridTypeDTO gridType) {
        this.id = gridType.getId();
        this.doubleLetter = gridType.bonusToString(gridType.getDoubleLetter());
        this.tripleLetter = gridType.bonusToString(gridType.getTripleLetter());
        this.doubleWord = gridType.bonusToString(gridType.getDoubleWord());
        this.tripleWord = gridType.bonusToString(gridType.getTripleWord());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDoubleLetter() {
        return doubleLetter;
    }

    public void setDoubleLetter(String doubleLetter) {
        this.doubleLetter = doubleLetter;
    }

    public String getTripleLetter() {
        return tripleLetter;
    }

    public void setTripleLetter(String tripleLetter) {
        this.tripleLetter = tripleLetter;
    }

    public String getDoubleWord() {
        return doubleWord;
    }

    public void setDoubleWord(String doubleWord) {
        this.doubleWord = doubleWord;
    }

    public String getTripleWord() {
        return tripleWord;
    }

    public void setTripleWord(String tripleWord) {
        this.tripleWord = tripleWord;
    }

    public int[][] bonusToIntArray(String bonus) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(bonus, int[][].class);
        } catch (Exception ex) {
            return new int[][]{};
        }
    }

    public GridTypeDTO toGridTypeDTO() {
        return new GridTypeDTO(this);
    }

    @Override
    public String toString() {
        return "GridType{" +
                "id=" + id +
                ", doubleLetter=" + doubleLetter +
                ", tripleLetter=" + tripleLetter +
                ", doubleWord=" + doubleWord +
                ", tripleWord=" + tripleWord +
                '}';
    }
}
