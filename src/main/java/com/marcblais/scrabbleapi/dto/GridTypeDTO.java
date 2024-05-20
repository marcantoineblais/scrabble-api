package com.marcblais.scrabbleapi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcblais.scrabbleapi.entities.GridType;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridTypeDTO {
    long id;
    private Integer[][] doubleLetter;
    private Integer[][] tripleLetter;
    private Integer[][] doubleWord;
    private Integer[][] tripleWord;

    public GridTypeDTO(GridType gridType) {
        this.id = gridType.getId();
        this.doubleLetter = gridType.bonusToIntArray(gridType.getDoubleLetter());
        this.tripleLetter = gridType.bonusToIntArray(gridType.getTripleLetter());
        this.doubleWord = gridType.bonusToIntArray(gridType.getDoubleWord());
        this.tripleWord = gridType.bonusToIntArray(gridType.getTripleWord());
    }


    public String bonusToString(Integer[][] bonus) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(bonus);
        } catch (Exception ex) {
            return "";
        }
    }

    public GridType toGridType() {
        return new GridType(this);
    }

    @Override
    public String toString() {
        return "GridTypeDTO{" +
                "id=" + id +
                ", doubleLetter=" + bonusToString(doubleLetter) +
                ", tripleLetter=" + bonusToString(tripleLetter) +
                ", doubleWord=" + bonusToString(doubleWord) +
                ", tripleWord=" + bonusToString(tripleWord) +
                '}';
    }
}
