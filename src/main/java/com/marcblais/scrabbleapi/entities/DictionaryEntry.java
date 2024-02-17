package com.marcblais.scrabbleapi.entities;

import com.marcblais.scrabbleapi.utilities.LettersCounter;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class DictionaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50)
    private String word;

    @ManyToMany
    private List<Language> languages;

    public DictionaryEntry() {
        this.languages = new ArrayList<>();
    }

    public DictionaryEntry(String word) {
        this.word = word;
    }

    public DictionaryEntry(long id, String word) {
        this.id = id;
        this.word = word;
    }

    public DictionaryEntry(long id, String word, List<Language> languages) {
        this.id = id;
        this.word = word;
        this.languages = languages;
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

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguage(List<Language> languages) {
        this.languages = languages;
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
        return "DictionaryEntry{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", language=" + languages +
                '}';
    }
}
