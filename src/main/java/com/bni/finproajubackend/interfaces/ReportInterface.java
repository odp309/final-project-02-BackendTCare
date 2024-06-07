package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReportInterface {
    ReportResponseDTO getCategory(@RequestParam(required = false) String category, long year);
    ReportResponseDTO getStatus(@RequestParam(required = false) String status, long year);
    ReportResponseDTO getRating(@RequestParam(required = false) Integer rate, long year);
}
