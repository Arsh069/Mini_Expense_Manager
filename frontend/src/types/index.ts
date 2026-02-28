export interface Expense {
  id: string;
  date: string;
  amount: number;
  vendorName: string;
  description: string;
  category: string;
  isAnomaly: boolean;
  createdAt: string;
}

export interface ExpenseRequest {
  date: string;
  amount: number;
  vendorName: string;
  description?: string;
}

export interface CategoryTotal {
  year: number;
  month: number;
  category: string;
  total: number;
}

export interface TopVendor {
  vendorName: string;
  totalSpend: number;
}

export interface CsvUploadResponse {
  totalRows: number;
  successCount: number;
  failureCount: number;
  errors: string[];
  savedExpenses: Expense[];
}

export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: string;
  fieldErrors?: Record<string, string>;
}

export interface AnomalyCount {
  count: number;
}
