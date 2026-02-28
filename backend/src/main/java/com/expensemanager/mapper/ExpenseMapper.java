package com.expensemanager.mapper;

import com.expensemanager.dto.request.ExpenseRequest;
import com.expensemanager.dto.response.ExpenseResponse;
import com.expensemanager.entity.Expense;
import org.springframework.stereotype.Component;

/**
 * Converts between Expense entity and its DTOs.
 * Keeps mapping logic centralized and out of service/controller layers.
 */
@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseRequest request, String category, boolean isAnomaly) {
        return Expense.builder()
                .date(request.getDate())
                .amount(request.getAmount())
                .vendorName(request.getVendorName().trim())
                .description(request.getDescription())
                .category(category)
                .isAnomaly(isAnomaly)
                .build();
    }

    public ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .date(expense.getDate())
                .amount(expense.getAmount())
                .vendorName(expense.getVendorName())
                .description(expense.getDescription())
                .category(expense.getCategory())
                .isAnomaly(expense.isAnomaly())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
