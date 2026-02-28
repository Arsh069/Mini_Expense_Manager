package com.expensemanager.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvUploadResponse {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    private List<ExpenseResponse> savedExpenses;
}
