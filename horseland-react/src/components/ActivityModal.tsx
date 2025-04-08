import React, { useState, useEffect } from 'react';
import { Activity } from "../model/activity.model.tsx"; // Assuming Role is also exported

interface ActivityModalProps {
    isOpen: boolean;
    isUpdateMode: boolean;
    initialActivity: Activity;
    onClose: () => void;
    onAdd: (Activity: Activity) => Promise<void>;
    onUpdate: (activity: Activity) => Promise<void>;
}

function ActivityModal({ isOpen, isUpdateMode, initialActivity, onClose, onAdd, onUpdate }: ActivityModalProps) {
    const [activity, setActivity] = useState<Activity>(initialActivity);

    useEffect(() => {
        setActivity(initialActivity);
    }, [initialActivity]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;

        // Handle input and textarea fields
        setActivity((prev) => ({
            ...prev,
            [name]: name === 'startDate' || name === 'endDate' ? value : value, // Keep as string for simplicity
        }));
    };

    const handleSubmit = async () => {
        if (isUpdateMode) {
            await onUpdate(activity);
        } else {
            await onAdd(activity);
        }
        onClose();
    };

    if (!isOpen) {
        return null;
    }

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>{isUpdateMode ? 'Update Activity' : 'Add Activity'}</h2>
                {isUpdateMode && (
                    <input
                        type="text"
                        name="id"
                        placeholder="ID"
                        value={activity.id}
                        disabled
                    />
                )}

                {/* Name */}
                <input
                    type="text"
                    name="name"
                    placeholder="Name"
                    value={activity.name}
                    onChange={handleInputChange}
                />

                {/* Description */}
                <input
                    type="text"
                    name="description"
                    placeholder="Description"
                    value={activity.description}
                    onChange={handleInputChange}
                />

                {/* Start Date */}
                <input
                    type="date"
                    name="startDate"
                    placeholder="Birth Date"
                    value={activity.startDate}
                    onChange={handleInputChange}
                />

                {/* Start Date */}
                <input
                    type="date"
                    name="endDate"
                    placeholder="End Date"
                    value={activity.endDate}
                    onChange={handleInputChange}
                />

                <div className="modal-buttons">
                    <button onClick={handleSubmit}>
                        {isUpdateMode ? 'Update' : 'Add'}
                    </button>
                    <button onClick={onClose}>Cancel</button>
                </div>
            </div>
        </div>
    );
}

export default ActivityModal;
