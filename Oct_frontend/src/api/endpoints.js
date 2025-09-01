export const ENDPOINTS = {
    // Sale Items API - Updated to match Backend
    SALE_ITEMS: {
        ALL: '/itb-mshop/v1/sale-items',
        BY_ID: (id) => `/itb-mshop/v2/sale-items/${id}`,
        CREATE: '/itb-mshop/v2/sale-items',
        UPDATE: (id) => `/itb-mshop/v2/sale-items/${id}`,
        DELETE: (id) => `/itb-mshop/v2/sale-items/${id}`,
        // V2 endpoints for pagination, filtering, sorting
        V2_ALL: '/itb-mshop/v2/sale-items',
        V2_BY_ID: (id) => `/itb-mshop/v2/sale-items/${id}`,
        // Attachment management
        DELETE_ATTACHMENT: (saleItemId, order) => `/itb-mshop/v2/sale-items/${saleItemId}/attachments/by-order?imageViewOrder=${order}`,
    },
    // Brands API - Updated to match Backend
    BRANDS: {
        ALL: '/itb-mshop/v1/brands',
        BY_ID: (id) => `/itb-mshop/v1/brands/${id}`,
        CREATE: '/itb-mshop/v1/brands',
        UPDATE: (id) => `/itb-mshop/v1/brands/${id}`,
        DELETE: (id) => `/itb-mshop/v1/brands/${id}`,
    },
    // Users API - Placeholder for future use
    USERS: {
        PROFILE: '/users/profile',
        LOGIN: '/auth/login',
        REGISTER: '/auth/register',
        LOGOUT: '/auth/logout',
    },
};