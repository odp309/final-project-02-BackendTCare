package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.receipt.TransactionResponseDTO;
import org.springframework.security.core.Authentication;

public interface ReceiptInterface {
    TransactionResponseDTO getTransactionDetails(Long id, Authentication authentication) throws Exception;
}
