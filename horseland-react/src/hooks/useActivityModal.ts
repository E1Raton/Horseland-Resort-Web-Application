// hooks/useActivityModal.ts
import { useState } from 'react';
import { Activity } from '../model/activity.model.tsx';

interface UseActivityModalProps {
    selectedActivity: Activity | null;
}

const useActivityModal = ({ selectedActivity }: UseActivityModalProps) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateMode, setIsUpdateMode] = useState(false);
    const [newActivity, setNewActivity] = useState<Activity>({ id: '', name: '', description: '', startDate: '', endDate: '', participants: [] });

    const openModal = (update = false) => {
        setIsModalOpen(true);
        setIsUpdateMode(update);
        if (update && selectedActivity) {
            setNewActivity({ ...selectedActivity });
        } else {
            setNewActivity({ id: '', name: '', description: '', startDate: '', endDate: '', participants: [] });
        }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setNewActivity({ id: '', name: '', description: '', startDate: '', endDate: '', participants: [] });
        setIsUpdateMode(false);
    };

    return {
        isModalOpen,
        isUpdateMode,
        newActivity,
        openModal,
        closeModal,
        setNewActivity,
    };
};

export default useActivityModal;