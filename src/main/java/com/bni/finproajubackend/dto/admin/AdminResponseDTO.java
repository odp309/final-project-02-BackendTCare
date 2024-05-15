package com.bni.finproajubackend.dto.admin;

import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.user.admin.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponseDTO {
    private Person person;
    private Role role;
    private String npp;
}
