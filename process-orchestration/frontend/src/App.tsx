import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import ProcessDesigner from './pages/ProcessDesigner';
import './App.css';

const App: React.FC = () => {
  return (
    <Router>
      <ToastContainer position="top-right" autoClose={3000} />
      <Routes>
        <Route path="/" element={<ProcessDesigner />} />
      </Routes>
    </Router>
  );
};

export default App;
