// components/NotificationView.tsx

import React, { useEffect, useState } from 'react';
import { Notification } from "../model/notification.model.tsx";
import {NOTIFICATION_ENDPOINT} from "../constants/api.ts";

const NotificationView: React.FC = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [loading, setLoading] = useState(true);
    const [userId, setUserId] = useState<string | null>(null);

    // Get user ID from sessionStorage on mount
    useEffect(() => {
        const storedUserId = sessionStorage.getItem('userId');
        setUserId(storedUserId);
    }, []);

    // Fetch notifications when userId is available
    useEffect(() => {
        if (!userId) return;

        const fetchNotifications = async () => {
            try {
                const response = await fetch(`${NOTIFICATION_ENDPOINT}/${userId}`);
                const data = await response.json();
                setNotifications(data);
            } catch (error) {
                console.error('Error fetching notifications:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, [userId]);

    const handleDisable = async (activityId: string) => {
        if (!userId) return;

        try {
            await fetch(`${NOTIFICATION_ENDPOINT}/${userId}/${activityId}`, {
                method: 'DELETE',
            });
            setNotifications((prev) =>
                prev.filter((n) => n.activityId !== activityId)
            );
        } catch (error) {
            console.error('Failed to disable notification:', error);
        }
    };

    if (!userId) return <p>No user ID found in session. Please log in.</p>;
    if (loading) return <p>Loading notifications...</p>;
    if (notifications.length === 0) return <p>No notifications found.</p>;

    return (
        <div className="space-y-4">
            {notifications.map((notification) => (
                <div
                    key={notification.id}
                    className="p-4 bg-white shadow rounded flex justify-between items-start"
                >
                    <div>
                        <h3 className="text-lg font-semibold">{notification.title}</h3>
                        <p className="text-sm text-gray-600">{notification.message}</p>
                        <span className="text-xs text-gray-400">
              {new Date(notification.dateTime).toLocaleString()}
            </span>
                    </div>
                    <button
                        onClick={() => handleDisable(notification.activityId)}
                        className="ml-4 bg-red-500 text-white text-sm px-3 py-1 rounded hover:bg-red-600"
                    >
                        Disable
                    </button>
                </div>
            ))}
        </div>
    );
};

export default NotificationView;
