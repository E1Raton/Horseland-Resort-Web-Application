import React, { useState, useEffect } from "react";
import "./ActivityView.css";
import RegisterButton from "../RegisterButton.tsx";
import useRegisteredActivities from "../../hooks/useRegisteredActivities.ts";
import { ActivityService } from "../../service/ActivityService.ts";
import { Activity } from "../../model/activity.model.tsx";

const ActivityView: React.FC = () => {
    const [activities, setActivities] = useState<Activity[]>([]);
    const [filteredActivities, setFilteredActivities] = useState<Activity[]>([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [sortBy, setSortBy] = useState("date");
    const [onlyFutureActivities, setOnlyFutureActivities] = useState(false);

    const userId = localStorage.getItem("userId");
    const { registeredActivities, registerActivity, unregisterActivity } = useRegisteredActivities(userId);

    useEffect(() => {
        fetchActivities();
    }, [userId, onlyFutureActivities]); // <-- Fetch again when user toggles future activities

    useEffect(() => {
        filterAndSortActivities();
    }, [activities, searchTerm, sortBy, onlyFutureActivities]);

    // Fetch activities from the backend
    const fetchActivities = async () => {
        try {
            let data;

            if (onlyFutureActivities) {
                data = await ActivityService.fetchFutureActivities(); // New backend endpoint
            } else {
                data = await ActivityService.fetchActivities(); // All activities
            }

            setActivities(data);

            // Set initial registration state
            if (userId) {
                const initialRegistrations = new Set<string>();
                data.forEach((activity: Activity) => {
                    activity.participants.forEach((participant) => {
                        if (participant.id === userId) {
                            initialRegistrations.add(activity.id);
                        }
                    });
                });

                initialRegistrations.forEach((activityId) => registerActivity(activityId));
            }
        } catch (error) {
            console.error("Failed to fetch activities", error);
        }
    };

    // Handles filtering and sorting
    const filterAndSortActivities = () => {
        let filtered = [...activities];

        // Search filter
        if (searchTerm.trim()) {
            filtered = filtered.filter((activity) =>
                activity.name.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }

        // Sorting
        filtered.sort((a, b) => {
            if (sortBy === "name") return a.name.localeCompare(b.name);
            return new Date(a.startDate).getTime() - new Date(b.startDate).getTime();
        });

        setFilteredActivities(filtered);
    };

    // Register the user for an activity
    const handleRegister = async (activityId: string) => {
        if (!userId) return;

        if (window.confirm("Are you sure you want to register for this activity?")) {
            const success = await ActivityService.registerActivity(activityId, userId);
            if (success) registerActivity(activityId);
        }
    };

    // Unregister the user from an activity
    const handleUnregister = async (activityId: string) => {
        if (!userId) return;

        if (window.confirm("Are you sure you want to unregister from this activity?")) {
            const success = await ActivityService.unregisterActivity(activityId, userId);
            if (success) unregisterActivity(activityId);
        }
    };

    return (
        <div className="activity-view">
            {/* Controls Section */}
            <div className="controls">
                <input
                    type="text"
                    placeholder="Search by name..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="search-input"
                />

                <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    className="sort-dropdown"
                >
                    <option value="date">Sort by Date</option>
                    <option value="name">Sort by Name</option>
                </select>

                <label className="filter-checkbox">
                    <input
                        type="checkbox"
                        checked={onlyFutureActivities}
                        onChange={(e) => setOnlyFutureActivities(e.target.checked)}
                    />
                    Show only future activities
                </label>
            </div>

            {/* Activities List Section */}
            <div className="activity-container">
                {filteredActivities.map((activity) => {
                    const isRegistered = registeredActivities.has(activity.id);

                    return (
                        <div key={activity.id} className="activity-card">
                            <h3>{activity.name}</h3>
                            <p>{activity.description}</p>
                            <p>
                                Start Date: {activity.startDate} <br /> End Date: {activity.endDate}
                            </p>

                            {new Date(activity.startDate) > new Date() ? (
                                <RegisterButton
                                    activityId={activity.id}
                                    isRegistered={isRegistered}
                                    onRegister={() => handleRegister(activity.id)}
                                    onUnregister={() => handleUnregister(activity.id)}
                                />
                            ) : (
                                <div className="activity-status">Activity has ended</div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default ActivityView;
