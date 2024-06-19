package com.bni.finproajubackend.dto.tickets;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
public class CreateFeedbackResponseDTO {
    private FeedbackDetails result;

    public CreateFeedbackResponseDTO(FeedbackDetails result) {
        this.result = result;
    }

    public CreateFeedbackResponseDTO() {

    }

    // Inner class for feedback details
    @Setter
    @Getter
    public static class FeedbackDetails {
        // Getters and Setters
        private String ticket_number;
        private int rating;
        private String comment;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
        @LastModifiedDate
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;
    }
}
