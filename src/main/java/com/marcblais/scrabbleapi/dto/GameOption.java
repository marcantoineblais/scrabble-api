package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Language;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameOption {
    private GridTypeDTO gridType;
    private Language language;
    private String name;

    @Override
    public String toString() {
        return "GameOption{" +
                "gridType=" + gridType +
                ", language=" + language +
                ", name='" + name + '\'' +
                '}';
    }
}
