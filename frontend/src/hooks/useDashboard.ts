import { useCallback, useEffect, useState } from 'react';
import { getAnomalies, getAnomalyCount, getMonthlyTotals, getTopVendors } from '../api/expenseApi';
import { CategoryTotal, Expense, TopVendor } from '../types';

interface DashboardData {
  monthlyTotals: CategoryTotal[];
  topVendors: TopVendor[];
  anomalies: Expense[];
  anomalyCount: number;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export const useDashboard = (): DashboardData => {
  const [monthlyTotals, setMonthlyTotals] = useState<CategoryTotal[]>([]);
  const [topVendors, setTopVendors] = useState<TopVendor[]>([]);
  const [anomalies, setAnomalies] = useState<Expense[]>([]);
  const [anomalyCount, setAnomalyCount] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [totals, vendors, anom, count] = await Promise.all([
        getMonthlyTotals(),
        getTopVendors(),
        getAnomalies(),
        getAnomalyCount(),
      ]);
      setMonthlyTotals(totals);
      setTopVendors(vendors);
      setAnomalies(anom);
      setAnomalyCount(count.count);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  return { monthlyTotals, topVendors, anomalies, anomalyCount, loading, error, refetch: fetchAll };
};
