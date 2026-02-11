import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MissionPage from './pages/mission/MissionPage';
import MissionDetailPage from './pages/mission/MissionDetailPage';
import ClientMissionsPage from './pages/mission/ClientMissionsPage';

import LoginPage from './pages/LoginPage';
import ClientPage from './pages/client/ClientPage'
import FreelancePage from './pages/freelance/FreelancePage'

import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { OpenAPI } from './api';
import FreelanceMissions from './pages/freelance/FreelanceMission';
import { NotificationProvider } from './context/NotificationContext';

const App: React.FC = () => {
    OpenAPI.BASE = OpenAPI.BASE = process.env.REACT_APP_BACK_API_URL || 'http://default-api-url.com';

    return (
        <Router>
            <NotificationProvider>
                <ToastContainer position="top-right" autoClose={3000} />
                <Routes>
                    <Route path="/missions/:id/:titre" element={<MissionPage />} />
                    <Route path="/missions/:id" element={<MissionDetailPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/clients/:nom" element={<ClientPage />} />
                    <Route path="/freelance/:nom" element={<FreelancePage />} />

                    <Route path="/clients/:id/missions" element={<ClientMissionsPage />} />
                    <Route path="/freelances/:id/missions" element={<FreelanceMissions />} />
                </Routes>
            </NotificationProvider>
        </Router>
    );
};

export default App;