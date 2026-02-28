import React, { useRef, useState } from 'react';
import { uploadCsv } from '../api/expenseApi';
import { CsvUploadResponse } from '../types';

const CsvUploadPage: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<CsvUploadResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files?.[0];
    if (selected) {
      if (!selected.name.endsWith('.csv')) {
        setError('Only CSV files are accepted.');
        setFile(null);
        return;
      }
      setFile(selected);
      setError(null);
      setResult(null);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a CSV file first.');
      return;
    }
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await uploadCsv(file);
      setResult(response);
      setFile(null);
      if (inputRef.current) inputRef.current.value = '';
    } catch (err: any) {
      setError(err.response?.data?.message || 'CSV upload failed. Please check your file format.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container">
      <h1 className="page-title">Upload CSV</h1>

      <div className="info-box">
        <strong>Expected CSV Format:</strong>
        <code className="code-block">date,amount,vendorName,description<br />
          2024-01-15,450.00,Swiggy,Dinner order<br />
          2024-01-16,2000.00,Amazon,Office supplies
        </code>
        <p className="hint">
          Columns: <strong>date</strong> (yyyy-MM-dd), <strong>amount</strong>, <strong>vendorName</strong>, <strong>description</strong> (optional)
        </p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="upload-area">
        <input
          ref={inputRef}
          type="file"
          accept=".csv"
          onChange={handleFileChange}
          className="file-input"
        />
        {file && <p className="file-name">Selected: <strong>{file.name}</strong> ({(file.size / 1024).toFixed(1)} KB)</p>}
        <button
          className="btn btn-primary"
          onClick={handleUpload}
          disabled={loading || !file}
        >
          {loading ? 'Uploading...' : 'Upload CSV'}
        </button>
      </div>

      {result && (
        <div className="upload-result">
          <h2>Upload Result</h2>
          <div className="result-stats">
            <div className="stat-card">
              <span className="stat-value">{result.totalRows}</span>
              <span className="stat-label">Total Rows</span>
            </div>
            <div className="stat-card stat-success">
              <span className="stat-value">{result.successCount}</span>
              <span className="stat-label">Successful</span>
            </div>
            <div className="stat-card stat-error">
              <span className="stat-value">{result.failureCount}</span>
              <span className="stat-label">Failed</span>
            </div>
          </div>

          {result.errors.length > 0 && (
            <div className="error-list">
              <h3>Errors</h3>
              <ul>
                {result.errors.map((err, i) => (
                  <li key={i} className="error-item">{err}</li>
                ))}
              </ul>
            </div>
          )}

          {result.savedExpenses.length > 0 && (
            <div className="saved-expenses">
              <h3>Saved Expenses ({result.savedExpenses.length})</h3>
              <table className="expense-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Vendor</th>
                    <th>Amount (₹)</th>
                    <th>Category</th>
                    <th>Anomaly</th>
                  </tr>
                </thead>
                <tbody>
                  {result.savedExpenses.map((exp) => (
                    <tr key={exp.id} className={exp.isAnomaly ? 'row-anomaly' : ''}>
                      <td>{exp.date}</td>
                      <td>{exp.vendorName}</td>
                      <td>{exp.amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                      <td><span className="badge">{exp.category}</span></td>
                      <td>{exp.isAnomaly ? <span className="badge badge-anomaly">⚠ Yes</span> : '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default CsvUploadPage;
