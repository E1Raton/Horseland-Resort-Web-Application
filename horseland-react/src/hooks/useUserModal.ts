// hooks/useUserModal.ts
import { useState } from 'react';
import { User, Role } from '../model/user.model.tsx';

interface UseUserModalProps {
    selectedUser: User | null;
}

const useUserModal = ({ selectedUser }: UseUserModalProps) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isUpdateMode, setIsUpdateMode] = useState(false);
    const [newUser, setNewUser] = useState<User>({ id: '', firstName: '', lastName: '', birthDate: '', username: '', email: '', password: '', role: Role.STUDENT });

    const openModal = (update = false) => {
        setIsModalOpen(true);
        setIsUpdateMode(update);
        if (update && selectedUser) {
            setNewUser({ ...selectedUser });
        } else {
            setNewUser({ id: '', firstName: '', lastName: '', birthDate: '', username: '', email: '', password: '', role: Role.STUDENT });
        }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setNewUser({ id: '', firstName: '', lastName: '', birthDate: '', username: '', email: '', password: '', role: Role.STUDENT });
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