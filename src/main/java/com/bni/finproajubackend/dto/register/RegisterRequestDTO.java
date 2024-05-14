package com.bni.finproajubackend.dto.register;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String password;
}
