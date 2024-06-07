package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TicketStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDTO {

    private long id;
    private String ticket_number;
    private TicketCategories category;
    private long time_response;
    private DivisionTarget division_target;
    private TicketStatus status;
    private long rating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;
}
