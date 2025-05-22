import React, { useState, useEffect } from 'react';
import { DARK_THEME, LIGHT_THEME } from '../constants/theme.ts';
import useUserActions from '../hooks/useUserActions.ts';
import useUserModal from '../hooks/useUserModal.ts';
import { UserService } from '../service/UserService.ts';
import UserTable from './UserTable.tsx';
import UserModal from './UserModal.tsx';
import { User } from '../model/user.model.tsx';

const UserView: React.FC = () => {
    const [data, setData] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [currentTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        setIsError(false);
        try {
            const users = await UserService.getUsers();
            setData(users);
        } catch (error) {
            console.error('Error fetching users:', error);
            setIsError(true);
        } finally {
            setLoading(false);
        }
    };

    const { handleAddUser, handleUpdateUser, handleDeleteUser } = useUserActions({
        setData,
        selectedUser,
        setSelectedUser,
    });

    const { isModalOpen, isUpdateMode, newUser, openModal, closeModal } = useUserModal({ selectedUser });

    const handleRowSelected = (state: { selectedRows: User[] }) => {
        setSelectedUser(state.selectedRows[0] || null);
    };

    return (
        <div className="app-container">
            <div className="button-group">
                <button onClick={() => openModal()}>Add</button>
                <button onClick={() => openModal(true)} disabled={!selectedUser}>Update</button>
                <button onClick={handleDeleteUser} disabled={!selectedUser}>Delete</button>
            </div>
            <UserTable
                data={data}
                loading={loading}
                isError={isError}
                onRowSelected={handleRowSelected}
                theme={currentTheme}
            />
            <UserModal
                isOpen={isModalOpen}
                isUpdateMode={isUpdateMode}
                initialUser={newUser}
                onClose={closeModal}
                onAdd={handleAddUser}
                onUpdate={handleUpdateUser}
            />
        </div>
    );
};

export default UserView;