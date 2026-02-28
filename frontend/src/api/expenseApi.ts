import apiClient from './apiClient';
import {
  AnomalyCount,
  CategoryTotal,
  CsvUploadResponse,
  Expense,
  ExpenseRequest,
  TopVendor,
} from '../types';

export const addExpense = async (request: ExpenseRequest): Promise<Expense> => {
  const response = await apiClient.post<Expense>('/expenses', request);
  return response.data;
};

export const uploadCsv = async (file: File): Promise<CsvUploadResponse> => {
  const formData = new FormData();
  formData.append('file', file);
  const response = await apiClient.post<CsvUploadResponse>('/expenses/upload-csv', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
};

export const getMonthlyTotals = async (): Promise<CategoryTotal[]> => {
  const response = await apiClient.get<CategoryTotal[]>('/expenses/dashboard/monthly-totals');
  return response.data;
};

export const getTopVendors = async (): Promise<TopVendor[]> => {
  const response = await apiClient.get<TopVendor[]>('/expenses/dashboard/top-vendors');
  return response.data;
};

export const getAnomalies = async (): Promise<Expense[]> => {
  const response = await apiClient.get<Expense[]>('/expenses/dashboard/anomalies');
  return response.data;
};

export const getAnomalyCount = async (): Promise<AnomalyCount> => {
  const response = await apiClient.get<AnomalyCount>('/expenses/dashboard/anomalies/count');
  return response.data;
};
