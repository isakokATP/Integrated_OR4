import { api } from "../api/client";

// Get current user ID from token
const getCurrentUserId = () => {
  const token = localStorage.getItem('accessToken');
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

// Add item to cart
export async function addToCart(saleItemId, quantity = 1) {
  try {
    const data = await api.post('/itb-mshop/v2/cart/items', {
      saleItemId: saleItemId,
      quantity: quantity
    });
    return data;
  } catch (error) {
    console.error('Error adding to cart:', error);
    throw error;
  }
}

// Get cart items
export async function getCart() {
  try {
    const data = await api.get('/itb-mshop/v2/cart');
    return data;
  } catch (error) {
    console.error('Error getting cart:', error);
    throw error;
  }
}

// Update cart item quantity
export async function updateCartItemQuantity(cartItemId, quantity) {
  try {
    const data = await api.put(`/itb-mshop/v2/cart/items/${cartItemId}`, {
      quantity: quantity
    });
    return data;
  } catch (error) {
    console.error('Error updating cart item:', error);
    throw error;
  }
}

// Remove cart item
export async function removeCartItem(cartItemId) {
  try {
    const result = await api.delete(`/itb-mshop/v2/cart/items/${cartItemId}`);
    return true;
  } catch (error) {
    console.error('Error removing cart item:', error);
    throw error;
  }
}

