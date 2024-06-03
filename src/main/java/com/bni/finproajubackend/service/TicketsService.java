package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketsHistoryResponseDTO;
import com.bni.finproajubackend.interfaces.TicketsInterface;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.TicketsRepository;
import com.bni.finproajubackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TicketsService implements TicketsInterface {
    @Autowired
    private TicketsHistoryRepository ticketsHistoryRepository;

    @Autowired
    private TicketsRepository ticketsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createTicketsHistory(long id, Authentication authentication) throws Exception {
        Tickets tickets = ticketsRepository.findTicketById(id);
        User user = userRepository.findByUsername(authentication.getName());

        List<TicketHistory> ticketHistories = tickets.getTicketHistory();
        long highestLevel = ticketHistories.stream()
                .mapToLong(TicketHistory::getLevel)
                .max()
                .orElse(0);

        long newLevel = highestLevel + 1;

        // Create primary ticket history
        String primaryDescription = getDescriptionForLevel(newLevel);
        createAndSaveTicketHistory(newLevel, tickets, user.getAdmin(), primaryDescription);

        // Conditionally create secondary ticket history
        if (highestLevel == 0 || highestLevel == 3) {
            long secondLevel = newLevel + 1;
            String secondaryDescription = getDescriptionForLevel(secondLevel);
            createAndSaveTicketHistory(secondLevel, tickets, user.getAdmin(), secondaryDescription);
        }
    }

    private void createAndSaveTicketHistory(long level, Tickets tickets, Admin admin, String description) {
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(tickets);
        ticketHistory.setLevel(level);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDate(new Date());
        ticketHistory.setDescription(description);
        ticketsHistoryRepository.save(ticketHistory);
    }

    private String getDescriptionForLevel(long level) {
        switch ((int) level) {
            case 1:
                return "Transaksi Dilakukan";
            case 2:
                return "Laporan Diajukan";
            case 3:
                return "Laporan Dalam Proses";
            case 4:
                return "Laporan Diterima Pelapor";
            default:
                return "Laporan Selesai Diproses";
        }
    }

    @Override
    public List<TicketsHistoryResponseDTO> getTicketsHistory(long id) {
        Tickets tickets = ticketsRepository.findTicketById(id);
        if (tickets == null)
            throw new EntityNotFoundException("Tickets with ID " + id + " not found.");

        List<TicketHistory> ticketHistories = tickets.getTicketHistory();

        return ticketHistories.stream()
                .map(ticketHistory -> TicketsHistoryResponseDTO.builder()
                        .pic(ticketHistory.getAdmin().getFirstName() + " " + ticketHistory.getAdmin().getLastName())
                        .date(ticketHistory.getDate())
                        .description(ticketHistory.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
