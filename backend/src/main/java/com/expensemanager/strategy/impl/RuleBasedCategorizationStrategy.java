package com.expensemanager.strategy.impl;

import com.expensemanager.repository.VendorCategoryMappingRepository;
import com.expensemanager.strategy.CategorizationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Rule-based categorization strategy.
 * Looks up vendor-to-category mappings from the database.
 * Defaults to "Others" if no mapping is found.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleBasedCategorizationStrategy implements CategorizationStrategy {

    private static final String DEFAULT_CATEGORY = "Others";

    private final VendorCategoryMappingRepository vendorCategoryMappingRepository;

    @Override
    public String categorize(String vendorName) {
        if (vendorName == null || vendorName.isBlank()) {
            log.warn("Vendor name is blank; defaulting to category '{}'", DEFAULT_CATEGORY);
            return DEFAULT_CATEGORY;
        }

        return vendorCategoryMappingRepository
                .findByVendorNameIgnoreCase(vendorName.trim())
                .map(mapping -> {
                    log.debug("Vendor '{}' mapped to category '{}'", vendorName, mapping.getCategory());
                    return mapping.getCategory();
                })
                .orElseGet(() -> {
                    log.debug("No mapping found for vendor '{}'; defaulting to '{}'", vendorName, DEFAULT_CATEGORY);
                    return DEFAULT_CATEGORY;
                });
    }
}
