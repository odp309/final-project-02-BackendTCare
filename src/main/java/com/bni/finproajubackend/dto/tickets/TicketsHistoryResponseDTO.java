package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TicketsHistoryResponseDTO {
    private Long id;
    private String pic;
    private String description;
    private Date date;
    private Long next;
    private Long previous;
}
