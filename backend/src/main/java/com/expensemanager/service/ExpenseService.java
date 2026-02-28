package com.expensemanager.service;

import com.expensemanager.dto.request.ExpenseRequest;
import com.expensemanager.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse addExpense(ExpenseRequest request);

    CsvUploadResponse uploadCsv(MultipartFile file);

    List<CategoryTotalResponse> getMonthlyTotalsPerCategory();

    List<TopVendorResponse> getTop5Vendors();

    List<ExpenseResponse> getAnomalies();

    long getAnomalyCount();
}
