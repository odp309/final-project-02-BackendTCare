package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.PaginationDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

public interface TicketReportsInterface {
    PaginationDTO getAllTickets(
            String user,
            @RequestParam(required = false) String account_number,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String ticket_number,
            @RequestParam(required = false) String created_at,
            @RequestParam(required = false) String division,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false, defaultValue = "created_at") String sort_by,
            @RequestParam(required = false, defaultValue = "asc") String order,
            Authentication authentication
    ) throws IllegalAccessException;
}
