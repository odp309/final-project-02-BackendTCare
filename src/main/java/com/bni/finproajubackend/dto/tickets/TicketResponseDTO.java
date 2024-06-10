package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.enumobject.DivisionTarget;
import com.bni.finproajubackend.model.enumobject.TicketCategories;
import com.bni.finproajubackend.model.enumobject.TransactionCategories;
import com.bni.finproajubackend.model.user.nasabah.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

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
    private Map<String, Object> reporter_detail;
    private Map<String, Object> report_detail;
    private Map<String, Object> report_status_detail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;
}


