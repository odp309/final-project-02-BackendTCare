package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.DivisiTarget;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDTO {

    private long id;
    private long time_response;
    private DivisiTarget divisiTarget;
    private TicketStatus status;
    private long rating;
    private String ticketNumber;
    private Transaction transaction;
    private TicketCategories ticketCategory;
    private String description;
    private String referenceNumber;
    private LocalDateTime report_date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;
}
