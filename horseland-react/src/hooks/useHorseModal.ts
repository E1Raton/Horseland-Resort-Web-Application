import { useState } from "react";
import { Breed, Horse } from "../model/horse.model.tsx";

interface UseHorseModalProps {
    selectedHorse: Horse | null;
}

const useHorseModal = ({ selectedHorse }: UseHorseModalProps) => {
    // Retrieve the logged-in user's ID from sessionStorage
    const userId = sessionStorage.getItem("userId") || "";

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateMode, setIsUpdateMode] = useState(false);
    const [newHorse, setNewHorse] = useState<Horse>({
        id: "",
        name: "",
        birthDate: "",
        breed: Breed.AXIOS,
        ownerId: userId, // Automatically assign the logged-in user's ID
    });

    const openModal = (update = false) => {
        setIsModalOpen(true);
        setIsUpdateMode(update);
        if (update && selectedHorse) {
            setNewHorse({
                ...selectedHorse,
                ownerId: selectedHorse.ownerId || userId, // Ensure ownerId is always set
            });
        } else {
            setNewHorse({
                id: "",
                name: "",
                birthDate: "",
                breed: Breed.AXIOS,
                ownerId: userId, // Set ownerId when creating a new horse
            });
        }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setNewHorse({
            id: "",
            name: "",
            birthDate: "",
            breed: Breed.AXIOS,
            ownerId: userId,
        });
        setIsUpdateMode(false);
    };

    return {
        isModalOpen,
        isUpdateMode,
        newHorse,
        openModal,
        closeModal,
        setNewHorse,
    };
};

export default useHorseModal;