package com.bni.finproajubackend.dto.tickets;

import lombok.*;


@Data
public class TicketFeedbackDTO {
    private ResultDTO result;

    @Getter
    @Setter
    public static class ResultDTO {
        private int rating;
        private String comment;
    }
}

