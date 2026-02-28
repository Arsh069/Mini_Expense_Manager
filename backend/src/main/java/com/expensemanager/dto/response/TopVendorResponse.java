package com.expensemanager.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopVendorResponse {
    private String vendorName;
    private BigDecimal totalSpend;
}
