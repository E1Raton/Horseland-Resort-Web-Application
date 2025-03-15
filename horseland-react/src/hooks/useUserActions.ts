// hooks/useUserActions.ts
import { User } from '../model/user.model.tsx';
import { UserService } from '../service/UserService.ts';
import React from "react";

interface UseUserActionsProps {
    setData: React.Dispatch<React.SetStateAction<User[]>>;
    setSelectedUser: React.Dispatch<React.SetStateAction<User | null>>;
    selectedUser: User | null;
}

const useUserActions = ({ setData, setSelectedUser, selectedUser }: UseUserActionsProps) => {
    const handleAddUser = async (user: User) => {
        try {
            const addedUser = await UserService.addUser(user);
            setData(prevData => [...prevData, addedUser]);
        } catch (error) {
            console.error('Error adding user:', error);
            const errorMessage = error instanceof Error ? error.message : error;
            alert(errorMessage);
        }
    };

    const handleUpdateUser = async (user: User) => {
        if (!selectedUser) return;
        try {
            await UserService.updateUser(user);
            setData(prevData =>
                prevData.map(u => (u.id === selectedUser.id ? user : u))
            );
        } catch (error) {
            console.error('Error updating user:', error);
            const errorMessage = error instanceof Error ? error.message : error;
            alert(errorMessage);
        }
    };

    const handleDeleteUser = async () => {
        if (!selectedUser) return;
        try {
            await UserService.deleteUser(selectedUser.id);
            setData(prevData => prevData.filter(user => user.id !== selectedUser.id));
            setSelectedUser(null);
        } catch (error) {
            console.error('Error deleting user:', error);
            alert('Failed to delete user.');
        }
    };

    return { handleAddUser, handleUpdateUser, handleDeleteUser };
};

export default useUserActions;