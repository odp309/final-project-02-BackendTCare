package com.bni.finproajubackend.service;

import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketResponseTime;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Contract;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TicketService implements TicketInterface {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TicketsRepository ticketsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TicketsHistoryRepository ticketHistoryRepository;
    @Autowired
    private TicketResponseTimeRepository ticketResponseTimeRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public Tickets updateTicketStatus(Long ticketId, TicketStatus status, Authentication authentication) {
        Tickets ticket = ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        TicketStatus oldStatus = ticket.getTicketStatus();
        ticket.setTicketStatus(status);
        ticketsRepository.save(ticket);

        // Get admin details from authentication
        String username = authentication.getName();
        Admin admin = adminRepository.findByUsername(username);

        if (admin == null)
            throw new RuntimeException("User not found");

        Long statusLevel = getStatusLevel(ticket.getTicketStatus());

        // Create a new ticket history entry
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(ticket);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDescription("Ticket status updated from " + oldStatus + " to " + status);
        ticketHistory.setDate(new Date());
        ticketHistory.setLevel(statusLevel);
        ticketHistory.setCreatedAt(LocalDateTime.now());
        ticketHistory.setUpdatedAt(LocalDateTime.now());
        ticketHistoryRepository.save(ticketHistory);

        // Handle TicketResponseTime when status is Ditutup
        if (status == TicketStatus.Ditutup) {
            handleTicketResponseTime(ticket);
        }

        // Send email notification
        emailService.sendNotification(ticket);

        return ticket;
    }

    private Long getStatusLevel(TicketStatus status) {
        return status == TicketStatus.Dibuat ? 1L :
                status == TicketStatus.Diajukan ? 2L :
                        status == TicketStatus.DalamProses ? 3L :
                                status == TicketStatus.Selesai ? 4L : 5L;
    }

    private void handleTicketResponseTime(Tickets ticket) {
        TicketResponseTime responseTime = new TicketResponseTime();
        responseTime.setTicket(ticket);

        LocalDate createdAt = ticket.getCreatedAt().toLocalDate();
        LocalDate closedAt = LocalDate.now();
        long daysBetween = java.time.Duration.between(createdAt.atStartOfDay(), closedAt.atStartOfDay()).toDays();

        responseTime.setResponseTime(daysBetween);
        responseTime.setCreatedAt(LocalDateTime.now());
        responseTime.setUpdatedAt(LocalDateTime.now());

        ticketResponseTimeRepository.save(responseTime);
    }

    @Override
    public TicketResponseDTO getTicketDetails(Long ticketId) {
        return null;
    }

    public String createTicketNumber(Transaction transaction) {
        String categoryCode = "";
        switch ("Transfer") {
            case "Transfer":
                categoryCode = "TF"; // ID kategori 1 untuk Gagal Transfer
                break;
            case "TopUp":
                categoryCode = "TU"; // ID kategori 2 untuk Gagal Top Up
                break;
            case "Payment":
                categoryCode = "PY"; // ID kategori 3 untuk Gagal Pembayaran
                break;
            default:
                categoryCode = ""; // ID kategori tidak valid
        }

        Date createdAt = new Date();
        String year = String.valueOf(createdAt.getYear());
        String month = String.format("%02d", createdAt.getMonth());
        String day = String.format("%02d", createdAt.getDay());

        String transactionId = String.valueOf(transaction);

        String ticketNumber = categoryCode + year + month + day + transactionId;

        if (ticketNumber.length() > 15) {
            ticketNumber = ticketNumber.substring(0, 15);
        }

        return ticketNumber;
    }

    @Override
    public List<TicketHistoryResponseDTO> getTicketHistory(long id) {
        List<TicketHistory> ticketHistoryList = ticketHistoryRepository.findAll();
        List<TicketHistoryResponseDTO> responseDTOList = new ArrayList<>();

        for (TicketHistory ticketHistory : ticketHistoryList) {
            TicketHistoryResponseDTO responseDTO = new TicketHistoryResponseDTO();
            responseDTO.setPic(getAdminFullName(ticketHistory.getAdmin()));
            responseDTO.setDescription(ticketHistory.getDescription());
            responseDTO.setDate(ticketHistory.getDate());

            responseDTOList.add(responseDTO);
        }

        return responseDTOList;
    }

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        List<Tickets> ticketsList = ticketsRepository.findAll();
        return null;
    }

    @Override
    public TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO) {
        Optional<Transaction> transaction = transactionRepository.findById(ticketRequestDTO.getTransactionId());
        Tickets ticket = new Tickets();
        ticket.setTicketNumber(createTicketNumber(transaction.get()));
        ticket.setTransaction(transaction.get());
        ticket.setTicketCategory(ticketRequestDTO.getTicketCategory());
        ticket.setDescription(ticketRequestDTO.getDescription());
        ticketsRepository.save(ticket);

        return TicketResponseDTO.builder()
                .ticketNumber(ticket.getTicketNumber())
                .transaction(ticket.getTransaction())
                .ticketCategory(ticket.getTicketCategory())
                .description(ticket.getDescription())
                .createdAt(ticket.getCreatedAt())
                .build();
    }

    public String getAdminFullName(@NotNull Admin admin) {
        return admin.getFirstName() + " " + admin.getLastName();
    }
}
