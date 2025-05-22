import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaUsers, FaClipboardList, FaHistory, FaSignOutAlt } from 'react-icons/fa';
import '../../App.css';
import './Dashboard.css';
import UserView from '../UserView.tsx';
import ActivityView from '../ActivityView.tsx';
import AuditView from '../AuditView.tsx';

const AdminDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [selectedTab, setSelectedTab] = useState<'users' | 'activities' | 'audits'>('users');

    const handleLogout = () => {
        sessionStorage.removeItem('authToken');
        sessionStorage.removeItem('role');
        sessionStorage.removeItem('userId');
        navigate('/login');
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
