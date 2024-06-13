package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.userAccount.TransactionDTO;
import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.interfaces.UserMutationInterface;
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
import java.util.Optional;
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
    @Autowired
    private TicketsRepository ticketsRepository;

    private void checkAccountOwnership(User user, Account account) {
        Nasabah nasabah = nasabahRepository.findByUser(user);
        if (!account.getNasabah().equals(nasabah)) {
            throw new RuntimeException("You're not the account owner");
        }
    }

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
                .map(transaction -> {
                    String transactionType = transaction.getTransaction_type() == TransactionType.In ? "In" : "Out";
                    String formattedDateTime = transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    // Ensure tickets is not null and handle its status
                    String ticketStatus = Optional.ofNullable(transaction.getTickets())
                            .map(ticket -> {
                                TicketStatus status = ticket.getTicketStatus();
                                if (status == TicketStatus.Selesai) return "Selesai";
                                if (status == TicketStatus.DalamProses) return "Dalam Proses";
                                if (status == TicketStatus.Diajukan) return "Diajukan";
                                return null;
                            })
                            .orElse("No Ticket");

                    return new TransactionDTO(
                            transaction.getId(),
                            transaction.getDetail(),
                            transactionType,
                            transaction.getAmount(),
                            formattedDateTime,
                            ticketStatus
                    );
                })
                .collect(Collectors.toList());

        return new UserMutationDTO(transactionDTOList);
    }

    @Override
    public UserMutationDTO getUserListTransaction(Authentication authentication, String account_number) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Account account = accountRepository.findByAccountNumber(account_number);
        checkAccountOwnership(user, account);

        List<Transaction> transactions = transactionRepository.findByAccount(account);

        List<TransactionDTO> transactionDTOList = transactions.stream()
                .filter(transaction -> transaction.getTransaction_type() == TransactionType.Out) // Filter hanya transaksi Out
                .map(transaction -> {
                    String transactionType = "Out"; // Di sini sudah pasti TransactionType adalah Out
                    String formattedDateTime = transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    // Ensure tickets is not null and handle its status
                    String ticketStatus = Optional.ofNullable(transaction.getTickets())
                            .map(ticket -> {
                                TicketStatus status = ticket.getTicketStatus();
                                if (status == TicketStatus.Selesai) return "Selesai";
                                if (status == TicketStatus.DalamProses) return "Dalam Proses";
                                if (status == TicketStatus.Diajukan) return "Diajukan";
                                return null;
                            })
                            .orElse("No Ticket");

                    return new TransactionDTO(
                            transaction.getId(),
                            transaction.getDetail(),
                            transactionType,
                            transaction.getAmount(),
                            formattedDateTime,
                            ticketStatus
                    );
                })
                .collect(Collectors.toList());

        return new UserMutationDTO(transactionDTOList);
    }

    @Override
    public UserMutationDTO getUserTransactionsByAccountNo(Authentication authentication, String account_number) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Account account = accountRepository.findByAccountNumber(account_number);
        checkAccountOwnership(user, account);

        List<Transaction> transactions = transactionRepository.findByAccount(account);

        List<TransactionDTO> transactionDTOList = transactions.stream()
                .map(transaction -> {
                    String transactionType = transaction.getTransaction_type() == TransactionType.In ? "In" : "Out";
                    String formattedDateTime = transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    // Ensure tickets is not null and handle its status
                    String ticketStatus = Optional.ofNullable(transaction.getTickets())
                            .map(ticket -> {
                                TicketStatus status = ticket.getTicketStatus();
                                if (status == TicketStatus.Selesai) return "Selesai";
                                if (status == TicketStatus.DalamProses) return "Dalam Proses";
                                if (status == TicketStatus.Diajukan) return "Diajukan";
                                return null;
                            })
                            .orElse(null);

                    return new TransactionDTO(
                            transaction.getId(),
                            transaction.getDetail(),
                            transactionType,
                            transaction.getAmount(),
                            formattedDateTime,
                            ticketStatus
                    );
                })
                .collect(Collectors.toList());

        return new UserMutationDTO(transactionDTOList);
    }
}
