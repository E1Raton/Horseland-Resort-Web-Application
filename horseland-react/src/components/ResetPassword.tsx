import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

const ResetPassword: React.FC = () => {
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');

    const navigate = useNavigate();
    const location = useLocation();

    const token = (location.state as { token?: string })?.token;
    const email = (location.state as { email?: string })?.email;

    const handleResetPassword = async () => {
        setError('');
        setMessage('');

        if (!newPassword || !confirmPassword) {
            setError('Please fill in both password fields.');
            return;
        }

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        if (!token || !email) {
            setError('Missing token or email.');
            return;
        }

        try {
            const payload = JSON.stringify({ email, password: newPassword });
            console.log('Payload:', payload);

            const response = await axios.post(
                'http://localhost:8080/auth/reset-password',
                payload,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                }
            );

            if (response.status === 200) {
                setMessage('Password reset successful. Redirecting to login...');
                setTimeout(() => navigate('/login'), 2000);
            }
        } catch (err: unknown) {
            if (axios.isAxiosError(err) && err.response) {
                const backendMessage = err.response.data?.errorMessage || 'Password reset failed.';
                setError(backendMessage);
            } else {
                setError('Unexpected error occurred. Please try again.');
            }
        }
    };

    return (
        <div className="app-container">
            <h2>Reset Password</h2>
            <div className="form-container">
                <div className="form-group">
                    <label>New Password:</label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label>Confirm Password:</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                    />
                </div>

                <button className="btn" onClick={handleResetPassword}>
                    Reset Password
                </button>

                {error && <p className="error-message">{error}</p>}
                {message && <p className="success-message">{message}</p>}
            </div>

            <div className="button-group">
                <button className="btn" onClick={() => navigate('/')}>
                    Go Back
                </button>
            </div>
        </div>
    );
};

export default ResetPassword;
