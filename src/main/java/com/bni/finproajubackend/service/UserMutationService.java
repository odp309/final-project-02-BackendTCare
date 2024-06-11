package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.userAccount.TransactionDTO;
import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.interfaces.UserMutationInterface;
//import com.bni.finproajubackend.model.user.Person;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.enumobject.TransactionType;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMutationService implements UserMutationInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private NasabahRepository nasabahRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserMutationDTO getUserMutations(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }


        Nasabah nasabah = nasabahRepository.findByUser(user);

        Account account = accountRepository.findByNasabah(nasabah);

        List<Transaction> transactions = transactionRepository.findByAccount(account);
        List<TransactionDTO> transactionDTOList = transactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());

        return UserMutationDTO.builder()
                .transaction_list(transactionDTOList)
                .build();
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction){
        String transactionType = transaction.getTransaction_type() == TransactionType.In ? "In" : "Out";
        String formattedDateTime = transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String ticketStatus = transaction.getTickets().getTicketStatus() == TicketStatus.Selesai ? "Selesai"
                : transaction.getTickets().getTicketStatus() == TicketStatus.DalamProses ? "Dalam Proses"
                : "Diajukan";

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setTransaction_description(transaction.getDetail());
        transactionDTO.setTransaction_type(transactionType);
        transactionDTO.setDate(formattedDateTime);
        transactionDTO.setTicket_status(ticketStatus);
        return transactionDTO;
    }
}
