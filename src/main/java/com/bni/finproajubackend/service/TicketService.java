package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.dto.tickets.TicketResponseDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketInterface {

    @Autowired
    private TicketsHistoryRepository ticketsHistoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TicketsRepository ticketsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;

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
    public Tickets createTicketsHistory(long id, Authentication authentication) throws Exception {
        Tickets tickets = ticketsHistoryRepository.findTicketById(id);
        User user = userRepository.findByUsername(authentication.getName());

        List<TicketHistory> ticketHistories = tickets.getTicketHistory();
        long highestLevel = ticketHistories.stream()
                .mapToLong(TicketHistory::getLevel)
                .max()
                .orElse(0);

        long newLevel = highestLevel + 1;

        String primaryDescription = getDescriptionForLevel(newLevel);
        createAndSaveTicketHistory(newLevel, tickets, user.getAdmin(), primaryDescription);

        if (highestLevel == 0 || highestLevel == 3) {
            long secondLevel = newLevel + 1;
            String secondaryDescription = getDescriptionForLevel(secondLevel);
            createAndSaveTicketHistory(secondLevel, tickets, user.getAdmin(), secondaryDescription);
        }
        return tickets;
    }

    @Override
    public void createAndSaveTicketHistory(long level, Tickets tickets, Admin admin, String description) {
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(tickets);
        ticketHistory.setLevel(level);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDate(new Date());
        ticketHistory.setDescription(description);
        ticketsHistoryRepository.save(tickets);
    }

    @Contract(pure = true)
    private @NotNull String getDescriptionForLevel(long level) {
        switch ((int) level) {
            case 1:
                return "Transaksi Dilakukan";
            case 2:
                return "Laporan Diajukan";
            case 3:
                return "Laporan Dalam Proses";
            case 4:
                return "Laporan Selesai";
            default:
                return "Laporan Selesai Diproses";
        }
    }

    @Override
    public Tickets updateTicketStatus(Long id, TicketStatus status) {
        Tickets ticket = ticketsHistoryRepository.findTicketById(id);

        if (ticket == null) {
            return new Tickets();
        }

        ticket.setTicketStatus(status);

        return ticketsHistoryRepository.save(ticket);
    }

    @Override
    public List<TicketHistoryResponseDTO> getTicketHistory(long id) {
        List<TicketHistory> ticketHistoryList = ticketsHistoryRepository.findTicketById(id).getTicketHistory();
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
        return getAllTickets();
    }

    @Override
    public TicketResponseDTO createNewTicket(TicketRequestDTO ticketRequestDTO) {
        Transaction transaction = transactionRepository.findById(ticketRequestDTO.getTransactionId());
        Tickets ticket = new Tickets();
        ticket.setTicketNumber(createTicketNumber(transaction));
        ticket.setTransaction(transaction);
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
