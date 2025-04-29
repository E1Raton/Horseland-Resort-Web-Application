import { useState, useEffect } from 'react';
import '../App.css';
import { User } from "../model/user.model.tsx";
import { Activity } from "../model/activity.model.tsx";
import ThemeSwitcher from "./ThemeSwitcher.tsx";
import UserTable from "./UserTable.tsx";
import ActivityTable from "./ActivityTable.tsx";
import { UserService } from "../service/UserService.ts";
import { ActivityService } from "../service/ActivityService.ts"; // You need to create this if it doesn't exist
import UserModal from "./UserModal.tsx";
import ActivityModal from "./ActivityModal.tsx";
import useUserActions from "../hooks/useUserActions.ts";
import useUserModal from "../hooks/useUserModal.ts";
import useActivityModal from "../hooks/useActivityModal.ts"; // You need to create this hook similar to useUserModal
import useActivityActions from "../hooks/useActivityActions.ts"; // Create similar to useUserActions
import { DARK_THEME, LIGHT_THEME } from "../constants/theme.ts";
import { useNavigate } from "react-router-dom";

const AdminPanel = () => {
    // Theme and Auth
    const [currentTheme, setCurrentTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);
    const navigate = useNavigate();

    // View Mode Toggle (Users or Activities)
    const [view, setView] = useState<'users' | 'activities'>('users');

    // User State
    const [users, setUsers] = useState<User[]>([]);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [userLoading, setUserLoading] = useState(true);
    const [userError, setUserError] = useState(false);

    const { handleAddUser, handleUpdateUser, handleDeleteUser } = useUserActions({
        setData: setUsers,
        selectedUser,
        setSelectedUser,
    });

    const { isModalOpen: isUserModalOpen, isUpdateMode: isUserUpdateMode, newUser, openModal: openUserModal, closeModal: closeUserModal } =
        useUserModal({ selectedUser });

    // Activity State
    const [activities, setActivities] = useState<Activity[]>([]);
    const [selectedActivity, setSelectedActivity] = useState<Activity | null>(null);
    const [activityLoading, setActivityLoading] = useState(true);
    const [activityError, setActivityError] = useState(false);

    const { handleAddActivity, handleUpdateActivity, handleDeleteActivity } = useActivityActions({
        setData: setActivities,
        selectedActivity,
        setSelectedActivity,
    });

    const { isModalOpen: isActivityModalOpen, isUpdateMode: isActivityUpdateMode, newActivity, openModal: openActivityModal, closeModal: closeActivityModal } =
        useActivityModal({ selectedActivity });

    // Fetching Data
    useEffect(() => {
        fetchUsers();
        fetchActivities();
    }, []);

    const fetchUsers = async () => {
        setUserLoading(true);
        setUserError(false);
        try {
            const result = await UserService.getUsers();
            setUsers(result);
        } catch (err) {
            console.error('Failed to fetch users:', err);
            setUserError(true);
        } finally {
            setUserLoading(false);
        }
    };

    const fetchActivities = async () => {
        setActivityLoading(true);
        setActivityError(false);
        try {
            const result = await ActivityService.fetchActivities();
            setActivities(result);
        } catch (err) {
            console.error('Failed to fetch users:', err);
            setUserError(true);
        }
        finally {
            setActivityLoading(false);
        }
    };

    const handleThemeChange = (newTheme: 'light' | 'dark') => {
        setCurrentTheme(newTheme);
    };

    const handleLogout = () => {
        sessionStorage.removeItem('authToken');
        sessionStorage.removeItem("role");
        sessionStorage.removeItem('userId');
        sessionStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div className="app-container">
            <h1>Admin Panel</h1>
            <ThemeSwitcher onThemeChange={handleThemeChange} />
            <button onClick={handleLogout} className="logout-button">Logout</button>

            <div className="toggle-buttons">
                <button onClick={() => setView('users')} className={view === 'users' ? 'active' : ''}>Users</button>
                <button onClick={() => setView('activities')} className={view === 'activities' ? 'active' : ''}>Activities</button>
            </div>

            <div className="button-group">
                <button onClick={() => view === 'users' ? openUserModal() : openActivityModal()}>Add</button>
                <button
                    onClick={() => view === 'users' ? openUserModal(true) : openActivityModal(true)}
                    disabled={view === 'users' ? !selectedUser : !selectedActivity}
                >
                    Update
                </button>
                <button
                    onClick={() => view === 'users' ? handleDeleteUser() : handleDeleteActivity()}
                    disabled={view === 'users' ? !selectedUser : !selectedActivity}
                >
                    Delete
                </button>
            </div>

            {view === 'users' ? (
                <>
                    <UserTable
                        data={users}
                        loading={userLoading}
                        isError={userError}
                        onRowSelected={(state) => setSelectedUser(state.selectedRows[0] || null)}
                        theme={currentTheme}
                    />
                    <UserModal
                        isOpen={isUserModalOpen}
                        isUpdateMode={isUserUpdateMode}
                        initialUser={newUser}
                        onClose={closeUserModal}
                        onAdd={handleAddUser}
                        onUpdate={handleUpdateUser}
                    />
                </>
            ) : (
                <>
                    <ActivityTable
                        data={activities}
                        loading={activityLoading}
                        isError={activityError}
                        onRowSelected={(state) => setSelectedActivity(state.selectedRows[0] || null)}
                        theme={currentTheme}
                    />
                    <ActivityModal
                        isOpen={isActivityModalOpen}
                        isUpdateMode={isActivityUpdateMode}
                        initialActivity={newActivity}
                        onClose={closeActivityModal}
                        onAdd={handleAddActivity}
                        onUpdate={handleUpdateActivity}
                    />
                </>
            )}
        </div>
    );
};

export default AdminPanel;