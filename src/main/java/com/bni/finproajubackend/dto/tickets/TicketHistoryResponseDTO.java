package com.bni.finproajubackend.dto.tickets;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Data
@NoArgsConstructor
public class TicketHistoryResponseDTO {
    private Long id;
    private String pic;
    private String description;
    private Date date;
    private Long next;
    private Long previous;
}
