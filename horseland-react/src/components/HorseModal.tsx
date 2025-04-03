import React, { useState, useEffect } from 'react';
import { Horse, Breed } from "../model/horse.model.tsx"; // Assuming Role is also exported

interface HorseModalProps {
    isOpen: boolean;
    isUpdateMode: boolean;
    initialHorse: Horse;
    onClose: () => void;
    onAdd: (horse: Horse) => Promise<void>;
    onUpdate: (horse: Horse) => Promise<void>;
}

function HorseModal({ isOpen, isUpdateMode, initialHorse, onClose, onAdd, onUpdate }: HorseModalProps) {
    const [horse, setHorse] = useState<Horse>(initialHorse);

    useEffect(() => {
        setHorse(initialHorse);
    }, [initialHorse]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;

        // Check if it's a select element
        if (e.target instanceof HTMLSelectElement) {
            setHorse((prev) => ({
                ...prev,
                [name]: value as Breed, // Role is the type of 'role'
            }));
        } else {
            // Handle input and textarea fields
            setHorse((prev) => ({
                ...prev,
                [name]: name === 'birthDate' ? value : value, // Keep as string for simplicity
            }));
        }
    };

    const handleSubmit = async () => {
        if (isUpdateMode) {
            await onUpdate(horse);
        } else {
            await onAdd(horse);
        }
        onClose();
    };

    if (!isOpen) {
        return null;
    }

    // Create breed options dynamically from the Breed enum
    const breedOptions = Object.values(Breed).map((breed) => ({
        value: breed,
        label: breed.charAt(0) + breed.slice(1).toLowerCase() // Capitalize first letter for better presentation
    }));

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>{isUpdateMode ? 'Update Horse' : 'Add Horse'}</h2>
                {isUpdateMode && (
                    <input
                        type="text"
                        name="id"
                        placeholder="ID"
                        value={horse.id}
                        disabled
                    />
                )}

                {/* Name */}
                <input
                    type="text"
                    name="name"
                    placeholder="Name"
                    value={horse.name}
                    onChange={handleInputChange}
                />

                {/* Birth Date */}
                <input
                    type="date"
                    name="birthDate"
                    placeholder="Birth Date"
                    value={horse.birthDate instanceof Date ? horse.birthDate.toISOString().split("T")[0] : horse.birthDate} // Handle Date or string
                    onChange={handleInputChange}
                />

                {/* Breed */}
                <select
                    name="breed"
                    value={horse.breed}
                    onChange={handleInputChange}
                >
                    {breedOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>

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

export default HorseModal;
