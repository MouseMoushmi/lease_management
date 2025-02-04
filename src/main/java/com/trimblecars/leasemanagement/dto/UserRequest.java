package com.trimblecars.leasemanagement.dto;

import com.trimblecars.leasemanagement.model.auth.UserRole;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}