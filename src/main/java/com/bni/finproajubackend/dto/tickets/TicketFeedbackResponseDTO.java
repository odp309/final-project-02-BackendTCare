package com.bni.finproajubackend.dto.tickets;

import ch.qos.logback.classic.model.LevelModel;
import com.bni.finproajubackend.model.enumobject.StarRating;
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




