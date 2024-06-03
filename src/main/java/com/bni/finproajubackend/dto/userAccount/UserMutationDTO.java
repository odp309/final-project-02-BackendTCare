package com.bni.finproajubackend.dto.userAccount;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class UserMutationDTO {
    private List<TransactionDTO> transactionList;
}
