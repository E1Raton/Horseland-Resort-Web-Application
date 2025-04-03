import React, { useState, useEffect } from "react";
import { FaRegTimesCircle } from "react-icons/fa"; // For "Registered" and "Unregister" icons
import "./ActivityView.css"

interface Activity {
    id: string;
    name: string;
    description: string;
    startDate: string;
    endDate: string;
    participants: { id: string }[]; // assuming participants is an array of user objects with an id field
}

const ActivityView: React.FC = () => {
    const [activities, setActivities] = useState<Activity[]>([]);
    const [registeredActivities, setRegisteredActivities] = useState<Set<string>>(new Set());
    const userId = localStorage.getItem("userId"); // Get the logged-in user's ID from localStorage (or context)

    useEffect(() => {
        fetchActivities();
    }, []);

    // Fetch the activities from the backend
    const fetchActivities = async () => {
        try {
            const response = await fetch("http://localhost:8080/activity");
            if (response.ok) {
                const data = await response.json();
                setActivities(data);

                // Check if the logged-in user is already registered for any activity
                const initialRegistrations = new Set<string>();

                // Iterate through each activity to check if the user is a participant
                data.forEach((activity: Activity) => {
                    activity.participants.forEach((participant) => {
                        if (participant.id === userId) {
                            initialRegistrations.add(activity.id);
                        }
                    });
                });

                // Update the registeredActivities state
                setRegisteredActivities(initialRegistrations);
                // Persist initial registrations in localStorage
                localStorage.setItem("registeredActivities", JSON.stringify(Array.from(initialRegistrations)));
            } else {
                console.error("Failed to fetch activities");
            }
        } catch (error) {
            console.error("Error fetching activities:", error);
        }
    };

    // Register the user for an activity
    const handleRegister = async (activityId: string) => {
        if (!userId) return; // Make sure the user is logged in

        const confirmed = window.confirm("Are you sure you want to register for this activity?");
        if (!confirmed) return;

        try {
            const response = await fetch(`http://localhost:8080/activity/${activityId}/register/${userId}`, {
                method: "PUT",
            });

            if (response.ok) {
                // After successful registration, update the state to reflect the new registration
                setRegisteredActivities((prev) => {
                    const updatedSet = new Set(prev);
                    updatedSet.add(activityId);
                    // Persist updated registrations in localStorage
                    localStorage.setItem("registeredActivities", JSON.stringify(Array.from(updatedSet)));
                    return updatedSet;
                });
            } else {
                console.error("Failed to register for the activity");
            }
        } catch (error) {
            console.error("Error registering for activity:", error);
        }
    };

    // Unregister the user from an activity
    const handleUnregister = async (activityId: string) => {
        if (!userId) return; // Make sure the user is logged in

        const confirmed = window.confirm("Are you sure you want to unregister from this activity?");
        if (!confirmed) return;

        try {
            const response = await fetch(`http://localhost:8080/activity/${activityId}/deregister/${userId}`, {
                method: "PUT",
            });

            if (response.ok) {
                // After successful unregistration, update the state to reflect the new unregistration
                setRegisteredActivities((prev) => {
                    const updatedSet = new Set(prev);
                    updatedSet.delete(activityId);
                    // Persist updated registrations in localStorage
                    localStorage.setItem("registeredActivities", JSON.stringify(Array.from(updatedSet)));
                    return updatedSet;
                });
            } else {
                console.error("Failed to unregister from the activity");
            }
        } catch (error) {
            console.error("Error unregistering from activity:", error);
        }
    };

    return (
        <div className="activity-container">
            {activities.map((activity) => {
                // Check if the user is registered for the current activity
                const isRegistered = registeredActivities.has(activity.id);

                return (
                    <div key={activity.id} className="activity-card">
                        <h3>{activity.name}</h3>
                        <p>{activity.description}</p>
                        <p>
                            Start Date: {activity.startDate} <br /> End Date: {activity.endDate}
                        </p>

                        {/* Only show the button if the activity is in the future */}
                        {new Date(activity.startDate) > new Date() ? (
                            isRegistered ? (
                                // If the user is registered, show "Unregister" button
                                <button
                                    className="unregister-btn"
                                    onClick={() => handleUnregister(activity.id)}
                                >
                                    <FaRegTimesCircle /> Unregister
                                </button>
                            ) : (
                                // If the user is not registered, show "Register" button
                                <button
                                    className="register-btn"
                                    onClick={() => handleRegister(activity.id)}
                                >
                                    Register
                                </button>
                            )
                        ) : (
                            <div className="activity-status">Activity has ended</div>
                        )}
                    </div>
                );
            })}
        </div>
    );
};

export default ActivityView;
