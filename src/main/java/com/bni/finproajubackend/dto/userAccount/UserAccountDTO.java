package com.bni.finproajubackend.dto.userAccount;

import com.bni.finproajubackend.model.enumobject.Gender;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserAccountDTO {
    private Long id;
    private String name;
    private List<AccountDTO> account_list;
}
