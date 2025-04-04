import {ACTIVITY_ENDPOINT} from "../constants/api.ts";

export class ActivityService {
    static async fetchActivities() {
        try {
            const response = await fetch(`${ACTIVITY_ENDPOINT}`);
            if (response.ok) {
                return await response.json();
            } else {
                console.error("Failed to fetch activities");
                return [];
            }
        } catch (error) {
            console.error("Error fetching activities:", error);
            return [];
        }
    }

    static async fetchFutureActivities() {
        try {
            const response = await fetch(`${ACTIVITY_ENDPOINT}/future`);
            if (response.ok) {
                return await response.json();
            } else {
                console.error("Failed to fetch future activities");
                return [];
            }
        } catch (error) {
            console.error("Error fetching future activities:", error);
            return [];
        }
    }

    static async registerActivity(activityId: string, userId: string) {
        try {
            const response = await fetch(`${ACTIVITY_ENDPOINT}/${activityId}/register/${userId}`, {
                method: "PUT",
            });
            if (response.ok) {
                return true;
            } else {
                console.error("Failed to register for the activity");
                return false;
            }
        } catch (error) {
            console.error("Error registering for activity:", error);
            return false;
        }
    }

    static async unregisterActivity(activityId: string, userId: string) {
        try {
            const response = await fetch(`${ACTIVITY_ENDPOINT}/${activityId}/deregister/${userId}`, {
                method: "PUT",
            });
            if (response.ok) {
                return true;
            } else {
                console.error("Failed to unregister from the activity");
                return false;
            }
        } catch (error) {
            console.error("Error unregistering from activity:", error);
            return false;
        }
    }
}
