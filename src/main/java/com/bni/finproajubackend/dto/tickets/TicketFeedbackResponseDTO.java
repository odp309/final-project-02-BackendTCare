package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class TicketFeedbackResponseDTO {
    private Integer rating;
    private String comment;
}




