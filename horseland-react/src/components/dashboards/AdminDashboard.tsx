import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaUsers, FaClipboardList, FaHistory, FaSignOutAlt } from 'react-icons/fa';
import '../../App.css';
import './Dashboard.css';
import UserView from '../UserView.tsx';
import ActivityView from '../ActivityView.tsx';
import AuditView from '../AuditView.tsx';
import {AUTH_ENDPOINT} from "../../constants/api.ts";

const AdminDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [selectedTab, setSelectedTab] = useState<'users' | 'activities' | 'audits'>('users');

    const handleLogout = async () => {
        const authToken = sessionStorage.getItem('authToken');
        const userId = sessionStorage.getItem('userId');

        if (!authToken || !userId) {
            console.error("No token or userId found in sessionStorage.");
            navigate('/login');
            return;
        }

        try {
            const response = await fetch(`${AUTH_ENDPOINT}/logout/${userId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok) {
                // 🧹 Clean up local session
                sessionStorage.removeItem('authToken');
                sessionStorage.removeItem('role');
                sessionStorage.removeItem('userId');

                // 🔁 Navigate to login
                navigate('/login');
            } else {
                console.error("Failed to log out:", response.status);
                // Optionally still clear local data to force logout
                sessionStorage.clear();
                navigate('/login');
            }
        } catch (error) {
            console.error("Error during logout:", error);
            sessionStorage.clear();
            navigate('/login');
        }
    };

    return (
        <div className="dashboard-container">
            {/* Sidebar */}
            <aside className="sidebar">
                <h2 className="sidebar-title">Admin Panel</h2>
                <nav>
                    <ul className="sidebar-menu">
                        <li
                            className={selectedTab === 'users' ? 'active' : ''}
                            onClick={() => setSelectedTab('users')}
                        >
                            <FaUsers size={20} className="icon" /> Users
                        </li>
                        <li
                            className={selectedTab === 'activities' ? 'active' : ''}
                            onClick={() => setSelectedTab('activities')}
                        >
                            <FaClipboardList size={20} className="icon" /> Activities
                        </li>
                        <li
                            className={selectedTab === 'audits' ? 'active' : ''}
                            onClick={() => setSelectedTab('audits')}
                        >
                            <FaHistory size={20} className="icon" /> Audit Log
                        </li>
                        <li className="logout" onClick={handleLogout}>
                            <FaSignOutAlt size={20} className="icon" /> Logout
                        </li>
                    </ul>
                </nav>
            </aside>

            {/* Main Content */}
            <main className="content">
                {selectedTab === 'users' && <UserView />}
                {selectedTab === 'activities' && <ActivityView />}
                {selectedTab === 'audits' && <AuditView />}
            </main>
        </div>
    );
};

export default AdminDashboard;
