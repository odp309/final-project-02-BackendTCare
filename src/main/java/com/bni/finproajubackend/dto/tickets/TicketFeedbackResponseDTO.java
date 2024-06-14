package com.bni.finproajubackend.dto.tickets;

import ch.qos.logback.classic.model.LevelModel;
import com.bni.finproajubackend.model.enumobject.StarRating;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@Builder
public class TicketFeedbackResponseDTO {

    @Setter
    private Integer rating;

    @Setter
    private String comment;
}




