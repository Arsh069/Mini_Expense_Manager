package com.expensemanager.service.impl;

import com.expensemanager.anomaly.AnomalyDetectionService;
import com.expensemanager.dto.request.ExpenseRequest;
import com.expensemanager.dto.response.*;
import com.expensemanager.entity.Expense;
import com.expensemanager.exception.CsvParseException;
import com.expensemanager.mapper.ExpenseMapper;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.service.ExpenseService;
import com.expensemanager.strategy.CategorizationStrategy;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int CSV_EXPECTED_COLUMNS = 4;

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final CategorizationStrategy categorizationStrategy;
    private final AnomalyDetectionService anomalyDetectionService;

    @Override
    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest request) {
        log.info("Adding expense for vendor '{}' with amount {}", request.getVendorName(), request.getAmount());

        String category = categorizationStrategy.categorize(request.getVendorName());
        boolean isAnomaly = anomalyDetectionService.isAnomaly(category, request.getAmount());

        Expense expense = expenseMapper.toEntity(request, category, isAnomaly);
        Expense saved = expenseRepository.save(expense);

        log.info("Expense saved with id={}, category='{}', isAnomaly={}", saved.getId(), category, isAnomaly);
        return expenseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CsvUploadResponse uploadCsv(MultipartFile file) {
        log.info("Processing CSV upload: filename={}, size={}", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            throw new CsvParseException("Uploaded CSV file is empty.");
        }

        List<String[]> rows = parseCsv(file);

        List<ExpenseResponse> savedExpenses = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // Skip header row if present
        int startIndex = isHeaderRow(rows) ? 1 : 0;

        for (int i = startIndex; i < rows.size(); i++) {
            String[] row = rows.get(i);
            int rowNumber = i + 1;

            try {
                ExpenseRequest request = parseRowToRequest(row, rowNumber);
                ExpenseResponse response = addExpense(request);
                savedExpenses.add(response);
                successCount++;
            } catch (Exception e) {
                log.warn("Failed to process CSV row {}: {}", rowNumber, e.getMessage());
                errors.add(String.format("Row %d: %s", rowNumber, e.getMessage()));
                failureCount++;
            }
        }

        int totalRows = rows.size() - startIndex;
        log.info("CSV processing complete: total={}, success={}, failure={}", totalRows, successCount, failureCount);

        return CsvUploadResponse.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .failureCount(failureCount)
                .errors(errors)
                .savedExpenses(savedExpenses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTotalResponse> getMonthlyTotalsPerCategory() {
        return expenseRepository.findMonthlyTotalsPerCategory();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopVendorResponse> getTop5Vendors() {
        return expenseRepository.findTop5VendorsByTotalSpend();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAnomalies() {
        return expenseRepository.findByIsAnomalyTrueOrderByDateDesc()
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getAnomalyCount() {
        return expenseRepository.countByIsAnomalyTrue();
    }

    // ---- Private helpers ----

    private List<String[]> parseCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            throw new CsvParseException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    private boolean isHeaderRow(List<String[]> rows) {
        if (rows.isEmpty()) return false;
        String firstCell = rows.get(0)[0].trim().toLowerCase();
        return firstCell.equals("date") || firstCell.equals("\"date\"");
    }

    private ExpenseRequest parseRowToRequest(String[] row, int rowNumber) {
        if (row.length < CSV_EXPECTED_COLUMNS) {
            throw new IllegalArgumentException(
                    String.format("Expected %d columns but found %d.", CSV_EXPECTED_COLUMNS, row.length));
        }

        String dateStr = row[0].trim();
        String amountStr = row[1].trim();
        String vendorName = row[2].trim();
        String description = row.length > 3 ? row[3].trim() : "";

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format '" + dateStr + "'. Expected yyyy-MM-dd.");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount '" + amountStr + "'.");
        }

        if (vendorName.isBlank()) {
            throw new IllegalArgumentException("Vendor name must not be blank.");
        }

        return ExpenseRequest.builder()
                .date(date)
                .amount(amount)
                .vendorName(vendorName)
                .description(description)
                .build();
    }
}
