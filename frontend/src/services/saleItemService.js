import { api, handleApiError } from '../api/client';
import { ENDPOINTS } from '../api/endpoints';

export async function fetchAllSaleItems() {
    try {
        return await api.get(ENDPOINTS.SALE_ITEMS.ALL);
    } catch (error) {
        throw error;
    }
}
