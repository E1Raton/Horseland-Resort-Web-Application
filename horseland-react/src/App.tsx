// App.tsx
import { useState, useEffect } from 'react';
import './App.css';
import { User }from "./model/user.model.tsx";
import ThemeSwitcher from "./components/ThemeSwitcher.tsx";
import UserTable from "./components/UserTable.tsx";
import { UserService } from "./service/UserService.ts";
import UserModal from "./components/UserModal.tsx";
import useUserActions from "./hooks/useUserActions.ts";
import useUserModal from "./hooks/useUserModal.ts";
import {DARK_THEME, LIGHT_THEME} from "./constants/theme.ts";

function App() {
    const [data, setData] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [currentTheme, setCurrentTheme] =
        useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);

    useEffect(() => {
        fetchData();
    }, []);

    const { handleAddUser, handleUpdateUser, handleDeleteUser } =
        useUserActions({
            setData,
            setSelectedUser,
            selectedUser,
        });

    const {
        isModalOpen,
        isUpdateMode,
        newUser,
        openModal,
        closeModal,
    } = useUserModal({ selectedUser });

    const fetchData = async () => {
        setLoading(true);
        setIsError(false);
        try {
            const users = await UserService.getUsers();
            setData(users);
            setLoading(false);
        } catch (error) {
            console.error('Error fetching data:', error);
            setLoading(false);
            setIsError(true);
        }
    };

    const handleRowSelected = (state: { selectedRows: User[] }) => {
        setSelectedUser(state.selectedRows[0] || null);
    };

    const handleThemeChange = (newTheme: 'light' | 'dark') => {
        setCurrentTheme(newTheme);
    };

    return (
        <div className="app-container">
            <h1>User List</h1>
            <ThemeSwitcher onThemeChange={handleThemeChange} />
            <div className="button-group">
                <button onClick={() => openModal()}>Add</button>
                <button onClick={() => openModal(true)} disabled={!selectedUser}>Update</button>
                <button onClick={handleDeleteUser} disabled={!selectedUser}>Delete</button>
            </div>
            <br />
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
}

export default App;