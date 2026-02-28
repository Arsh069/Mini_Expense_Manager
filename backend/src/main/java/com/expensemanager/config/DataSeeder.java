package com.expensemanager.config;

import com.expensemanager.entity.VendorCategoryMapping;
import com.expensemanager.repository.VendorCategoryMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds vendor-category mappings on application startup if the table is empty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final VendorCategoryMappingRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info("Vendor category mappings already seeded. Skipping.");
            return;
        }

        List<VendorCategoryMapping> mappings = List.of(
                mapping("Amazon", "Shopping"),
                mapping("Flipkart", "Shopping"),
                mapping("Myntra", "Shopping"),
                mapping("Swiggy", "Food & Dining"),
                mapping("Zomato", "Food & Dining"),
                mapping("Dominos", "Food & Dining"),
                mapping("McDonald's", "Food & Dining"),
                mapping("Starbucks", "Food & Dining"),
                mapping("Uber", "Transport"),
                mapping("Ola", "Transport"),
                mapping("Rapido", "Transport"),
                mapping("IRCTC", "Transport"),
                mapping("MakeMyTrip", "Travel"),
                mapping("Goibibo", "Travel"),
                mapping("AirIndia", "Travel"),
                mapping("IndiGo", "Travel"),
                mapping("Netflix", "Entertainment"),
                mapping("Spotify", "Entertainment"),
                mapping("PrimeVideo", "Entertainment"),
                mapping("BookMyShow", "Entertainment"),
                mapping("Apollo Pharmacy", "Healthcare"),
                mapping("1mg", "Healthcare"),
                mapping("Netmeds", "Healthcare"),
                mapping("Max Healthcare", "Healthcare"),
                mapping("Airtel", "Utilities"),
                mapping("Jio", "Utilities"),
                mapping("BSES", "Utilities"),
                mapping("Tata Power", "Utilities"),
                mapping("HDFC Bank", "Finance"),
                mapping("ICICI Bank", "Finance"),
                mapping("SBI", "Finance"),
                mapping("Zerodha", "Finance")
        );

        repository.saveAll(mappings);
        log.info("Seeded {} vendor-category mappings.", mappings.size());
    }

    private VendorCategoryMapping mapping(String vendorName, String category) {
        return VendorCategoryMapping.builder()
                .vendorName(vendorName)
                .category(category)
                .build();
    }
}
