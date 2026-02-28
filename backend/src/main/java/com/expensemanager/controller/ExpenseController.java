package com.expensemanager.controller;

import com.expensemanager.dto.request.ExpenseRequest;
import com.expensemanager.dto.response.*;
import com.expensemanager.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@Valid @RequestBody ExpenseRequest request) {
        log.info("POST /api/v1/expenses - Adding expense for vendor: {}", request.getVendorName());
        ExpenseResponse response = expenseService.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvUploadResponse> uploadCsv(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/expenses/upload-csv - Filename: {}", file.getOriginalFilename());
        CsvUploadResponse response = expenseService.uploadCsv(file);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/dashboard/monthly-totals")
    public ResponseEntity<List<CategoryTotalResponse>> getMonthlyTotals() {
        return ResponseEntity.ok(expenseService.getMonthlyTotalsPerCategory());
    }

    @GetMapping("/dashboard/top-vendors")
    public ResponseEntity<List<TopVendorResponse>> getTopVendors() {
        return ResponseEntity.ok(expenseService.getTop5Vendors());
    }

    @GetMapping("/dashboard/anomalies")
    public ResponseEntity<List<ExpenseResponse>> getAnomalies() {
        return ResponseEntity.ok(expenseService.getAnomalies());
    }

    @GetMapping("/dashboard/anomalies/count")
    public ResponseEntity<Map<String, Long>> getAnomalyCount() {
        return ResponseEntity.ok(Map.of("count", expenseService.getAnomalyCount()));
    }
}
