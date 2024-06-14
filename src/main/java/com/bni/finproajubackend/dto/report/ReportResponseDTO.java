package com.bni.finproajubackend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ReportResponseDTO {
    private Map<String, Object> result;
    private int total;
    private int total_reports;
    private String year;
}

