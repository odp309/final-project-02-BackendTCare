package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.userAccount.TransactionDTO;
import com.bni.finproajubackend.dto.userAccount.UserMutationDTO;
import com.bni.finproajubackend.interfaces.UserMutationInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.enumobject.TransactionCategories;
import com.bni.finproajubackend.model.enumobject.TransactionType;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                            .filter(tickets -> !tickets.isEmpty())
                            .map(tickets -> {
                                TicketStatus status = tickets.get(0).getTicketStatus();
                                if (status == TicketStatus.Selesai) return "Selesai";
                                if (status == TicketStatus.DalamProses) return "Dalam Proses";
                                if (status == TicketStatus.Diajukan) return "Diajukan";
                                return null;
                            })
                            .orElse(null);
                    String transactionDetail = transaction.getTransaction_type() + " " + transaction.getRecipient_account().getNasabah().getFirst_name();

                    return new TransactionDTO(
                            transaction.getId(),
                            transactionDetail,
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
    public UserMutationDTO getUserListTransaction(Authentication authentication, String account_number, LocalDate startDate, LocalDate endDate, TicketStatus ticketStatus) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Account account = accountRepository.findByAccountNumber(account_number);
        checkAccountOwnership(user, account);

        List<Transaction> transactions = transactionRepository.findByAccount(account);

        List<Transaction> filteredTransactions = transactions;

        // Jika startDate dan endDate tidak null, lakukan filter
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            filteredTransactions = transactions.stream()
                    .filter(transaction -> !transaction.getCreatedAt().isBefore(startDateTime) && !transaction.getCreatedAt().isAfter(endDateTime))
                    .collect(Collectors.toList());
        }

        if (ticketStatus != null) {
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getTickets() != null && !transaction.getTickets().isEmpty() && transaction.getTickets().get(0).getTicketStatus() == ticketStatus)
                    .collect(Collectors.toList());
        }

        List<TransactionDTO> transactionDTOList = filteredTransactions.stream()
                .filter(transaction -> transaction.getTransaction_type() == TransactionType.Out)
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .map(transaction -> {
                    String transactionType = "Out";
                    String formattedDateTime = transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    String ticketStatusStr = Optional.ofNullable(transaction.getTickets())
                            .filter(tickets -> !tickets.isEmpty())
                            .map(tickets -> {
                                TicketStatus status = tickets.get(0).getTicketStatus();
                                if (status == TicketStatus.Selesai) return "Selesai";
                                if (status == TicketStatus.DalamProses) return "Dalam Proses";
                                if (status == TicketStatus.Diajukan) return "Diajukan";
                                return null;
                            })
                            .orElse(null);

                    String transactionDetail = transaction.getCategory() == TransactionCategories.Transfer ? transaction.getCategory() + " ke " + transaction.getRecipient_account().getNasabah().getFirst_name()
                            : transaction.getCategory() == TransactionCategories.TopUp ? transaction.getCategory() + " OVO"
                            : transaction.getCategory() + " PLN Pascabayar";

                    return new TransactionDTO(
                            transaction.getId(),
                            transactionDetail,
                            transactionType,
                            transaction.getAmount(),
                            formattedDateTime,
                            ticketStatusStr
                    );
                })
                .collect(Collectors.toList());

        return new UserMutationDTO(transactionDTOList);
    }

    @Override
    public UserMutationDTO getUserTransactionsByAccountNo(Authentication authentication, String account_number, LocalDate startDate, LocalDate endDate) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Account account = accountRepository.findByAccountNumber(account_number);
        checkAccountOwnership(user, account);

        List<Transaction> transactions = transactionRepository.findByAccount(account);
        List<Transaction> filteredTransactions = transactions;

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            filteredTransactions = transactions.stream()
                    .filter(transaction -> !transaction.getCreatedAt().isBefore(startDateTime) && !transaction.getCreatedAt().isAfter(endDateTime))
                    .collect(Collectors.toList());
        }

        List<TransactionDTO> transactionDTOList = filteredTransactions.stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .map(transaction -> {
                    String transactionType = transaction.getTransaction_type() == TransactionType.In ? "In" : "Out";
                    String formattedDateTime = transaction.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    // Ensure tickets is not null and handle its status
                    String ticketStatus = Optional.ofNullable(transaction.getTickets())
                            .filter(tickets -> !tickets.isEmpty())
                            .map(tickets -> {
                                TicketStatus status = tickets.get(0).getTicketStatus();
                                if (status == TicketStatus.Selesai) return "Selesai";
                                if (status == TicketStatus.DalamProses) return "Dalam Proses";
                                if (status == TicketStatus.Diajukan) return "Diajukan";
                                return null;
                            })
                            .orElse(null);

                    String transactionDetail = transaction.getCategory() == TransactionCategories.Transfer ? transaction.getCategory() + " ke " + transaction.getRecipient_account().getNasabah().getFirst_name()
                            : transaction.getCategory() == TransactionCategories.TopUp ? transaction.getCategory() + " OVO"
                            : transaction.getCategory() + " PLN Pascabayar";

                    return new TransactionDTO(
                            transaction.getId(),
                            transactionDetail,
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
