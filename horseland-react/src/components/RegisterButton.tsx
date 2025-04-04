import React from "react";
import { FaRegTimesCircle } from "react-icons/fa";

interface RegisterButtonProps {
    activityId: string;
    isRegistered: boolean;
    onRegister: () => void;
    onUnregister: () => void;
}

const RegisterButton: React.FC<RegisterButtonProps> = ({
                                                           isRegistered,
                                                           onRegister,
                                                           onUnregister,
                                                       }) => {
    return isRegistered ? (
        <button className="unregister-btn" onClick={() => onUnregister()}>
            <FaRegTimesCircle /> Unregister
        </button>
    ) : (
        <button className="register-btn" onClick={() => onRegister()}>
            Register
        </button>
    );
};

export default RegisterButton;
