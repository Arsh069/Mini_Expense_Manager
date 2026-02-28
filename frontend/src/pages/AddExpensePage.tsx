import React, { useState } from 'react';
import { addExpense } from '../api/expenseApi';
import { Expense, ExpenseRequest } from '../types';

const INITIAL_FORM: ExpenseRequest = {
  date: new Date().toISOString().split('T')[0],
  amount: 0,
  vendorName: '',
  description: '',
};

const AddExpensePage: React.FC = () => {
  const [form, setForm] = useState<ExpenseRequest>(INITIAL_FORM);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [savedExpense, setSavedExpense] = useState<Expense | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: name === 'amount' ? parseFloat(value) || 0 : value }));
    setFieldErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});
    setSavedExpense(null);
    setLoading(true);

    try {
      const result = await addExpense(form);
      setSavedExpense(result);
      setForm(INITIAL_FORM);
    } catch (err: any) {
      if (err.response?.data?.fieldErrors) {
        setFieldErrors(err.response.data.fieldErrors);
      } else {
        setError(err.response?.data?.message || 'Failed to add expense. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <h1 className="page-title">Add Expense</h1>

      {error && <div className="alert alert-error">{error}</div>}

      {savedExpense && (
        <div className="alert alert-success">
          <strong>Expense added successfully!</strong>
          <span className="badge">{savedExpense.category}</span>
          {savedExpense.isAnomaly && (
            <span className="badge badge-anomaly">⚠ Anomaly Detected</span>
          )}
        </div>
      )}

      <form className="expense-form" onSubmit={handleSubmit} noValidate>
        <div className="form-group">
          <label htmlFor="date">Date *</label>
          <input
            id="date"
            name="date"
            type="date"
            value={form.date}
            onChange={handleChange}
            className={fieldErrors.date ? 'input-error' : ''}
            required
          />
          {fieldErrors.date && <span className="field-error">{fieldErrors.date}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="amount">Amount (₹) *</label>
          <input
            id="amount"
            name="amount"
            type="number"
            min="0.01"
            step="0.01"
            value={form.amount || ''}
            onChange={handleChange}
            placeholder="0.00"
            className={fieldErrors.amount ? 'input-error' : ''}
            required
          />
          {fieldErrors.amount && <span className="field-error">{fieldErrors.amount}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="vendorName">Vendor Name *</label>
          <input
            id="vendorName"
            name="vendorName"
            type="text"
            value={form.vendorName}
            onChange={handleChange}
            placeholder="e.g. Swiggy, Amazon, Uber"
            className={fieldErrors.vendorName ? 'input-error' : ''}
            required
          />
          {fieldErrors.vendorName && <span className="field-error">{fieldErrors.vendorName}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={form.description}
            onChange={handleChange}
            placeholder="Optional notes about this expense"
            rows={3}
          />
        </div>

        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Saving...' : 'Add Expense'}
        </button>
      </form>
    </div>
  );
};

export default AddExpensePage;
