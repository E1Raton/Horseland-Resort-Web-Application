import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../App.css';
import ThemeSwitcher from './ThemeSwitcher.tsx';
import { DARK_THEME, LIGHT_THEME } from '../constants/theme.ts';
import {AUTH_ENDPOINT} from "../constants/api.ts";

const Login: React.FC = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [, setCurrentTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);
    const navigate = useNavigate();

    const handleLogin = async (event: React.FormEvent) => {
        event.preventDefault();
        setErrorMessage('');

        try {
            const response = await axios.post(`${AUTH_ENDPOINT}/login`, {
                username,
                password,
            }, {
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response.status === 200) {
                const data = response.data;
                console.log('Login successful:', data);

                // Save user data in localStorage and sessionStorage
                sessionStorage.setItem('userId', data.userId);
                sessionStorage.setItem('authToken', data.token);
                sessionStorage.setItem('userRole', data.role); // Save the role (admin, student, instructor)
                sessionStorage.setItem('token', data.token);

                // Redirect based on user role
                if (/^admin$/i.test(data.role)) {
                    navigate('/admin', { replace: true });
                } else if (/^student$/i.test(data.role)) {
                    navigate('/student-dashboard', { replace: true });
                } else if (/^instructor$/i.test(data.role)) {
                    navigate('/instructor-dashboard', { replace: true });
                } else {
                    console.error('Unknown role received:', data.role);
                }
            }
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.status === 401) {
                console.error('Login failed:', error.response.data);
                setErrorMessage(error.response.data.errorMessage || 'Invalid credentials.');
            } else {
                console.error('An unexpected error occurred:', error);
                setErrorMessage('Failed to login. Please try again later.');
            }
        }
    };

    return (
        <div className="app-container">
            <ThemeSwitcher onThemeChange={setCurrentTheme} />
            <h1>Login</h1>
            <form className="form-container" onSubmit={handleLogin}>
                <div className="form-group">
                    <label htmlFor="username">Username:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                {errorMessage && <p className="error-message">{errorMessage}</p>}
                <button type="submit">Login</button>
                <p>
                    <a href="/forgot-password">Forgot Password?</a>
                </p>
            </form>
        </div>
    );
};

export default Login;