// hooks/useHorseActions.ts
import { Horse } from '../model/horse.model.tsx';
import { HorseService } from '../service/HorseService.ts';
import React from "react";

interface UseHorseActionsProps {
    setData: React.Dispatch<React.SetStateAction<Horse[]>>;
    setSelectedHorse: React.Dispatch<React.SetStateAction<Horse | null>>;
    selectedHorse: Horse | null;
}

const useHorseActions = ({ setData, setSelectedHorse, selectedHorse }: UseHorseActionsProps) => {
    const handleAddHorse = async (horse: Omit<Horse, 'id'>) => {
        const ownerId = sessionStorage.getItem("userId"); // Get userId from sessionStorage

        if (!ownerId) {
            alert("Error: User is not logged in or ownerId is missing.");
            console.error("Error: ownerId is missing.");
            return;
        }

        try {
            console.log(`Adding horse for ownerId: ${ownerId}`, horse); // Debugging log

            const addedHorse = await HorseService.addHorseToOwner(horse, ownerId);

            console.log("Horse added successfully:", addedHorse); // Success log

            setData(prevData => [...prevData, addedHorse]);
        } catch (error) {
            console.error("Error adding horse:", error);

            // More detailed error handling
            if (error instanceof Error) {
                alert(`Failed to add horse: ${error.message}`);
            } else {
                alert("An unexpected error occurred while adding the horse.");
            }
        }
    };

    const handleUpdateHorse = async (horse: Horse) => {
        if (!selectedHorse) return;
        try {
            await HorseService.updateHorse(horse);
            setData(prevData =>
                prevData.map(u => (u.id === selectedHorse.id ? horse : u))
            );
        } catch (error) {
            console.error('Error updating horse:', error);
            const errorMessage = error instanceof Error ? error.message : error;
            alert(errorMessage);
        }
    };

    const handleDeleteHorse = async () => {
        if (!selectedHorse) return;
        try {
            await HorseService.deleteHorse(selectedHorse.id);
            setData(prevData => prevData.filter(horse => horse.id !== selectedHorse.id));
            setSelectedHorse(null);
        } catch (error) {
            console.error('Error deleting horse:', error);
            alert('Failed to delete horse.');
        }
    };

    return { handleAddHorse, handleUpdateHorse, handleDeleteHorse };
};

export default useHorseActions;