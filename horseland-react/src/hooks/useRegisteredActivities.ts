import { useState, useEffect } from "react";

// This hook will handle fetching and storing activities in localStorage
const useRegisteredActivities = (userId: string | null) => {
    const [registeredActivities, setRegisteredActivities] = useState<Set<string>>(new Set());

    useEffect(() => {
        if (!userId) return; // Don't load if there's no userId

        // Fetch the registered activities from localStorage if userId exists
        const storedRegisteredActivities = localStorage.getItem("registeredActivities");
        if (storedRegisteredActivities) {
            setRegisteredActivities(new Set(JSON.parse(storedRegisteredActivities)));
        }
    }, [userId]); // Re-run on userId change

    // Add activity to registered activities
    const registerActivity = (activityId: string) => {
        setRegisteredActivities((prev) => {
            const updatedSet = new Set(prev);
            updatedSet.add(activityId); // Add the activity ID to the set
            localStorage.setItem("registeredActivities", JSON.stringify(Array.from(updatedSet))); // Persist changes
            return updatedSet;
        });
    };

    // Remove activity from registered activities
    const unregisterActivity = (activityId: string) => {
        setRegisteredActivities((prev) => {
            const updatedSet = new Set(prev);
            updatedSet.delete(activityId); // Remove the activity ID from the set
            localStorage.setItem("registeredActivities", JSON.stringify(Array.from(updatedSet))); // Persist changes
            return updatedSet;
        });
    };

    return {
        registeredActivities,
        registerActivity,
        unregisterActivity
    };
};

export default useRegisteredActivities;
