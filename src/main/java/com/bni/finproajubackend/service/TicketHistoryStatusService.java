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
import java.util.ArrayList;
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

    String category = switch (ticket.getTicketCategory()) {
        case TopUp -> "gagal top-up";
        case Payment -> "gagal pembayaran";
        case Transfer -> "gagal transfer";
        default -> "kategori tidak diketahui";
    };

    // List untuk menyimpan deskripsi dari setiap level status
    List<String> levels = new ArrayList<>();
    levels.add("transaksi dilakukan");
    levels.add("laporan diajukan");
    levels.add("laporan dalam proses");
    levels.add("laporan selesai diproses");
    levels.add("laporan diterima pelapor");

    // List untuk menyimpan hasil akhir yang akan dikembalikan
    List<TrackTicketStatusResponseDTO> trackTicketStatusDTOList = new ArrayList<>();

    // Iterasi untuk setiap level
    for (String level : levels) {
        // Cari history yang sesuai dengan deskripsi level
        TicketHistory relevantHistory = ticketHistories.stream()
                .filter(history -> history.getDescription().equals(level))
                .findFirst()
                .orElse(null);

        TrackTicketStatusResponseDTO trackTicketStatusDTO = new TrackTicketStatusResponseDTO();
        if (relevantHistory != null) {
            String pic = relevantHistory.getAdmin().getFirstName() + " " + relevantHistory.getAdmin().getLastName();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(relevantHistory.getDate());


            trackTicketStatusDTO.setPic(pic);
            trackTicketStatusDTO.setDate(formattedDate);
            trackTicketStatusDTO.setCategory(category);
            trackTicketStatusDTO.setDescription(relevantHistory.getDescription());
        } else {
            // Jika tidak ada history yang cocok, set nilai default
            trackTicketStatusDTO.setPic(null);
            trackTicketStatusDTO.setDate(null);
            trackTicketStatusDTO.setCategory(category);
            trackTicketStatusDTO.setDescription(level);
        }

        // Tambahkan ke dalam list yang akan dikembalikan
        trackTicketStatusDTOList.add(trackTicketStatusDTO);
    }

    return trackTicketStatusDTOList;
}


    @Override
    public List<TrackTicketStatusResponseDTO> trackMyTicketStatus(Authentication authentication, Long id, String account_number) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Account account = accountRepository.findByAccountNumber(account_number);
        checkAccountOwnership(user, account);

        Tickets ticket = ticketsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        String category = switch (ticket.getTicketCategory()) {
            case TopUp -> "gagal top-up";
            case Payment -> "gagal pembayaran";
            case Transfer -> "gagal transfer";
            default -> "kategori tidak diketahui";
        };

        if (!ticket.getTransaction().getAccount().equals(account)) {
            throw new RuntimeException("Ticket does not belong to the user");
        }

        List<TicketHistory> ticketHistories = ticketsHistoryRepository.findAllByTicket(ticket);

        return ticketHistories.stream().map(ticketHistory -> {
            TrackTicketStatusResponseDTO response = new TrackTicketStatusResponseDTO();
            response.setPic("BNI");
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticketHistory.getDate());
            response.setDate(formattedDate);
            response.setDescription(ticketHistory.getDescription());
            response.setCategory(category);
            return response;
        }).collect(Collectors.toList());
    }
}