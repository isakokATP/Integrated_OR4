<template>
  <div></div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { updateCartCount } from '../composables/useCartCount';

const router = useRouter();

onMounted(async () => {
  try {
    // Call logout API
    await fetch(`${import.meta.env.BASE_URL}itb-mshop/v2/auth/logout`, {
      method: 'POST',
      credentials: 'include'
    });
  } catch (err) {
    console.error('Logout API error:', err);
    // Continue with clearing storage even if API call fails
  }

  // Clear session storage
  sessionStorage.clear();
  
  // Clear local storage
  localStorage.clear();
  
  // Update cart count to 0
  await updateCartCount();
  
  // Redirect to sale-items gallery page
  router.push({ name: 'sale-items-page' });
});
</script>

