package com.bni.finproajubackend.controller;

import com.bni.finproajubackend.dto.report.ReportResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateReportResponseDTO;
import com.bni.finproajubackend.dto.templateResponse.TemplateResponseDTO;
import com.bni.finproajubackend.interfaces.ReportInterface;
import com.bni.finproajubackend.interfaces.TemplateResInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/private/admin/reports-stats")
public class ReportController {

    @Autowired
    private ReportInterface reportService;
    @Autowired
    private TemplateResInterface responseService;

    @GetMapping("/status")
    public ResponseEntity getStatus(
            @RequestParam(required = false) String name,
            @RequestParam long year) {
        try {
            ReportResponseDTO result = reportService.getStatus(name, year);
            return ResponseEntity.ok(responseService.apiSuccessReport(result, "Get Status Data on year " + year));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Failed Getting Data" : e.getMessage()));
        }
    }

    @GetMapping("/category")
    public ResponseEntity getCategory(
            @RequestParam(required = false) String name,
            @RequestParam long year) {
        try {
            ReportResponseDTO result = reportService.getCategory(name, year);
            return ResponseEntity.ok(responseService.apiSuccessReport(result, "Get Category Data on year " + year));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Failed Getting Data" : e.getMessage()));
        }
    }

    @GetMapping("/rating")
    public ResponseEntity<TemplateResponseDTO<ReportResponseDTO>> getRating(
            @RequestParam(required = false) Integer rate,
            @RequestParam long year) {
        try {
            ReportResponseDTO result = reportService.getRating(rate, year);
            return ResponseEntity.ok(responseService.apiSuccess(result, "Get Rating Data on year " + year));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.apiFailed(null, e.getCause() == null ? "Failed Getting Data" : e.getMessage()));
        }
    }

}
