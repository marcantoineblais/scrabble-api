package com.marcblais.scrabbleapi.entities;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class LettersValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(unique = true)
    private Language language;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> points;

    public LettersValue() {
        points = new HashMap<>();
    }

    public LettersValue(long id, Language language, Map<String, Integer> points) {
        this.id = id;
        this.language = language;
        this.points = points;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Map<String, Integer> getPoints() {
        return points;
    }

    public void setPoints(Map<String, Integer> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Points{" +
                "id=" + id +
                ", language=" + language +
                ", points=" + points +
                '}';
    }
}
