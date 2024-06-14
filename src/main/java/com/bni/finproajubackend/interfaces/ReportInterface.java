package com.bni.finproajubackend.interfaces;

import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface ReportInterface {
    ReportResponseDTO getReports(@RequestParam(required = false) Map<String, String> queryParams, String path);
}
