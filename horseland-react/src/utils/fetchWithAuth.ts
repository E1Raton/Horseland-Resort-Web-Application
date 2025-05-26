import { handleSessionExpired } from './handleSessionExpired';

export async function fetchWithAuth(input: RequestInfo, init: RequestInit = {}): Promise<Response> {
    const token = sessionStorage.getItem('token');

    const enhancedInit: RequestInit = {
        ...init,
        headers: {
            ...(init.headers || {}),
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
    };

    const response = await fetch(input, enhancedInit);

    if (response.status === 401) {
        handleSessionExpired();
        return Promise.reject("Unauthorized - session expired");
    }

    return response;
}
