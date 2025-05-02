import { api, handleApiError } from '../api/client';
import { ENDPOINTS } from '../api/endpoints';

export async function fetchAllSaleItems() {
    try {
        const response = await api.get(ENDPOINTS.SALE_ITEMS.ALL);
        console.log('Fetch response:', response);
        return response;
    } catch (error) {
        console.error('Error fetching all sale items:', {
            url: error.url,
            status: error.status,
            message: error.message,
            fullError: error
        });
        throw error;
    }
}
