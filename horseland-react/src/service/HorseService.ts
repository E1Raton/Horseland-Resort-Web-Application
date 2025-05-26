import { Horse } from '../model/horse.model.tsx';
import {HORSE_ENDPOINT} from '../constants/api';
import {fetchWithAuth} from "../utils/fetchWithAuth.ts";

export class HorseService {
    static async getHorses(ownerId: string): Promise<Horse[]> {

        const response = await fetchWithAuth(`${HORSE_ENDPOINT}/owner/${ownerId}`, {
            method: 'GET'
        });
        if (!response.ok) {
            throw new Error('Failed to fetch horses');
        }
        return response.json();
    }

    static async getHorsesByOwnerId(ownerId: string): Promise<Horse[]> {

        const response = await fetchWithAuth(`${HORSE_ENDPOINT}/owner/${ownerId}`, {
            method: 'GET'
        });
        if (!response.ok) {
            throw new Error('Failed to fetch horses for the user');
        }
        return response.json();
    }

    static async addHorse(horse: Omit<Horse, 'id'>): Promise<Horse> {

        const response = await fetchWithAuth(HORSE_ENDPOINT, {
            method: 'POST',
            body: JSON.stringify(horse),
        });

        console.log('Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to add horse';

            try {
                const errorResponse = await response.json();
                console.error('Failed to add horse:', errorResponse);

                // Extract main message
                errorMessage = errorResponse?.message || 'Unknown error occurred';

                // Extract validation errors (if present)
                if (errorResponse?.errors && typeof errorResponse.errors === 'object') {
                    const validationMessages = Object.entries(errorResponse.errors)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join(', ');

                    errorMessage += ` - Details: ${validationMessages}`;
                }
            } catch (error) {
                console.error('Error parsing error response:', error);
            }

            throw new Error(errorMessage);
        }

        const responseBody = await response.json();
        console.log('Response Body:', responseBody);

        return responseBody;
    }

    static async addHorseToOwner(horse: Omit<Horse, 'id'>, ownerId: string): Promise<Horse> {
        const url = `${HORSE_ENDPOINT}/owner/${ownerId}`;

        // Ensure ownerId is part of the horse object
        const horseWithOwnerId = { ...horse, ownerId };

        try {
            console.log("Sending POST request to:", url);
            console.log("Request Body:", JSON.stringify(horseWithOwnerId));

            const response = await fetchWithAuth(url, {
                method: 'POST', // Confirm your backend expects POST here
                body: JSON.stringify(horseWithOwnerId),
            });

            console.log("Response Status:", response.status);

            if (!response.ok) {
                let errorMessage = 'Failed to add horse to owner';

                try {
                    const errorResponse = await response.json();
                    console.error('Failed to add horse:', errorResponse);
                    errorMessage = errorResponse?.message || 'Unknown error occurred';

                    if (errorResponse?.errors) {
                        const validationErrors = Object.entries(errorResponse.errors)
                            .map(([field, msg]) => `${field}: ${msg}`)
                            .join(', ');
                        errorMessage += ` - Details: ${validationErrors}`;
                    }
                } catch (error) {
                    console.error('Error parsing error response:', error);
                }

                throw new Error(errorMessage);
            }

            const responseBody = await response.json();
            console.log('Horse added successfully:', responseBody);

            return responseBody;
        } catch (error) {
            console.error("Unexpected error in addHorseToOwner:", error);
            throw new Error("An unexpected error occurred while adding the horse to the owner.");
        }
    }

    static async updateHorse(horse: Horse): Promise<void> {
        // Check the horse object to ensure ownerId is present
        console.log('Horse object before sending update:', horse);

        if (!horse.ownerId) {
            console.error('Owner ID is missing in horse object');
            throw new Error('Owner ID is required');
        }

        const updatedHorse = {
            ...horse,         // Spread existing horse properties
            ownerId: horse.ownerId, // Ensure ownerId is correctly passed
        };

        console.log('Sending update request:', updatedHorse);  // Debug log

        const response = await fetchWithAuth(`${HORSE_ENDPOINT}/${horse.id}`, {
            method: 'PUT',
            body: JSON.stringify(updatedHorse),  // Send the horse object as payload
        });

        console.log('Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to update horse';

            try {
                const errorResponse = await response.json();
                console.error('Failed to update horse:', errorResponse);

                errorMessage = errorResponse?.message || 'Unknown error occurred';

                if (errorResponse?.errors && typeof errorResponse.errors === 'object') {
                    const validationMessages = Object.entries(errorResponse.errors)
                        .map(([field, message]) => `${field}: ${message}`)
                        .join(', ');

                    errorMessage += ` - Validation Details: ${validationMessages}`;
                }
            } catch (error) {
                console.error('Error parsing error response:', error);
                errorMessage = 'Error occurred while processing error response';
            }

            throw new Error(errorMessage);
        }
    }

    static async deleteHorse(id: string): Promise<void> {
        const response = await fetchWithAuth(`${HORSE_ENDPOINT}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error('Failed to delete horse');
        }
    }
}
