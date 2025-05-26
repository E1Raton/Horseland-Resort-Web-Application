import {ACTIVITY_ENDPOINT} from "../constants/api.ts";
import {Activity} from "../model/activity.model.tsx";
import {fetchWithAuth} from "../utils/fetchWithAuth.ts";

export class ActivityService {
    static async fetchActivities() {
        try {

            const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}`, {
                method: 'GET'
            });

            if (response.ok) {
                return await response.json();
            } else {
                const errorText = await response.text();
                console.error(`Failed to fetch activities: ${response.status} - ${errorText}`);
                return [];
            }
        } catch (error) {
            console.error("Network or fetch error:", error);
            return [];
        }
    }


    static async fetchActivitiesByUserId(userId: string) {
        try {

            const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}/participant/${userId}`, {
                method: 'GET'
            });
            if (response.ok) {
                return await response.json();
            } else {
                console.error("Failed to fetch activities by user id");
                return [];
            }
        } catch (error) {
            console.error("Error fetching activities:", error);
            return [];
        }
    }

    static async createActivity(activity: Omit<Activity, 'id'>): Promise<Activity> {

        const response = await fetchWithAuth(ACTIVITY_ENDPOINT, {
            method: 'POST',
            body: JSON.stringify(activity),
        });

        console.log('Create Activity Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to create activity';

            try {
                const errorResponse = await response.json();
                console.error('Create activity error response:', errorResponse);

                errorMessage = errorResponse?.message || 'Unknown error occurred';

                if (errorResponse?.errors && typeof errorResponse.errors === 'object') {
                    const validationMessages = Object.entries(errorResponse.errors)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join(', ');
                    errorMessage += ` - Details: ${validationMessages}`;
                }
            } catch (error) {
                console.error('Error parsing create activity error response:', error);
            }

            throw new Error(errorMessage);
        }

        return await response.json();
    }

    static async updateActivity(activityId: string, updatedData: Partial<Activity>): Promise<Activity> {

        const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}/${activityId}`, {
            method: 'PUT',
            body: JSON.stringify(updatedData),
        });

        console.log('Update Activity Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to update activity';

            try {
                const errorResponse = await response.json();
                console.error('Update activity error response:', errorResponse);

                errorMessage = errorResponse?.message || 'Unknown error occurred';

                if (errorResponse?.errors && typeof errorResponse.errors === 'object') {
                    const validationMessages = Object.entries(errorResponse.errors)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join(', ');
                    errorMessage += ` - Details: ${validationMessages}`;
                }
            } catch (error) {
                console.error('Error parsing update activity error response:', error);
            }

            throw new Error(errorMessage);
        }

        return await response.json();
    }

    static async deleteActivity(activityId: string): Promise<boolean> {

        const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}/${activityId}`, {
            method: 'DELETE',
        });

        console.log('Delete Activity Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to delete activity';

            try {
                const errorResponse = await response.json();
                console.error('Delete activity error response:', errorResponse);

                errorMessage = errorResponse?.message || 'Unknown error occurred';

                if (errorResponse?.errors && typeof errorResponse.errors === 'object') {
                    const validationMessages = Object.entries(errorResponse.errors)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join(', ');
                    errorMessage += ` - Details: ${validationMessages}`;
                }
            } catch (error) {
                console.error('Error parsing delete activity error response:', error);
            }

            throw new Error(errorMessage);
        }

        return true;
    }

    static async fetchFutureActivities() {

        try {
            const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}/future`, {
                method: 'GET'
            });
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
        const token = sessionStorage.getItem('token');

        try {
            const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}/${activityId}/register/${userId}`, {
                method: "PUT",
                headers: {
                    'Authorization': `Bearer ${token}`,
                }
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
            const response = await fetchWithAuth(`${ACTIVITY_ENDPOINT}/${activityId}/deregister/${userId}`, {
                method: "PUT"
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
