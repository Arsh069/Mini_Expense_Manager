import React from 'react';
import { useDashboard } from '../hooks/useDashboard';

const MONTH_NAMES = [
  '', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
];

const DashboardPage: React.FC = () => {
  const { monthlyTotals, topVendors, anomalies, anomalyCount, loading, error, refetch } = useDashboard();

  if (loading) {
    return (
      <div className="page-container">
        <h1 className="page-title">Dashboard</h1>
        <div className="loading">Loading dashboard data...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <h1 className="page-title">Dashboard</h1>
        <div className="alert alert-error">{error}</div>
        <button className="btn btn-secondary" onClick={refetch}>Retry</button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="dashboard-header">
        <h1 className="page-title">Dashboard</h1>
        <button className="btn btn-secondary btn-sm" onClick={refetch}>↻ Refresh</button>
      </div>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="summary-card">
          <span className="summary-value">{topVendors.length}</span>
          <span className="summary-label">Active Vendors</span>
        </div>
        <div className="summary-card">
          <span className="summary-value">{monthlyTotals.length}</span>
          <span className="summary-label">Category-Month Records</span>
        </div>
        <div className={`summary-card ${anomalyCount > 0 ? 'summary-card-danger' : ''}`}>
          <span className="summary-value">{anomalyCount}</span>
          <span className="summary-label">Anomalies Detected</span>
        </div>
      </div>

      {/* Monthly Category Totals */}
      <section className="dashboard-section">
        <h2>Monthly Totals by Category</h2>
        {monthlyTotals.length === 0 ? (
          <p className="empty-state">No expense data available yet.</p>
        ) : (
          <table className="expense-table">
            <thead>
              <tr>
                <th>Year</th>
                <th>Month</th>
                <th>Category</th>
                <th>Total (₹)</th>
              </tr>
            </thead>
            <tbody>
              {monthlyTotals.map((item, i) => (
                <tr key={i}>
                  <td>{item.year}</td>
                  <td>{MONTH_NAMES[item.month]}</td>
                  <td><span className="badge">{item.category}</span></td>
                  <td className="amount-cell">
                    {item.total.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {/* Top 5 Vendors */}
      <section className="dashboard-section">
        <h2>Top 5 Vendors by Spend</h2>
        {topVendors.length === 0 ? (
          <p className="empty-state">No vendor data available yet.</p>
        ) : (
          <div className="vendor-list">
            {topVendors.map((vendor, index) => (
              <div key={vendor.vendorName} className="vendor-item">
                <div className="vendor-rank">#{index + 1}</div>
                <div className="vendor-name">{vendor.vendorName}</div>
                <div className="vendor-amount">
                  ₹{vendor.totalSpend.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                </div>
                <div className="vendor-bar-wrapper">
                  <div
                    className="vendor-bar"
                    style={{
                      width: `${(vendor.totalSpend / topVendors[0].totalSpend) * 100}%`,
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Anomalies */}
      <section className="dashboard-section">
        <h2>
          Anomalous Expenses
          {anomalyCount > 0 && <span className="anomaly-badge">{anomalyCount}</span>}
        </h2>
        {anomalies.length === 0 ? (
          <p className="empty-state">No anomalies detected. Great job keeping expenses in check!</p>
        ) : (
          <table className="expense-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Vendor</th>
                <th>Amount (₹)</th>
                <th>Category</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              {anomalies.map((exp) => (
                <tr key={exp.id} className="row-anomaly">
                  <td>{exp.date}</td>
                  <td>{exp.vendorName}</td>
                  <td className="amount-cell">
                    {exp.amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </td>
                  <td><span className="badge">{exp.category}</span></td>
                  <td>{exp.description || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
};

export default DashboardPage;
