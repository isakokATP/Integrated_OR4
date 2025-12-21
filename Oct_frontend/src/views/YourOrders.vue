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

    <!-- Tabs -->
    <div class="flex space-x-4 mb-6 border-b border-gray-200">
      <button
        @click="currentTab = 'ALL'"
        class="pb-2 px-4 font-medium text-sm transition-colors relative"
        :class="currentTab === 'ALL' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'"
      >
        All Orders
      </button>
      <button
        @click="currentTab = 'CANCELED'"
        class="pb-2 px-4 font-medium text-sm transition-colors relative"
        :class="currentTab === 'CANCELED' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500 hover:text-gray-700'"
      >
        Canceled
      </button>
    </div>

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

    <!-- Bills List -->
    <div v-else class="space-y-8">
      <div v-for="bill in groupedBills" :key="bill.id" class="border border-gray-300 rounded-xl overflow-hidden bg-white shadow-sm">
        
        <!-- Bill Header -->
        <div class="bg-gray-100 px-6 py-4 border-b border-gray-300 flex flex-col md:flex-row justify-between md:items-center gap-4">
          <div>
            <h2 class="text-lg font-bold text-gray-800">Bill placed on {{ formatOrderDate(bill.billDate) }}</h2>
            <p class="text-sm text-gray-500 mt-1">Contains {{ bill.orders.length }} seller order(s)</p>
          </div>
          <div class="flex flex-col md:items-end">
             <p class="text-sm text-gray-500">Total Bill Amount</p>
             <p class="text-2xl font-bold text-blue-700">฿{{ bill.totalAmount.toLocaleString() }}</p>
          </div>
        </div>

        <!-- Bill Orders (Grouped by Seller) -->
        <div class="p-6 space-y-6 bg-gray-50">
          <div v-for="order in bill.orders" :key="order.orderNumber" class="bg-white rounded-lg shadow-sm border border-gray-200">
            
            <!-- Seller Order Header -->
            <div class="px-5 py-3 border-b border-gray-100 flex justify-between items-center bg-white rounded-t-lg">
              <div class="flex items-center gap-4">
                 <span class="text-sm text-gray-600">Seller: <span class="font-bold text-blue-600">{{ getSellerName(order) }}</span></span>
                 <span class="text-sm text-gray-500">Order #{{ order.orderNumber }}</span>
              </div>
              <span class="badge" :class="getStatusBadgeClass(order.orderStatus || order.status)">
                {{ order.orderStatus || order.status }}
              </span>
            </div>

            <!-- Order Items -->
            <div class="p-5">
              <div class="space-y-4">
                <div v-for="item in order.items" :key="item.saleItemId" class="flex gap-4">
                  <!-- Item Image -->
                  <div class="w-20 h-20 flex-shrink-0 bg-gray-100 rounded-md overflow-hidden border border-gray-200">
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
                  <div class="flex-1 min-w-0">
                    <p class="font-medium text-gray-900 truncate">{{ item.description }}</p>
                    <div class="mt-1 text-sm text-gray-500 flex flex-wrap gap-x-4 gap-y-1">
                      <span v-if="item.brandName">Brand: {{ item.brandName }}</span>
                      <span v-if="item.model">Model: {{ item.model }}</span>
                      <span v-if="item.color">Color: {{ item.color }}</span>
                      <span v-if="item.storageGb">{{ item.storageGb }}GB</span>
                    </div>
                    <div class="mt-2 text-sm flex justify-between items-end">
                      <div class="text-gray-600">
                        Qty: <span class="font-semibold">{{ item.quantity }}</span> × ฿{{ item.unitPrice.toLocaleString() }}
                      </div>
                      <div class="font-semibold text-gray-900">
                        ฿{{ (item.quantity * item.unitPrice).toLocaleString() }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Seller Order Footer -->
              <div class="mt-4 pt-4 border-t border-gray-100 flex justify-between items-center text-sm">
                <div class="text-gray-500">
                   Shipping: {{ order.shippingAddress || 'N/A' }}
                </div>
                <div class="font-semibold text-gray-700">
                  Subtotal: ฿{{ order.totalAmount.toLocaleString() }}
                </div>
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
const currentTab = ref('ALL');

// Get current user ID from token
const getCurrentUserId = () => {
  const token = localStorage.getItem('accessToken');
  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id;
  } catch (e) {
    console.error('Error decoding token:', e);
    return null;
  }
};


// Helper to get seller name from an order
const getSellerName = (order) => {
   // Assuming items in an order belong to the same seller as per backend structure
   if (order.items && order.items.length > 0) {
      const item = order.items[0];
       // Try different possible properties for seller name
      return item.sellerNickname || item.seller?.nickName || item.seller?.nickname || order.sellerNickname || 'Unknown Seller';
   }
   return order.sellerNickname || 'Unknown Seller';
};

// Filter orders based on current tab
const filteredOrders = computed(() => {
  if (!orders.value) return [];
  if (currentTab.value === 'ALL') return orders.value;
  if (currentTab.value === 'CANCELED') {
    return orders.value.filter(o => {
      const status = (o.status || o.orderStatus || '').toUpperCase();
      return status.includes('CANCEL');
    });
  }
  return orders.value;
});

// Group orders into bills based on time proximity (1 second window)
const groupedBills = computed(() => {
  const sourceOrders = filteredOrders.value;
  if (!sourceOrders || sourceOrders.length === 0) return [];

  // First sort by date descending
  const sorted = [...sourceOrders].sort((a, b) => {
    const dateA = new Date(a.orderDate || a.orderTimestamp || 0);
    const dateB = new Date(b.orderDate || b.orderTimestamp || 0);
    return dateB - dateA; // Newest first
  });

  const bills = [];
  let currentBill = null;

  sorted.forEach(order => {
    const orderTime = new Date(order.orderDate || order.orderTimestamp || 0).getTime();

    if (!currentBill) {
      // Start first bill
      currentBill = {
        id: `bill-${orderTime}`,
        billDate: order.orderDate || order.orderTimestamp,
        orders: [order],
        totalAmount: order.totalAmount
      };
      bills.push(currentBill);
    } else {
      // Check if this order belongs to current bill (within 1000ms of the LATEST order in the group? 
      // Or 1000ms of the PREVIOUSLY added order? 
      // The logic: if (prevOrderTime - thisOrderTime) <= 1000ms. 
      // Since sorted DESC, prevOrderTime >= thisOrderTime.
      
      const lastOrderInBill = currentBill.orders[currentBill.orders.length - 1];
      const lastOrderTime = new Date(lastOrderInBill.orderDate || lastOrderInBill.orderTimestamp || 0).getTime();
      
      // Difference in ms
      const diff = Math.abs(lastOrderTime - orderTime);

      if (diff <= 1000) {
        // Add to current bill
        currentBill.orders.push(order);
        currentBill.totalAmount += order.totalAmount;
      } else {
        // Create new bill
         currentBill = {
            id: `bill-${orderTime}`,
            billDate: order.orderDate || order.orderTimestamp,
            orders: [order],
            totalAmount: order.totalAmount
         };
         bills.push(currentBill);
      }
    }
  });

  return bills;
});

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
const getImageUrl = (filename) => filename ? `/or4/itb-mshop/images/${encodeURIComponent(filename)}` : null;

// Get status badge class
const getStatusBadgeClass = (status) => {
  if (!status) return 'badge-info';
  const s = status.toUpperCase();
  if (s === 'COMPLETED') return 'badge-success';
  if (s.includes('CANCEL')) return 'badge-error';
  if (s === 'PENDING') return 'badge-warning';
  return 'badge-info';
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
