// src/api/axiosConfig.ts
import axios, { AxiosError } from 'axios';

const api = axios.create({
    baseURL: 'https://your-api-base-url.com', // 🔁 Replace with your API URL
    withCredentials: true,
});

// Intercept 401 Unauthorized responses (e.g., expired token)
api.interceptors.response.use(
    response => response,
    (error: AxiosError) => {
        if (error.response?.status === 401) {
            alert('Session has expired. Please log in again.');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;