package com.marcblais.scrabbleapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player {
    @Id
    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 76, nullable = false)
    @JsonIgnore
    private String password;

    @Column(length = 50, unique = true, nullable = false)
    @JsonIgnore
    private String email;

    @OneToMany
    @JoinColumn(referencedColumnName = "username", name = "player_username")
    @Builder.Default
    private List<Grid> grids = new ArrayList<>();

    @OneToMany
    @JoinColumn(referencedColumnName = "username", name = "player_username")
    @JsonIgnore
    private List<Role> roles;

    private boolean enabled;

    public Grid findGrid(String uuid) {
        return grids.stream().filter(g -> g.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", grids=" + grids +
                ", roles=" + roles +
                ", enabled=" + enabled +
                '}';
    }
}
