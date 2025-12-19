import { ref, computed } from 'vue';
import { getCart } from '../services/cartService';

// Global reactive cart count
const cartCount = ref(0);

// Function to update cart count
export async function updateCartCount() {
  try {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      cartCount.value = 0;
      return;
    }

    const cartData = await getCart();
    if (cartData && cartData.totalItems !== undefined) {
      cartCount.value = cartData.totalItems;
    } else {
      cartCount.value = 0;
    }
  } catch (error) {
    // Only log error if it's not a 401 (unauthorized)
    if (error.status !== 401) {
      console.error('Error updating cart count:', error);
    }
    cartCount.value = 0;
  }
}

// Composable to use cart count
export function useCartCount() {
  return {
    cartCount: computed(() => cartCount.value),
    updateCartCount
  };
}

