package com.expensemanager.repository;

import com.expensemanager.dto.response.CategoryTotalResponse;
import com.expensemanager.dto.response.TopVendorResponse;
import com.expensemanager.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    /**
     * Calculates the average expense amount for a given category.
     */
    @Query("SELECT AVG(e.amount) FROM Expense e WHERE e.category = :category")
    Optional<BigDecimal> findAverageAmountByCategory(String category);

    /**
     * Returns monthly totals grouped by year, month, and category.
     */
    @Query("""
            SELECT new com.expensemanager.dto.response.CategoryTotalResponse(
                YEAR(e.date), MONTH(e.date), e.category, SUM(e.amount)
            )
            FROM Expense e
            GROUP BY YEAR(e.date), MONTH(e.date), e.category
            ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC, e.category
            """)
    List<CategoryTotalResponse> findMonthlyTotalsPerCategory();

    /**
     * Returns the top 5 vendors by total spend.
     */
    @Query("""
            SELECT new com.expensemanager.dto.response.TopVendorResponse(
                e.vendorName, SUM(e.amount)
            )
            FROM Expense e
            GROUP BY e.vendorName
            ORDER BY SUM(e.amount) DESC
            LIMIT 5
            """)
    List<TopVendorResponse> findTop5VendorsByTotalSpend();

    /**
     * Returns all expenses flagged as anomalies.
     */
    List<Expense> findByIsAnomalyTrueOrderByDateDesc();

    /**
     * Counts all anomalous expenses.
     */
    long countByIsAnomalyTrue();
}
