package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserMutationInterface {
    UserMutationDTO getUserMutations(Authentication authentication);
    UserMutationDTO getUserListTransaction(Authentication authentication, String account_number, LocalDate startDate, LocalDate endDate, TicketStatus ticket_status);
    UserMutationDTO getUserTransactionsByAccountNo(Authentication authentication, String accountNo, LocalDate startDate, LocalDate endDate);
}