package com.marcblais.scrabbleapi.dto;

public class SignInRequest {
    private String username;
    private String password;
    private String email;
    private String info;

    public SignInRequest() {
    }

    public SignInRequest(String username, String password, String email, String info) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.info = info;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
