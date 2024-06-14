package com.bni.finproajubackend.dto.userAccount;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserMutationDTO {
    private List<TransactionDTO> transaction_list;

    // Ubah konstruktor menjadi publik
    public UserMutationDTO(List<TransactionDTO> transaction_list) {
        this.transaction_list = transaction_list;
    }
}
