package com.expensemanager.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTotalResponse {
    private int year;
    private int month;
    private String category;
    private BigDecimal total;
}
