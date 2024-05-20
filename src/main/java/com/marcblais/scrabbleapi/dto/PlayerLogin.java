package com.marcblais.scrabbleapi.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLogin {
    private String username;
    private String password;
    private boolean rememberMe;
}
