package com.bni.finproajubackend.dto.tickets;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
public class CreateFeedbackResponseDTO {
    private CreateFeedbackResponseDTO.ResultDTO result;

    @Getter
    @Setter
    public static class ResultDTO {
        private String ticket_number;
        private int rating;
        private String comment;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime created_at;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updated_at;
    }
}
