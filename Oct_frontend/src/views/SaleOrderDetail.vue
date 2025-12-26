<template>
  <Header />
  <div class="px-6 pt-2">
    <!-- Breadcrumb -->
    <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link to="/sale-orders" class="text-gray-500 hover:text-blue-600">Sale Orders</router-link>
        <span class="text-gray-400">/</span>
        <span class="font-semibold text-gray-800">Order #{{ order.orderId }}</span>
    </nav>
    
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <div v-else-if="error" class="alert alert-error">
      <span>{{ error }}</span>
    </div>

    <div v-else class="max-w-4xl mx-auto bg-white rounded-lg shadow-md overflow-hidden">
      <!-- Order Header -->
      <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
        <div>
            <h1 class="text-2xl font-bold text-gray-800">Order #{{ order.orderId }}</h1>
            <p class="text-sm text-gray-500">Placed on {{ formatDate(order.orderTimestamp) }}</p>
        </div>
        <div>
            <span class="px-3 py-1 rounded-full text-sm font-semibold inline-flex items-center gap-2"
                  :class="{
                    'bg-green-100 text-green-800': order.orderStatus === 'COMPLETED',
                    'bg-red-100 text-red-800': order.orderStatus === 'CANCELLED',
                    'bg-yellow-100 text-yellow-800': order.orderStatus === 'PENDING'
                  }">
              <span class="w-2 h-2 rounded-full" 
                    :class="{
                        'bg-green-600': order.orderStatus === 'COMPLETED',
                        'bg-red-600': order.orderStatus === 'CANCELLED',
                        'bg-yellow-600': order.orderStatus === 'PENDING'
                    }"></span>
              {{ order.orderStatus }}
            </span>
        </div>
      </div>

      <div class="p-6">
        <!-- Customer & Shipping Info -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <div class="bg-blue-50 p-4 rounded-lg">
                <h3 class="font-bold text-gray-800 mb-2 flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-blue-600" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                    </svg>
                    Customer
                </h3>
                <p class="text-lg font-medium text-gray-900">{{ order.buyerNickname || 'Unknown' }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
                 <h3 class="font-bold text-gray-800 mb-2 flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-gray-600" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd" />
                    </svg>
                    Shipping Address
                </h3>
                <p class="text-gray-700 whitespace-pre-wrap">{{ order.shippingAddress || '-' }}</p>
                <div v-if="order.note" class="mt-4 pt-4 border-t border-gray-200">
                    <span class="text-xs font-bold text-gray-500 uppercase">Note</span>
                    <p class="text-sm text-gray-600 italic mt-1">{{ order.note }}</p>
                </div>
            </div>
        </div>

        <!-- Order Items -->
        <div class="mb-8">
            <h3 class="font-bold text-gray-800 text-lg mb-4">Ordered Items</h3>
            <div class="border rounded-lg overflow-hidden">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                            <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Specs</th>
                            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Price/Unit</th>
                            <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Qty</th>
                            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Total</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <tr v-for="item in order.items" :key="item.saleItemId">
                            <td class="px-6 py-4 whitespace-nowrap">
                                <div class="flex items-center">
                                    <div class="h-12 w-12 flex-shrink-0 border rounded bg-gray-100 overflow-hidden">
                                        <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="h-full w-full object-cover" alt="">
                                        <div v-else class="h-full w-full flex items-center justify-center text-gray-400 text-xs">No Img</div>
                                    </div>
                                    <div class="ml-4">
                                        <div class="text-sm font-medium text-gray-900">{{ item.brandName }} {{ item.model }}</div>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-center text-sm text-gray-500">
                                {{ item.color }} · {{ item.storageGb }}GB
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-900">
                                ฿{{ formatCurrency(item.unitPrice) }}
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-center text-sm text-gray-900">
                                {{ item.quantity }}
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium text-blue-600">
                                ฿{{ formatCurrency(item.unitPrice * item.quantity) }}
                            </td>
                        </tr>
                    </tbody>
                    <tfoot class="bg-gray-50">
                        <tr>
                            <td colspan="4" class="px-6 py-4 text-right font-bold text-gray-900">Order Total</td>
                            <td class="px-6 py-4 text-right font-bold text-xl text-blue-700">฿{{ formatCurrency(order.totalPrice) }}</td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>

        <div class="flex justify-end">
            <button @click="router.push('/sale-orders')" class="px-6 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 font-medium transition-colors">
                Back to Orders
            </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getSellerOrderDetail, markOrderViewed } from '../services/orderService';
import Header from '../components/Header.vue';
import { handleApiError } from '../api/client';

const route = useRoute();
const router = useRouter();
const order = ref({});
const loading = ref(true);
const error = ref('');

const orderId = route.params.id;

// Helpers
const formatCurrency = (val) => (val || 0).toLocaleString();
const formatDate = (dateString) => {
    if (!dateString) return "-";
    return new Date(dateString).toLocaleDateString("en-US", {
        year: "numeric", month: "long", day: "numeric",
        hour: "2-digit", minute: "2-digit"
    });
};
const getImageUrl = (filename) => filename ? `/or4/itb-mshop/images/${encodeURIComponent(filename)}` : null;

const getCurrentUserId = () => {
  const token = localStorage.getItem("accessToken");
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.id;
  } catch (e) {
    console.error("Error decoding token:", e);
    return null;
  }
};

const fetchOrder = async () => {
    loading.value = true;
    error.value = '';
    const sellerId = getCurrentUserId();
    if(!sellerId) {
        error.value = "Not authenticated";
        loading.value = false;
        return;
    }

    try {
        const data = await getSellerOrderDetail(sellerId, orderId);
        order.value = data;
        
        // Always try to mark as viewed if status is COMPLETED or CANCELLED
        if (['COMPLETED', 'CANCELLED'].includes(data.orderStatus)) {
             try {
                await markOrderViewed(orderId);
             } catch(ignore) {
                 // Ignore if already viewed or permission issue (though getDetail would fail first)
             }
        }
    } catch (err) {
        error.value = handleApiError(err, "Failed to load order details");
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchOrder();
});
</script>
