import React, { useState, useEffect } from 'react';
import { DARK_THEME, LIGHT_THEME } from '../constants/theme.ts';
import useActivityActions from '../hooks/useActivityActions.ts';
import useActivityModal from '../hooks/useActivityModal.ts';
import { ActivityService } from '../service/ActivityService.ts';
import ActivityTable from './ActivityTable.tsx';
import ActivityModal from './ActivityModal.tsx';
import { Activity } from '../model/activity.model.tsx';

const ActivityView: React.FC = () => {
    const [data, setData] = useState<Activity[]>([]);
    const [loading, setLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [selectedActivity, setSelectedActivity] = useState<Activity | null>(null);
    const [currentTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        setIsError(false);
        try {
            const activities = await ActivityService.fetchActivities();
            setData(activities);
        } catch (error) {
            console.error('Error fetching activities:', error);
            setIsError(true);
        } finally {
            setLoading(false);
        }
    };

    const { handleAddActivity, handleUpdateActivity, handleDeleteActivity } = useActivityActions({
        setData,
        selectedActivity,
        setSelectedActivity,
    });

    const { isModalOpen, isUpdateMode, newActivity, openModal, closeModal } = useActivityModal({ selectedActivity });

    const handleRowSelected = (state: { selectedRows: Activity[] }) => {
        setSelectedActivity(state.selectedRows[0] || null);
    };

    return (
        <div className="app-container">
            <div className="button-group">
                <button onClick={() => openModal()}>Add</button>
                <button onClick={() => openModal(true)} disabled={!selectedActivity}>Update</button>
                <button onClick={handleDeleteActivity} disabled={!selectedActivity}>Delete</button>
            </div>
            <ActivityTable
                data={data}
                loading={loading}
                isError={isError}
                onRowSelected={handleRowSelected}
                theme={currentTheme}
            />
            <ActivityModal
                isOpen={isModalOpen}
                isUpdateMode={isUpdateMode}
                initialActivity={newActivity}
                onClose={closeModal}
                onAdd={handleAddActivity}
                onUpdate={handleUpdateActivity}
            />
        </div>
    );
};

export default ActivityView;