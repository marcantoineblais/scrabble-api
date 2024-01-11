package com.marcblais.scrabbleapi.entities;

import jakarta.persistence.*;

@Entity
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    public Language() {
    }

    public Language(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
