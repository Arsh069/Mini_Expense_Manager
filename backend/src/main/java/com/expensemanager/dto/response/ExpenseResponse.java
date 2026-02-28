package com.expensemanager.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private UUID id;
    private LocalDate date;
    private BigDecimal amount;
    private String vendorName;
    private String description;
    private String category;
    private boolean isAnomaly;
    private LocalDateTime createdAt;
}
