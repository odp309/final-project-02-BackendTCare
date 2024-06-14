package com.bni.finproajubackend.dto.userAccount;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ListAccountNumberDTO {
    private List<AccountNumberDTO> list_account;
}
