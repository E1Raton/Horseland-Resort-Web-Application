import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaHorseHead, FaClipboardList, FaBell, FaSignOutAlt } from "react-icons/fa"; // React Icons
import HorseView from "../HorseView.tsx";
import ActivityView from "../ActivityView/ActivityView.tsx";
import "./Dashboard.css";
import NotificationView from "../NotificationView.tsx";
import {AUTH_ENDPOINT} from "../../constants/api.ts";

const UserDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [selectedTab, setSelectedTab] = useState<"horses" | "activities" | "notifications">("horses");

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
                <h2 className="sidebar-title">Dashboard</h2>
                <nav>
                    <ul className="sidebar-menu">
                        <li className={selectedTab === "horses" ? "active" : ""} onClick={() => setSelectedTab("horses")}>
                            <FaHorseHead size={20} className="icon" /> Horses
                        </li>
                        <li className={selectedTab === "activities" ? "active" : ""} onClick={() => setSelectedTab("activities")}>
                            <FaClipboardList size={20} className="icon" /> Activities
                        </li>
                        <li className={selectedTab === "notifications" ? "active" : ""} onClick={() => setSelectedTab("notifications")}>
                            <FaBell size={20} className="icon" /> Notifications
                        </li>
                        <li className="logout" onClick={handleLogout}>
                            <FaSignOutAlt size={20} className="icon" /> Logout
                        </li>
                    </ul>
                </nav>
            </aside>

            {/* Content */}
            <main className="content">
                {selectedTab === "horses" && <HorseView />}
                {selectedTab === "activities" && <ActivityView />}
                {selectedTab === "notifications" && <NotificationView />}
            </main>
        </div>
    );
};

export default UserDashboard;