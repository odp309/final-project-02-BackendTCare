package com.bni.finproajubackend.dto.tickets;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class TicketResponseDTO {

    private Long id;
    private String ticketNumber;
    private Long transactionId;
    private Long ticketCategoryId;
    private String description;
    private String createdAt;

}
