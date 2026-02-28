package com.expensemanager.strategy;

/**
 * Strategy interface for expense categorization.
 * Implementations can vary from rule-based to AI-based strategies.
 * Follows Open/Closed Principle – new strategies can be added without modifying existing code.
 */
public interface CategorizationStrategy {

    /**
     * Determines the category for a given vendor name.
     *
     * @param vendorName the name of the vendor
     * @return the category string; must never return null
     */
    String categorize(String vendorName);
}
