const baseUrl = import.meta.env.VITE_API_URL || '/api';
async function apiCall(endpoint, options = {}) {
    const url = `${baseUrl}${endpoint}`;
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const requestOptions = { ...defaultOptions, ...options };

    try {
        const response = await fetch(url, requestOptions);
        if (!response.ok) {
            let errorMessage;
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || `HTTP Error: ${response.status}`;
            } catch (e) {
                errorMessage = `HTTP Error: ${response.status} ${response.statusText}`;
            }

            const error = new Error(errorMessage);
            error.status = response.status;
            error.response = response;
            throw error;
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }

        return response;
    } catch (error) {
        if (!error.status) {
            error.message = `Network Error: ${error.message}`;
            error.isNetworkError = true;
        }

        console.error('API Error:', error);
        throw error;
    }
}

export const api = {
    get: (endpoint, options = {}) => apiCall(endpoint, { ...options, method: 'GET' }),
    post: (endpoint, data, options = {}) => apiCall(endpoint, {
        ...options,
        method: 'POST',
        body: JSON.stringify(data)
    }),
    put: (endpoint, data, options = {}) => apiCall(endpoint, {
        ...options,
        method: 'PUT',
        body: JSON.stringify(data)
    }),
    delete: (endpoint, options = {}) => apiCall(endpoint, { ...options, method: 'DELETE' }),
};

export function handleApiError(error, defaultMessage = 'not connect server') {
    switch (error.status) {
        case 400:
            return '400';
        case 401:
            return '401';
        case 403:
            return '403';
        case 404:
            return '404';
        case 500:
            return '500';
        default:
            if (error.isNetworkError) {
                return 'not connect server';
            }
            return error.message || defaultMessage;
    }
}