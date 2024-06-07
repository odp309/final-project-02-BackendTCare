package com.bni.finproajubackend.dto.templateResponse;

import lombok.Data;

@Data
public class ReportResponseDTO extends StatusResponseDTO {
    private long total;
    private long total_all_year;
    private long year;
}
