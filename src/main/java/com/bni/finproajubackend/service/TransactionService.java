package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.receipt.TransactionResponseDTO;
import com.bni.finproajubackend.interfaces.ReceiptInterface;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements ReceiptInterface {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private static final Marker TRANSACTION_MARKER = MarkerFactory.getMarker("TRANSACTION");
    @Autowired
    private LoggerService loggerService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public TransactionResponseDTO getTransactionDetails(Long id, Authentication authentication) throws Exception {
        logger.debug(TRANSACTION_MARKER, "IP {}, Transaction Details requested received for username {}", loggerService.getClientIp(), authentication.getName());
        try {
            Transaction tx = transactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transaction Not Found"));
            logger.info(TRANSACTION_MARKER, "IP {}, Transaction Details acquired", loggerService.getClientIp());
            return TransactionResponseDTO.builder()
                    .amount(tx.getAmount())
                    .transaction_number(tx.getId().toString())
                    .transaction_date(tx.getCreatedAt().toString())
                    .transaction_type(tx.getTransaction_type().toString())
                    .transaction_category(tx.getCategory().toString())
                    .sender_account_number(tx.getAccount().getAccountNumber().toString())
                    .sender_name(tx.getAccount().getNasabah().getFirst_name() + " " + tx.getAccount().getNasabah().getLast_name())
                    .sender_company(tx.getBank().getBankName())
                    .recipient_account_number(tx.getRecipient_account().getAccountNumber())
                    .recipient_name(tx.getRecipient_account().getNasabah().getFirst_name() + " " + tx.getRecipient_account().getNasabah().getLast_name())
                    .recipient_company(tx.getRecipient_bank().getBankName())
                    .build();
        } catch (Exception e) {
            logger.error(TRANSACTION_MARKER, "IP {}, Error getting transaction details", loggerService.getClientIp(), e);
            throw new Exception("Error getting transaction details");
        }
    }
}
