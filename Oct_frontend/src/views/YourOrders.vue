<template>
  <Header />
  <div class="max-w-6xl mx-auto p-6">
    <!-- Navigation Bar -->
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items"
        class="text-blue-600 hover:underline font-medium"
      >Home</router-link>
      <span class="mx-1">›</span>
      <span class="font-semibold">Your Orders</span>
    </nav>

    <h1 class="text-3xl font-bold mb-6">Your Orders</h1>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-error mb-4">
      <span>{{ error }}</span>
    </div>

    <!-- Empty Orders -->
    <div v-else-if="!sortedOrders || sortedOrders.length === 0" class="text-center py-12">
      <p class="text-xl text-gray-500">You have no orders yet</p>
      <router-link to="/sale-items" class="text-blue-600 hover:underline mt-4 inline-block">
        Start Shopping
      </router-link>
    </div>

    <!-- Orders List (flat, no seller grouping) -->
    <div v-else class="space-y-6">
      <div v-for="order in sortedOrders" :key="order.orderNumber" class="bg-white rounded-lg shadow-md p-6 space-y-4">
        <!-- Order Summary -->
        <div class="flex justify-between items-start mb-4">
          <div class="flex-1">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
              <div>
                <p class="text-sm text-gray-600">Order Date: <span class="font-semibold text-gray-800">{{ formatOrderDate(order.orderDate) }}</span></p>
                <p class="text-sm text-gray-600">Payment Date: <span class="font-semibold text-gray-800">{{ formatOrderDate(order.orderDate) }}</span></p>
                <p class="text-sm text-gray-600">Seller: <span class="font-semibold text-gray-800">{{ order.sellerNickname }}</span></p>
                <p class="text-sm text-gray-600">Order Number: <span class="font-semibold text-gray-800">{{ order.orderNumber }}</span></p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Shipping To:</p>
                <p class="text-sm font-semibold text-gray-800 mt-1">{{ order.shippingAddress || 'N/A' }}</p>
                <p class="text-sm text-gray-600 mt-2">Total Amount: <span class="font-semibold text-blue-600 text-lg">฿{{ order.totalAmount.toLocaleString() }}</span></p>
              </div>
            </div>
          </div>
          <div class="text-right ml-4">
            <span class="badge" :class="getStatusBadgeClass(order.status)">
              {{ order.status }}
            </span>
          </div>
        </div>

        <!-- Order Items -->
        <div class="space-y-3">
          <h4 class="font-semibold text-gray-700">Order Items:</h4>
          <div v-for="item in order.items" :key="item.saleItemId" class="flex gap-4 p-3 bg-gray-50 rounded-lg">
            <!-- Item Image -->
            <div class="w-24 h-24 flex-shrink-0 bg-gray-200 rounded-lg overflow-hidden">
              <img
                v-if="item.imageUrl"
                :src="getImageUrl(item.imageUrl)"
                :alt="item.description"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs">
                No Image
              </div>
            </div>

            <!-- Item Details -->
            <div class="flex-1">
              <p class="font-semibold text-gray-800">{{ item.description }}</p>
              <div class="mt-2 flex gap-4 text-sm text-gray-600">
                <span>Quantity: <span class="font-semibold">{{ item.quantity }}</span></span>
                <span>Unit Price: <span class="font-semibold">฿{{ item.unitPrice.toLocaleString() }}</span></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import Header from '../components/Header.vue';
import { getBuyerOrders } from '../services/orderService';

const router = useRouter();
const loading = ref(false);
const error = ref('');
const orders = ref([]);

// Get current user ID from token
const getCurrentUserId = () => {
  const token = sessionStorage.getItem('accessToken');
  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id;
  } catch (e) {
    console.error('Error decoding token:', e);
    return null;
  }
};

// Sorted flat orders (no grouping)
const sortedOrders = computed(() => {
  if (!orders.value) return [];
  return [...orders.value].sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));
});

// Format date for display
const formatOrderDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// Get image URL
const getImageUrl = (filename) => {
  if (!filename) return null;
  return `/or4/uploads/${encodeURIComponent(filename)}`;
};

// Status badge class
const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'COMPLETED': return 'badge-success';
    case 'CANCELLED': return 'badge-error';
    case 'PENDING': return 'badge-warning';
    default: return 'badge-info';
  }
};

// Load orders
const loadOrders = async () => {
  loading.value = true;
  error.value = '';
  try {
    const userId = getCurrentUserId();
    if (!userId) {
      error.value = 'Not authenticated';
      router.push({ name: 'login-page' });
      return;
    }

    orders.value = await getBuyerOrders(userId);
  } catch (err) {
    if (err.status === 401) {
      router.push({ name: 'login-page' });
    } else {
      error.value = err.message || 'Failed to load orders';
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadOrders();
});
</script>
