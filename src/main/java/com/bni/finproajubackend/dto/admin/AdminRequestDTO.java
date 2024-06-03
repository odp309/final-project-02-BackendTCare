package com.bni.finproajubackend.dto.admin;

import com.bni.finproajubackend.model.enumobject.Gender;
import com.bni.finproajubackend.model.user.admin.Role;
import lombok.Data;

@Data
public class AdminRequestDTO {
    private String email;
    private String noHP;
    private String firstName;
    private String lastName;
    private Gender gender;
    private int age;
    private String address;
    private String npp;
}
