package com.marcblais.scrabbleapi.entities;

import com.marcblais.scrabbleapi.utilities.LettersCounter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DictionaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50)
    private String word;

    @ManyToMany
    @Builder.Default
    private List<Language> languages = new ArrayList<>();


    public Map<String, Integer> getLetters() {
        return LettersCounter.lettersCountMap(word.split(""));
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
