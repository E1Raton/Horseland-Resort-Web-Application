import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import {AUTH_ENDPOINT} from "../constants/api.ts";

const ForgotPassword: React.FC = () => {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const sendVerificationCode = async () => {
        console.log('Sending code to email:', email);

        setMessage('');
        setError('');

        try {
            const payload = JSON.stringify({ email });
            console.log('Payload:', payload);

            const response = await axios.post(
                `${AUTH_ENDPOINT}/request-reset`,
                payload,
                { headers: { 'Content-Type': 'application/json' } }
            );

            if (response.status === 200 && response.data.success) {
                setMessage('Verification code sent to your email.');
                navigate('/verify-code', { state: { email }});
            } else {
                // Fallback in case a 200 comes back with success = false
                setError(response.data?.errorMessage || 'Unexpected error.');
            }

        } catch (err) {
            if (axios.isAxiosError(err) && err.response) {
                const backendMessage =
                    err.response.data?.errorMessage || 'Failed to send reset link.';
                console.error('Backend error message:', backendMessage);
                setError(backendMessage);
            } else {
                console.error('Unexpected error:', err);
                setError('An unexpected error occurred. Please try again.');
            }
        }
    };

    return (
        <div className="app-container">
            <h1>Forgot Password</h1>

            <div className="form-container">
                <div className="form-group">
                    <label htmlFor="email">Enter your email address:</label>
                    <input
                        type="email"
                        id="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                {/* 2) Button now calls sendVerificationCode directly */}
                <button type="button" className="btn" onClick={sendVerificationCode}>
                    Send Verification Code
                </button>
            </div>

            {message && <p className="success-message">{message}</p>}
            {error && <p className="error-message">{error}</p>}

            {/* Go Back */}
            <div className="button-group">
                <button type="button" className="btn" onClick={() => navigate('/')}>
                    Go Back
                </button>
            </div>
        </div>
    );
};

export default ForgotPassword;
