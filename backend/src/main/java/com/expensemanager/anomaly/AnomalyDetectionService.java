package com.expensemanager.anomaly;

import com.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Encapsulates anomaly detection logic.
 * An expense is flagged as anomalous if its amount exceeds 3× the category average.
 * If it's the first expense in a category, it is never flagged as anomalous.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyDetectionService {

    private static final BigDecimal ANOMALY_MULTIPLIER = BigDecimal.valueOf(3);

    private final ExpenseRepository expenseRepository;

    /**
     * Determines whether the given amount is anomalous for the specified category.
     *
     * @param category the expense category
     * @param amount   the expense amount to evaluate
     * @return true if the amount exceeds 3× the category average, false otherwise
     */
    public boolean isAnomaly(String category, BigDecimal amount) {
        Optional<BigDecimal> avgOptional = expenseRepository.findAverageAmountByCategory(category);

        if (avgOptional.isEmpty()) {
            log.debug("No existing expenses in category '{}'; not marking as anomaly.", category);
            return false;
        }

        BigDecimal average = avgOptional.get();
        BigDecimal threshold = average.multiply(ANOMALY_MULTIPLIER);
        boolean anomaly = amount.compareTo(threshold) > 0;

        log.debug("Category '{}': avg={}, threshold={}, amount={}, isAnomaly={}",
                category, average, threshold, amount, anomaly);

        return anomaly;
    }
}
