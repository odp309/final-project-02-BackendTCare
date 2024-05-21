package com.bni.finprovinbackend.dto.user;

import com.bni.finprovinbackend.model.enumobject.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRequestDTO {
    private String email, username, firstName, lastName, password, address, noHP;
    private int age;
    private Gender gender;
}
