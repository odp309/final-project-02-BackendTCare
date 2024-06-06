package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDTO {

    private String ticketNumber;
    private Transaction transaction;
    private TicketCategories ticketCategory;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

}
