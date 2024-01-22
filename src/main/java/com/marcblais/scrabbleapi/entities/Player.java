package com.marcblais.scrabbleapi.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Player implements UserDetails {
    @Id
    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 50)
    private String password;

    @OneToMany
    @JoinColumn
    private List<Grid> grids;

    @OneToMany
    @JoinColumn(referencedColumnName = "username", name = "player_username")
    private List<Role> roles;

    private boolean enabled;
    private boolean active;

    public Player() {
    }

    public Player(String username, String password, List<Grid> grids, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.grids = grids;
        this.roles = roles;
        this.active = true;
        this.enabled = false;
    }

    public Player(String username, String password, List<Grid> grids, List<Role> roles, boolean enabled, boolean active) {
        this.username = username;
        this.password = password;
        this.grids = grids;
        this.roles = roles;
        this.enabled = enabled;
        this.active = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Grid> getGrids() {
        return grids;
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
