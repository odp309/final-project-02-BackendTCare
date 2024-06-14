package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TrackTicketStatusResponseDTO;
import com.bni.finproajubackend.interfaces.TrackTicketStatusInterface;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Account;
import com.bni.finproajubackend.model.user.nasabah.Nasabah;
import com.bni.finproajubackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketHistoryStatusService implements TrackTicketStatusInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NasabahRepository nasabahRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TicketsRepository ticketsRepository;
    @Autowired
    private TicketsHistoryRepository ticketsHistoryRepository;

    private void checkAccountOwnership(User user, Account account) {
        Nasabah nasabah = nasabahRepository.findByUser(user);
        if (!account.getNasabah().equals(nasabah)) {
            throw new RuntimeException("You're not the account owner!");
        }
    }

    @Override
    public List<TrackTicketStatusResponseDTO> trackTicketStatus(Long id) {
        Tickets ticket = ticketsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        List<TicketHistory> ticketHistories = ticketsHistoryRepository.findAllByTicket(ticket);

        return ticketHistories.stream().map(ticketHistory -> {
            TrackTicketStatusResponseDTO response = new TrackTicketStatusResponseDTO();
            response.setPic(ticketHistory.getAdmin().getFirstName() + " " + ticketHistory.getAdmin().getLastName());
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketHistory.getDate());
            response.setDate(formattedDate);
            response.setDescription(ticketHistory.getDescription());
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TrackTicketStatusResponseDTO> trackMyTicketStatus(Authentication authentication, Long id, String account_number) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        //Nasabah nasabah = nasabahRepository.findByUser(user);
        Account account = accountRepository.findByAccountNumber(account_number);
        checkAccountOwnership(user, account);

        // Find the ticket by ID
        Tickets ticket = ticketsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        // Check if the ticket belongs to the user's account via transactions
        if (!ticket.getTransaction().getAccount().equals(account)) {
            throw new RuntimeException("Ticket does not belong to the user");
        }

        List<TicketHistory> ticketHistories = ticketsHistoryRepository.findAllByTicket(ticket);

        return ticketHistories.stream().map(ticketHistory -> {
            TrackTicketStatusResponseDTO response = new TrackTicketStatusResponseDTO();
            response.setPic(ticketHistory.getAdmin().getFirstName() + " " + ticketHistory.getAdmin().getLastName());
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketHistory.getDate());
            response.setDate(formattedDate);
            response.setDescription(ticketHistory.getDescription());
            return response;
        }).collect(Collectors.toList());
    }

}
