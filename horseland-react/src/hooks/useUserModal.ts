// hooks/useUserModal.ts
import { useState } from 'react';
import User from '../model/user.model.tsx';

interface UseUserModalProps {
    selectedUser: User | null;
}

const useUserModal = ({ selectedUser }: UseUserModalProps) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateMode, setIsUpdateMode] = useState(false);
    const [newUser, setNewUser] = useState<User>({ id: '', firstName: '', lastName: '', birthDate: '', username: '', email: '', password: '', role: '' });

    const openModal = (update = false) => {
        setIsModalOpen(true);
        setIsUpdateMode(update);
        if (update && selectedUser) {
            setNewUser({ ...selectedUser });
        } else {
            setNewUser({ id: '', firstName: '', lastName: '', birthDate: '', username: '', email: '', password: '', role: '' });
        }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setNewUser({ id: '', firstName: '', lastName: '', birthDate: '', username: '', email: '', password: '', role: '' });
        setIsUpdateMode(false);
    };

    return {
        isModalOpen,
        isUpdateMode,
        newUser,
        openModal,
        closeModal,
        setNewUser,
    };
};

export default useUserModal;