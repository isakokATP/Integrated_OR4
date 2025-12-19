<script setup>
import { useRouter } from "vue-router";
import { addToCart } from "../services/cartService";
import { updateCartCount } from "../composables/useCartCount";

const props = defineProps({
  items: Array,
});

const emit = defineEmits(['cart-updated']);

const router = useRouter();

function goToSaleItem(id) {
  router.push({ name: "sale-items-page-byId", params: { id } });
}

async function handleAddToCart(event, item) {
  event.stopPropagation(); // Prevent card click
  
  // Check if user is logged in
  const token = localStorage.getItem('accessToken');
  
  if (!token) {
    router.push({ name: 'login-page' });
    return;
  }

  try {
    await addToCart(item.id, 1);
    // Update cart count in header
    await updateCartCount();
    // Emit event to parent to show notification
    emit('cart-updated', 'Item added to cart successfully!');
  } catch (error) {
    if (error.status === 401) {
      router.push({ name: 'login-page' });
    } else {
      // Emit error message to parent
      emit('cart-updated', error.message || 'Failed to add item to cart');
    }
  }
}
</script>

<template>
  <div class="max-w-6xl mx-auto mt-8">
    <div>
      <div
        v-if="Array.isArray(items) && items.length === 0"
        class="flex justify-center w-full py-12"
      >
        <div class="text-center">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-16 h-16 text-gray-400 mx-auto mb-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5m8.25 3v6.75m0 0l-3-3m3 3l3-3M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z" />
          </svg>
          <p class="text-xl text-gray-500 font-medium">No sale items found</p>
          <p class="text-gray-400 mt-2">Try adjusting your filters or search terms</p>
        </div>
      </div>

      <div
        v-else-if="Array.isArray(items) && items.length > 0"
        class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6"
      >
        <div
          v-for="item in items"
          :key="item.id"
          @click="goToSaleItem(item.id)"
          class="itbms-row bg-white rounded-2xl overflow-hidden cursor-pointer transition-all duration-300 hover:transform hover:scale-105 shadow-lg hover:shadow-2xl border border-gray-100 group"
        >
          <div class="relative overflow-hidden">
            <img
              src="../assets/iphone.png"
              alt="Product"
              class="w-full h-56 object-cover group-hover:scale-110 transition-transform duration-500"
            />
            <!-- Hover overlay -->
            <div class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </div>
          
          <div class="p-6 text-center bg-gradient-to-br from-blue-50 to-indigo-50">
            <strong class="itbms-brand text-lg font-bold text-gray-800 mb-2 block">{{
              item.brandName
            }}</strong>
            <p class="itbms-model text-gray-600 mb-3 font-medium">{{ item.model }}</p>
            
            <div class="flex items-center justify-center gap-2 mb-4 text-sm text-gray-500">
              <span class="bg-white px-2 py-1 rounded-lg shadow-sm">
                {{ item.ramGb ?? "-" }}
              </span>
              <span class="text-gray-400">/</span>
              <span class="bg-white px-2 py-1 rounded-lg shadow-sm">
                {{ item.storageGb ?? "-" }}
              </span>
              <span class="text-gray-400">GB</span>
            </div>
            
            <div class="itbms-price text-2xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
              ฿{{ item.price.toLocaleString() }}
            </div>
            
            <!-- Action Buttons -->
            <div class="mt-4 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
              <button
                @click.stop="handleAddToCart($event, item)"
                class="flex-1 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium shadow-md transition-all duration-200"
              >
                Add to Cart
              </button>
              <button
                @click.stop="goToSaleItem(item.id)"
                class="flex-1 bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white px-4 py-2 rounded-lg text-sm font-medium shadow-md transition-all duration-200"
              >
                Details →
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Enhanced card animations */
.itbms-row {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.itbms-row:hover {
  transform: translateY(-8px);
}

/* Smooth image zoom effect */
.itbms-row img {
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Enhanced price styling */
.itbms-price {
  background-size: 200% 200%;
  animation: gradient-shift 3s ease infinite;
}

@keyframes gradient-shift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* Smooth transitions for all interactive elements */
* {
  transition-property: color, background-color, border-color, text-decoration-color, fill, stroke, opacity, box-shadow, transform, filter, backdrop-filter;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}
</style>
