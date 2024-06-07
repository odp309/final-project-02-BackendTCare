package com.bni.finproajubackend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReportResponseDTO {
    private long january;
    private long february;
    private long march;
    private long april;
    private long may;
    private long june;
    private long july;
    private long august;
    private long september;
    private long october;
    private long november;
    private long december;

    private long total;
    private long totalAllReports;
    private long year;
}

