// services/UserService.ts
import { User } from '../model/user.model.tsx';
import { USER_ENDPOINT } from '../constants/api';

export class UserService {
    static async getUsers(): Promise<User[]> {
        const response = await fetch(USER_ENDPOINT);
        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }
        return response.json();
    }

    static async addUser(user: Omit<User, 'id'>): Promise<User> {
        const response = await fetch(USER_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        });

        console.log('Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to add user';

            try {
                const errorResponse = await response.json();
                console.error('Failed to add user:', errorResponse);

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

    static async updateUser(user: User): Promise<void> {
        const response = await fetch(`${USER_ENDPOINT}/${user.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        });

        console.log('Response Status:', response.status);

        if (!response.ok) {
            let errorMessage = 'Failed to update user';

            try {
                const errorResponse = await response.json();
                console.error('Failed to update user:', errorResponse);

                // Use the message from the error response, or a default message
                errorMessage = errorResponse?.message || 'Unknown error occurred';

                // Handling validation errors if present
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

            // Throwing the error with the final errorMessage
            throw new Error(errorMessage);
        }
    }

    static async deleteUser(id: string): Promise<void> {
        const response = await fetch(`${USER_ENDPOINT}/${id}`, {
            method: 'DELETE',
        });
        if (!response.ok) {
            throw new Error('Failed to delete user');
        }
    }
}