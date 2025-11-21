import { handleApiError } from "../api/client";

const URL = import.meta.env.BASE_URL || "";

// Get auth token
const getAuthToken = () => {
  return sessionStorage.getItem('accessToken');
};

// Place order
export async function placeOrder(sellerGroups, shippingAddress = '', note = '') {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error('Not authenticated');
    }

    const response = await fetch(`${URL}/itb-mshop/v2/orders`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        sellerGroups: sellerGroups,
        shippingAddress: shippingAddress,
        note: note
      })
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Failed to place order');
      error.status = response.status;
      throw error;
    }

    return await response.json();
  } catch (error) {
    console.error('Error placing order:', error);
    throw error;
  }
}

// Get buyer orders
export async function getBuyerOrders(userId) {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error('Not authenticated');
    }

    const response = await fetch(`${URL}/itb-mshop/v2/users/${userId}/orders`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Failed to get orders');
      error.status = response.status;
      throw error;
    }

    return await response.json();
  } catch (error) {
    console.error('Error getting buyer orders:', error);
    throw error;
  }
}
