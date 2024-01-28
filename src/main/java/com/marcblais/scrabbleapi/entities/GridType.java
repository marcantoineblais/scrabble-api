package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int[][] getDoubleLetter() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(doubleLetter, int[][].class);
        } catch (Exception ex) {
            return new int[][]{};
        }
    }

    public void setDoubleLetter(String doubleLetter) {
        this.doubleLetter = doubleLetter;
    }

    public void setDoubleLetter(int[][] doubleLetter) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            setDoubleLetter(mapper.writeValueAsString(doubleLetter));
        } catch (Exception ex) {
            return;
        }
    }

    public int[][] getTripleLetter() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(tripleLetter, int[][].class);
        } catch (Exception ex) {
            return new int[][]{};
        }
    }

    public void setTripleLetter(String tripleLetter) {
        this.tripleLetter = tripleLetter;
    }

    public void setTripleLetter(int[][] tripleLetter) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            setTripleLetter(mapper.writeValueAsString(doubleLetter));
        } catch (Exception ex) {
            return;
        }
    }

    public int[][] getDoubleWord() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(doubleWord, int[][].class);
        } catch (Exception ex) {
            return new int[][]{};
        }
    }

    public void setDoubleWord(String doubleWord) {
        this.doubleWord = doubleWord;
    }

    public void setDoubleWord(int[][] doubleWord) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            setDoubleWord(mapper.writeValueAsString(doubleLetter));
        } catch (Exception ex) {
            return;
        }
    }

    public int[][] getTripleWord() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(tripleWord, int[][].class);
        } catch (Exception ex) {
            return new int[][]{};
        }
    }

    public void setTripleWord(String tripleWord) {
        this.tripleWord = tripleWord;
    }

    public void setTripleWord(int[][] tripleWord) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            setTripleWord(mapper.writeValueAsString(doubleLetter));
        } catch (Exception ex) {
            return;
        }
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
