package com.bni.finproajubackend.service;

import com.bni.finproajubackend.dto.tickets.TicketHistoryResponseDTO;
import com.bni.finproajubackend.dto.tickets.TicketRequestDTO;
import com.bni.finproajubackend.interfaces.TicketInterface;
import com.bni.finproajubackend.model.enumobject.Status;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.ticket.TicketCategories;
import com.bni.finproajubackend.model.ticket.TicketHistory;
import com.bni.finproajubackend.model.ticket.TicketStatus;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import com.bni.finproajubackend.model.user.admin.Admin;
import com.bni.finproajubackend.repository.AdminRepository;
import com.bni.finproajubackend.repository.TicketStatusRepository;
import com.bni.finproajubackend.repository.TicketsHistoryRepository;
import com.bni.finproajubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService implements TicketInterface {

    @Autowired
    private TicketsHistoryRepository ticketsHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TicketStatus updatedStatus;

    @Override
    public Tickets createTicket(TicketRequestDTO requestDTO) {
        return null;
    }

    public void createTicketsHistory(long id, Authentication authentication) throws Exception {
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
    }

    private void createAndSaveTicketHistory(long level, Tickets tickets, Admin admin, String description) {
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.setTicket(tickets);
        ticketHistory.setLevel(level);
        ticketHistory.setAdmin(admin);
        ticketHistory.setDate(new Date());
        ticketHistory.setDescription(description);
        ticketsHistoryRepository.save(tickets);
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
    public Tickets getTicketDetails(Long ticketId) {
        return null;
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
    public List<TicketsHistoryRepository> getTicketsHistory(long id) {
        return List.of();
    }

    @Override
    public List<TicketHistoryResponseDTO> getTicketHistory(Long ticketId) {
        List<TicketHistory> ticketHistoryList = ticketsHistoryRepository.findByTicketId(ticketId);
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

    private String getAdminFullName(Admin admin) {
        return admin.getFirstName() + " " + admin.getLastName();
    }

    @Override
    public List<Tickets> getAllTickets() {
        return List.of();
    }
}
