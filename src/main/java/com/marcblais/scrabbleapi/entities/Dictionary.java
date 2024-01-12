package com.marcblais.scrabbleapi.entities;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String word;

    @ManyToOne
    private Language language;

    public Dictionary() {
    }

    public Dictionary(String word) {
        this.word = word;
    }

    public Dictionary(long id, String word) {
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
        Map<String, Integer> lettersMap = new HashMap<>();
        String[] letters = word.split("");

        for (String letter : letters) {
            if (lettersMap.containsKey(letter)) {
                int count = lettersMap.get(letter);
                lettersMap.put(letter, count + 1);
            } else {
                lettersMap.put(letter, 1);
            }
        }

        return lettersMap;
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
