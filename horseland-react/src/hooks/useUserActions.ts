// hooks/useUserActions.ts
import User from '../model/user.model.tsx';
import { UserService } from '../service/UserService.ts';

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
            alert('Failed to add user.');
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
            alert('Failed to update user.');
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