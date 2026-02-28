import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import AddExpensePage from './pages/AddExpensePage';
import CsvUploadPage from './pages/CsvUploadPage';
import DashboardPage from './pages/DashboardPage';
import './styles.css';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/add" element={<AddExpensePage />} />
          <Route path="/upload" element={<CsvUploadPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
};

export default App;
