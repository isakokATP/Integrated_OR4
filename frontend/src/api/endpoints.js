export const ENDPOINTS = {
    // สมมุติมีเพิ่ม SALE_ITEMS หลายๆแบบ
    SALE_ITEMS: {
        ALL: '/sale-items/all',
        BY_ID: (id) => `/sale-items/${id}`,
        CREATE: '/sale-items',
        UPDATE: (id) => `/sale-items/${id}`,
        DELETE: (id) => `/sale-items/${id}`,
    },
    // สมมุติมีเพิ่ม USERS
    USERS: {
        PROFILE: '/users/profile',
        LOGIN: '/auth/login',
        REGISTER: '/auth/register',
        LOGOUT: '/auth/logout',
    },
};