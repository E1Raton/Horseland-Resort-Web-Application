import React, { useState, useEffect } from "react";
import "./ActivityView.css";
import { ActivityService } from "../../service/ActivityService.ts";
import { Activity } from "../../model/activity.model.tsx";

const ActivityView: React.FC = () => {
    const [activities, setActivities] = useState<Activity[]>([]);
    const [filteredActivities, setFilteredActivities] = useState<Activity[]>([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [sortBy, setSortBy] = useState("date");
    const [onlyFutureActivities, setOnlyFutureActivities] = useState(false);
    const [userActivities, setUserActivities] = useState<Set<string>>(new Set()); // Store activity IDs the user is registered for
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const userId = sessionStorage.getItem("userId");

    useEffect(() => {
        if (userId) {
            fetchActivities();
        } else {
            setLoading(false);
        }
    }, [userId, onlyFutureActivities]);

    useEffect(() => {
        filterAndSortActivities();
    }, [activities, searchTerm, sortBy, onlyFutureActivities]);

    // Fetch activities from the backend
    // Fetch activities from the backend
    const fetchActivities = async () => {
        if (!userId) {
            return;
        }

        setLoading(true);
        setError(null);

        try {
            let data;

            // Fetch activities based on the "only future" filter
            if (onlyFutureActivities) {
                data = await ActivityService.fetchFutureActivities(); // Fetch only future activities
            } else {
                data = await ActivityService.fetchActivities(); // Fetch all activities
            }

            setActivities(data);

            // Check user registration for the activities
            updateUserActivities(data);

        } catch (err) {
            setError("Failed to fetch activities. Please try again later.");
            console.error("Error fetching activities:", err);
        } finally {
            setLoading(false);
        }
    };

    // Updates the userActivities set based on the activities fetched
    const updateUserActivities = (activities: Activity[]) => {
        const registeredActivityIds = new Set<string>();

        activities.forEach((activity: Activity) => {
            if (activity.participants && Array.isArray(activity.participants)) {
                // Check if the user is in the participantsIds for this activity
                if (activity.participants.some(participant => participant.id === userId)) {
                    registeredActivityIds.add(activity.id); // Add activity to registered set
                }
            }
        });

        setUserActivities(registeredActivityIds);
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
            setLoading(true);
            setError(null);
            try {
                const success = await ActivityService.registerActivity(activityId, userId);
                if (success) {
                    await fetchActivities(); // Refetch activities after registration
                }
            } catch (err) {
                setError("Failed to register for the activity.");
                console.error("Error registering activity:", err);
            } finally {
                setLoading(false);
            }
        }
    };

    if (loading) {
        return <div className="loading">Loading activities...</div>;
    }

    if (error) {
        return <div className="error">{error}</div>;
    }

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
                    const isRegistered = userActivities.has(activity.id); // Check if the user is registered for the activity
                    const isFutureActivity = new Date(activity.startDate) > new Date();

                    return (
                        <div key={activity.id} className="activity-card">
                            <h3>{activity.name}</h3>
                            <p>{activity.description}</p>
                            <p>
                                Start Date: {activity.startDate} <br /> End Date: {activity.endDate}
                            </p>

                            {isFutureActivity ? (
                                isRegistered ? (
                                    <div className="activity-status">Already registered</div>
                                ) : (
                                    <button className="register-button" onClick={() => handleRegister(activity.id)}>Register</button>
                                )
                            ) : (
                                <div className="activity-status">Registration closed</div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default ActivityView;