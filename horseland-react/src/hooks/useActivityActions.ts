import { Activity } from '../model/activity.model.ts';
import { ActivityService } from '../service/ActivityService.ts';
import React from 'react';

interface UseActivityActionsProps {
    setData: React.Dispatch<React.SetStateAction<Activity[]>>;
    setSelectedActivity: React.Dispatch<React.SetStateAction<Activity | null>>;
    selectedActivity: Activity | null;
}

const useActivityActions = ({ setData, setSelectedActivity, selectedActivity }: UseActivityActionsProps) => {
    const handleAddActivity = async (activity: Omit<Activity, 'id'>) => {
        try {
            const addedActivity = await ActivityService.createActivity(activity);
            setData(prevData => [...prevData, addedActivity]);
        } catch (error) {
            console.error('Error adding activity:', error);
            const errorMessage = error instanceof Error ? error.message : String(error);
            alert(errorMessage);
        }
    };

    const handleUpdateActivity = async (activity: Activity) => {
        if (!selectedActivity) return;
        try {
            console.log('Update activity:', activity);
            const updatedActivity = await ActivityService.updateActivity(selectedActivity.id, activity);
            console.log(updatedActivity);
            setData(prevData =>
                prevData.map(a => (a.id === selectedActivity.id ? updatedActivity : a))
            );
        } catch (error) {
            console.error('Error updating activity:', error);
            const errorMessage = error instanceof Error ? error.message : String(error);
            alert(errorMessage);
        }
    };

    const handleDeleteActivity = async () => {
        if (!selectedActivity) return;
        try {
            await ActivityService.deleteActivity(selectedActivity.id);
            setData(prevData => prevData.filter(a => a.id !== selectedActivity.id));
            setSelectedActivity(null);
        } catch (error) {
            console.error('Error deleting activity:', error);
            const errorMessage = error instanceof Error ? error.message : String(error);
            alert(errorMessage);
        }
    };

    return {
        handleAddActivity,
        handleUpdateActivity,
        handleDeleteActivity,
    };
};

export default useActivityActions;