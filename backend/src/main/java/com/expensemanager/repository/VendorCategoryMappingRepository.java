package com.expensemanager.repository;

import com.expensemanager.entity.VendorCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendorCategoryMappingRepository extends JpaRepository<VendorCategoryMapping, UUID> {

    Optional<VendorCategoryMapping> findByVendorNameIgnoreCase(String vendorName);
}
