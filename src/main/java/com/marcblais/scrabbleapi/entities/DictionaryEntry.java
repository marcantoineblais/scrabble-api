package com.marcblais.scrabbleapi.entities;

import com.marcblais.scrabbleapi.dto.LettersCounter;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class DictionaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50)
    private String word;

    @ManyToOne
    private Language language;

    public DictionaryEntry() {
    }

    public DictionaryEntry(String word) {
        this.word = word;
    }

    public DictionaryEntry(long id, String word) {
        this.id = id;
        this.word = word;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Map<String, Integer> getLetters() {
        return LettersCounter.lettersCountMap(word);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DictionaryEntry that)) return false;
        return Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", language=" + language +
                '}';
    }
}
