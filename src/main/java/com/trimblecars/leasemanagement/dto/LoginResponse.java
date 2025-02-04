package com.trimblecars.leasemanagement.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String email;
    private String username;
    private String message;


    public LoginResponse(String token, String email, String username, String message) {
        this.token = token;
        this.email = email;
        this.username = username;
        this.message = message;
    }

}
