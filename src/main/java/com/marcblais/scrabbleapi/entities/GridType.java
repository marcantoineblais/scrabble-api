package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.dto.GameOptions;
import com.marcblais.scrabbleapi.dto.GridTypeDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GridType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String doubleLetter;
    private String tripleLetter;
    private String doubleWord;
    private String tripleWord;


    public GridType(GridTypeDTO gridType) {
        this.id = gridType.getId();
        this.doubleLetter = gridType.bonusToString(gridType.getDoubleLetter());
        this.tripleLetter = gridType.bonusToString(gridType.getTripleLetter());
        this.doubleWord = gridType.bonusToString(gridType.getDoubleWord());
        this.tripleWord = gridType.bonusToString(gridType.getTripleWord());
    }


    public Integer[][] bonusToIntArray(String bonus) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(bonus, Integer[][].class);
        } catch (Exception ex) {
            return null;
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
