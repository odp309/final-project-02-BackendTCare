package com.bni.finproajubackend.dto.tickets;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketDetailsReportDTO {
    private ReporterDetail reporter_detail;
    private ReportDetail report_detail;
    private ReportStatusDetail report_status_detail;

    @Data
    @Builder
    public static class ReporterDetail {
        private String nama;
        private String account_number;
        private String address;
        private String no_handphone;
    }

    @Data
    @Builder
    public static class ReportDetail {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime transaction_date;
        private long transaction_number;
        private long amount;
        private String category;
        private String description;
        private String reference_num;
    }

    @Data
    @Builder
    public static class ReportStatusDetail {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime report_date;
        private String ticket_number;
        private String status;
        private boolean isReopened;
        private String next_reference_num;
    }
}

