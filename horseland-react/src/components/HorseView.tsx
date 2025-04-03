import { Horse } from "../model/horse.model.tsx";
import { useEffect, useState } from "react";
import { DARK_THEME, LIGHT_THEME } from "../constants/theme.ts";
import useHorseActions from "../hooks/useHorseActions.ts";
import useHorseModal from "../hooks/useHorseModal.ts";
import { HorseService } from "../service/HorseService.ts";
import HorseTable from "./HorseTable.tsx";
import HorseModal from "./HorseModal.tsx";
import { useNavigate } from "react-router-dom"; // For navigation after login

const HorseView: React.FC = () => {
    const [data, setData] = useState<Horse[]>([]);
    const [loading, setLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [selectedHorse, setSelectedHorse] = useState<Horse | null>(null);
    const [currentTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);

    const navigate = useNavigate(); // For redirection to login if not logged in

    // Retrieve the logged-in user's ID from sessionStorage (or localStorage)
    const userId = sessionStorage.getItem("userId"); // Assuming userId is stored after login
    console.log("userId:", userId);  // Log to check if it's correctly retrieved

    useEffect(() => {
        if (userId) {
            fetchData(userId); // Fetch horses based on the logged-in user's ownerId
        } else {
            setIsError(true);
            setLoading(false);
            console.error("User is not logged in or userId is not available.");
            // Redirect to login if user is not logged in
            navigate('/login');
        }
    }, [userId, navigate]);

    const { handleAddHorse, handleUpdateHorse, handleDeleteHorse } = useHorseActions({
        setData,
        setSelectedHorse,
        selectedHorse,
    });

    const { isModalOpen, isUpdateMode, newHorse, openModal, closeModal } = useHorseModal({ selectedHorse });

    const fetchData = async (ownerId: string) => {
        setLoading(true);
        setIsError(false);
        try {
            // Fetch horses by ownerId
            const horses = await HorseService.getHorsesByOwnerId(ownerId); // Updated to fetch by ownerId
            setData(horses);
            setLoading(false);
        } catch (error) {
            console.error("Error fetching data:", error);
            setLoading(false);
            setIsError(true);
        }
    };

    const handleRowSelected = (state: { selectedRows: Horse[] }) => {
        setSelectedHorse(state.selectedRows[0] || null);
    };

    return (
        <div className="app-container">
            <div className="button-group">
                <button onClick={() => openModal()}>Add</button>
                <button onClick={() => openModal(true)} disabled={!selectedHorse}>Update</button>
                <button onClick={handleDeleteHorse} disabled={!selectedHorse}>Delete</button>
            </div>
            <br />
            <HorseTable
                data={data}
                loading={loading}
                isError={isError}
                onRowSelected={handleRowSelected}
                theme={currentTheme}
            />
            <HorseModal
                isOpen={isModalOpen}
                isUpdateMode={isUpdateMode}
                initialHorse={newHorse}
                onClose={closeModal}
                onAdd={handleAddHorse}
                onUpdate={handleUpdateHorse}
            />
        </div>
    );
};

export default HorseView;