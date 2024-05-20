package com.marcblais.scrabbleapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


    @Override
    public String toString() {
        return "Points{" +
                "id=" + id +
                ", language=" + language +
                ", points=" + points +
                '}';
    }
}
