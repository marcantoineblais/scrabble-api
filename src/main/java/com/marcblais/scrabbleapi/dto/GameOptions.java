package com.marcblais.scrabbleapi.dto;

import com.marcblais.scrabbleapi.entities.Language;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameOptions {
    private List<Language> languages;
    private List<GridTypeDTO> gridTypes;
}
