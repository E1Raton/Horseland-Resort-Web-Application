// services/UserService.ts
import User from '../model/user.model.tsx';
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
        if (!response.ok) {
            throw new Error('Failed to add user');
        }
        return response.json();
    }

    static async updateUser(user: User): Promise<void> {
        const response = await fetch(`${USER_ENDPOINT}/${user.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        });
        if (!response.ok) {
            throw new Error('Failed to update user');
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