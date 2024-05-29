package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.user.UserRequestDTO;
import com.bni.finproajubackend.model.ticket.Tickets;
import com.bni.finproajubackend.model.user.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TicketsInterface {
    Tickets createTickets(UserRequestDTO request, Authentication authentication);
    Tickets editTickets(String username, UserRequestDTO request);
    boolean deleteTickets(String username);
    List<Tickets> getTickets();
    Tickets getTickets(String username);

    Tickets getTickets(Object details);
}
