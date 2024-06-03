package com.bni.finproajubackend.dto.userAccount;

import com.bni.finproajubackend.model.enumobject.Gender;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAccountDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Integer age;
    private String noHp;
    private String address;
    private String nasabahCode;
    private List<AccountDTO> accountList;
}
