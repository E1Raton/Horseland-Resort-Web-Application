import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaHorseHead, FaClipboardList, FaBell, FaSignOutAlt } from "react-icons/fa"; // React Icons
import HorseView from "../HorseView.tsx";
import ActivityView from "../ActivityView/ActivityView.tsx";
import "./UserDashboard.css";

const UserDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [selectedTab, setSelectedTab] = useState<"horses" | "activities" | "notifications">("horses");

    const handleLogout = () => {
        localStorage.removeItem("authToken");
        localStorage.removeItem("userRole");
        navigate("/login");
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
                {selectedTab === "notifications" && <div className="notification-box">No new notifications</div>}
            </main>
        </div>
    );
};

export default UserDashboard;