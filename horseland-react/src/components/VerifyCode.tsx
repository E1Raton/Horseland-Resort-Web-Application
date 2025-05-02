import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';

interface LocationState {
    email: string;
}

const VerifyCode: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const state = location.state as LocationState;

    const [email, setEmail] = useState('');
    const [code, setCode] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');

    useEffect(() => {
        if (state?.email) {
            setEmail(state.email);
        } else {
            setError("Missing email. Please start the process again.");
        }
    }, [state]);

    const handleVerifyCode = async () => {
        setError('');
        setMessage('');

        try {
            const response = await axios.post('http://localhost:8080/auth/verify-code', {
                email,
                code
            });

            const resetToken = response.data?.resetToken;
            console.log('Reset token:', resetToken);

            if (resetToken) {
                setMessage('Code verified. Redirecting...');
                navigate('/reset-password', {
                    state: {
                        email: email,
                        token: resetToken } });
            } else {
                setError('Invalid server response.');
            }
        } catch (err: unknown) {
            if (axios.isAxiosError(err) && err.response) {
                const backendMessage =
                    err.response.data?.errorMessage || 'Verification failed. Please try again.';
                console.error('Verification error:', backendMessage);
                setError(backendMessage);
            } else {
                console.error('Unexpected error:', err);
                setError('An unexpected error occurred.');
            }
        }
    };

    return (
        <div className="app-container">
            <h2>Verify Code</h2>
            <p>Email: <strong>{email}</strong></p>

            <div className="form-container">
                <div className="form-group">
                    <label htmlFor="code">Verification Code</label>
                    <input
                        type="text"
                        id="code"
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        required
                    />
                </div>

                <button className="btn" onClick={handleVerifyCode}>
                    Verify
                </button>

                {error && <p className="error-message">{error}</p>}
                {message && <p className="success-message">{message}</p>}
            </div>

            <div className="button-group">
                <button className="btn" onClick={() => navigate('/')}>Go Back</button>
            </div>
        </div>
    );
};

export default VerifyCode;
