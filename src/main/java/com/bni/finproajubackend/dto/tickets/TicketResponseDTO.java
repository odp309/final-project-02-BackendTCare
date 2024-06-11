package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDTO {

    private long id;
    private String ticket_number;
    private String category;
    private TicketCategories ticket_category;
    private long time_response;
    private DivisionTarget division_target;
    private String status;
    private long rating;
    private Transaction transaction;
    private String description;
    private String reference_number;
    private LocalDateTime report_date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;
}


