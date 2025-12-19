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
    <div v-else-if="!orders || orders.length === 0" class="text-center py-12">
      <p class="text-xl text-gray-500">You have no orders yet</p>
      <router-link to="/sale-items" class="text-blue-600 hover:underline mt-4 inline-block">
        Start Shopping
      </router-link>
    </div>

    <!-- Orders List - Each order is one bill/card -->
    <div v-else class="space-y-6">
      <div v-for="order in sortedOrders" :key="order.orderNumber" class="bg-white rounded-lg shadow-md border-l-4" :class="getOrderBorderClass(order.status)">
        <!-- Header: Order Time -->
        <div class="bg-gray-50 px-6 py-3 border-b border-gray-200">
          <h2 class="text-lg font-bold text-gray-800">{{ formatOrderDate(order.orderDate) }}</h2>
          <div class="flex justify-between items-center mt-1">
            <p class="text-sm text-gray-600">
              Order Number: <span class="font-semibold">{{ order.orderNumber }}</span>
            </p>
            <span class="badge" :class="getStatusBadgeClass(order.status)">
              {{ order.status }}
            </span>
          </div>
        </div>

        <!-- Order Content - Group items by seller -->
        <div class="p-6 space-y-6">
          <!-- Group items by seller -->
          <div v-for="(sellerGroup, sellerId) in groupItemsBySeller(order.items)" :key="sellerId" class="space-y-4">
            <!-- Seller Header -->
            <div class="border-l-4 border-blue-400 pl-4 py-2 bg-blue-50 rounded-r">
              <h3 class="font-semibold text-gray-700">Seller: {{ sellerGroup.sellerName }}</h3>
            </div>
            
            <!-- Items from this seller -->
            <div class="space-y-3 ml-4">
              <div v-for="item in sellerGroup.items" :key="item.saleItemId" class="flex gap-4 p-3 bg-gray-50 rounded-lg">
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
                  <!-- Display brand, model, color, storage size -->
                  <div class="mt-1 text-sm text-gray-600">
                    <span v-if="item.brandName" class="mr-2">Brand: <span class="font-semibold">{{ item.brandName }}</span></span>
                    <span v-if="item.model" class="mr-2">Model: <span class="font-semibold">{{ item.model }}</span></span>
                    <span v-if="item.color" class="mr-2">Color: <span class="font-semibold">{{ item.color }}</span></span>
                    <span v-if="item.storageGb" class="mr-2">Storage: <span class="font-semibold">{{ item.storageGb }}GB</span></span>
                  </div>
                  <div class="mt-2 flex gap-4 text-sm text-gray-600">
                    <span>Quantity: <span class="font-semibold">{{ item.quantity }}</span></span>
                    <span>Unit Price: <span class="font-semibold">฿{{ item.unitPrice.toLocaleString() }}</span></span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Order Summary Footer -->
          <div class="pt-4 border-t border-gray-200 mt-4">
            <div class="flex justify-between items-center">
              <div class="text-sm text-gray-600">
                <p>Shipping To: <span class="font-semibold">{{ order.shippingAddress || 'N/A' }}</span></p>
                <p v-if="order.paymentDate" class="mt-1">Payment Date: <span class="font-semibold">{{ formatOrderDate(order.paymentDate) }}</span></p>
              </div>
              <div class="text-right">
                <p class="text-sm text-gray-600">Total Amount:</p>
                <p class="text-2xl font-bold text-blue-600">฿{{ order.totalAmount.toLocaleString() }}</p>
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

// Sort orders by orderDate DESC (newest first)
const sortedOrders = computed(() => {
  return orders.value
    ? [...orders.value].sort((a, b) => {
        const dateA = new Date(a.orderDate || a.orderTimestamp || 0);
        const dateB = new Date(b.orderDate || b.orderTimestamp || 0);
        return dateB - dateA; // DESC: newest first
      })
    : [];
});

// Group items within an order by seller
const groupItemsBySeller = (items) => {
  if (!items || items.length === 0) return {};
  
  const grouped = {};
  items.forEach(item => {
    const sellerId = item.sellerId || item.seller?.id || 'unknown';
    const sellerName = item.sellerNickname || item.seller?.nickName || item.seller?.nickname || 'Unknown Seller';
    
    if (!grouped[sellerId]) {
      grouped[sellerId] = {
        sellerName: sellerName,
        items: []
      };
    }
    
    grouped[sellerId].items.push(item);
  });
  
  return grouped;
};

// Get border color class based on order status
const getOrderBorderClass = (status) => {
  switch (status) {
    case 'COMPLETED': return 'border-green-500';
    case 'CANCELLED': return 'border-red-500';
    case 'CANCELED': return 'border-red-500';
    case 'PENDING': return 'border-yellow-500';
    case 'PROCESSING': return 'border-blue-500';
    case 'SHIPPED': return 'border-purple-500';
    case 'DELIVERED': return 'border-green-600';
    default: return 'border-gray-400';
  }
};

// Format order date for display
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
const getImageUrl = (filename) => filename ? `/or4/uploads/${encodeURIComponent(filename)}` : null;

// Get status badge class
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

onMounted(() => loadOrders());
</script>
