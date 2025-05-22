import React, { useState, useEffect } from 'react';
import { User, Role } from "../model/user.model.tsx";

interface UserModalProps {
    isOpen: boolean;
    isUpdateMode: boolean;
    initialUser: User;
    onClose: () => void;
    onAdd: (user: User) => Promise<void>;
    onUpdate: (user: User) => Promise<void>;
}

function UserModal({ isOpen, isUpdateMode, initialUser, onClose, onAdd, onUpdate }: UserModalProps) {
    const [user, setUser] = useState<User>(initialUser);

    useEffect(() => {
        setUser(initialUser);
    }, [initialUser, isUpdateMode]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;

        if (e.target instanceof HTMLSelectElement) {
            setUser((prev) => ({
                ...prev,
                [name]: value as Role,
            }));
        } else {
            setUser((prev) => ({
                ...prev,
                [name]: name === 'birthDate' ? value : value,
            }));
        }
    };

    const handleSubmit = async () => {
        if (isUpdateMode) {
            await onUpdate(user);
        } else {
            await onAdd(user);
        }
        onClose();
    };

    if (!isOpen) {
        return null;
    }

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>{isUpdateMode ? 'Update User' : 'Add User'}</h2>
                {isUpdateMode && (
                    <input
                        type="text"
                        name="id"
                        placeholder="ID"
                        value={user.id}
                        disabled
                    />
                )}

                <input
                    type="text"
                    name="firstName"
                    placeholder="First Name"
                    value={user.firstName}
                    onChange={handleInputChange}
                />

                <input
                    type="text"
                    name="lastName"
                    placeholder="Last Name"
                    value={user.lastName}
                    onChange={handleInputChange}
                />

                <input
                    type="date"
                    name="birthDate"
                    placeholder="Birth Date"
                    value={user.birthDate instanceof Date ? user.birthDate.toISOString().split("T")[0] : user.birthDate}
                    onChange={handleInputChange}
                />

                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={user.username}
                    onChange={handleInputChange}
                />

                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={user.email}
                    onChange={handleInputChange}
                />

                {/* Password only shown in add mode */}
                {!isUpdateMode && (
                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={user.password}
                        onChange={handleInputChange}
                    />
                )}

                <select
                    name="role"
                    value={user.role}
                    onChange={handleInputChange}
                >
                    <option value={Role.ADMIN}>Admin</option>
                    <option value={Role.STUDENT}>Student</option>
                    <option value={Role.INSTRUCTOR}>Instructor</option>
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

export default UserModal;