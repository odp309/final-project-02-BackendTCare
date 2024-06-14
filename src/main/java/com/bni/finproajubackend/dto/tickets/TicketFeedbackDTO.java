package com.bni.finproajubackend.dto.tickets;

import com.bni.finproajubackend.model.ticket.TicketFeedback;
import lombok.*;


@Data
public class TicketFeedbackDTO {
    private ResultDTO result;

    public TicketFeedback getTicketFeedback() {
        return getTicketFeedback();
    }

    @Getter
    @Setter
    public static class ResultDTO {
        private int rating;
        private String comment;
    }
}

