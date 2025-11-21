import { handleApiError } from "../api/client";

const URL = import.meta.env.BASE_URL || "";

// Get current user ID from token
const getCurrentUserId = () => {
  const token = sessionStorage.getItem('accessToken');
  if (!token) {
    return null;
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id;
  } catch (e) {
    console.error('Error decoding token:', e);
    return null;
  }
};

// Get auth token
const getAuthToken = () => {
  return sessionStorage.getItem('accessToken');
};

// Add item to cart
export async function addToCart(saleItemId, quantity = 1) {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error('Not authenticated');
    }

    const response = await fetch(`${URL}/itb-mshop/v2/cart/items`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        saleItemId: saleItemId,
        quantity: quantity
      })
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Failed to add item to cart');
      error.status = response.status;
      throw error;
    }

    return await response.json();
  } catch (error) {
    console.error('Error adding to cart:', error);
    throw error;
  }
}

// Get cart items
export async function getCart() {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error('Not authenticated');
    }

    const response = await fetch(`${URL}/itb-mshop/v2/cart`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Failed to get cart');
      error.status = response.status;
      throw error;
    }

    return await response.json();
  } catch (error) {
    console.error('Error getting cart:', error);
    throw error;
  }
}

// Update cart item quantity
export async function updateCartItemQuantity(cartItemId, quantity) {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error('Not authenticated');
    }

    const response = await fetch(`${URL}/itb-mshop/v2/cart/items/${cartItemId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        quantity: quantity
      })
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Failed to update cart item');
      error.status = response.status;
      throw error;
    }

    return await response.json();
  } catch (error) {
    console.error('Error updating cart item:', error);
    throw error;
  }
}

// Remove cart item
export async function removeCartItem(cartItemId) {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error('Not authenticated');
    }

    const response = await fetch(`${URL}/itb-mshop/v2/cart/items/${cartItemId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Failed to remove cart item');
      error.status = response.status;
      throw error;
    }

    return true;
  } catch (error) {
    console.error('Error removing cart item:', error);
    throw error;
  }
}

