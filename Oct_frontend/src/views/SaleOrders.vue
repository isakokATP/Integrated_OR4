<script>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  fetchAndClassifySellerOrders,
  markOrderViewed,
} from "@/services/orderService";
import OrderList from "@/components/OrderList.vue";
import { newOrdersCount as globalNewOrdersCount, setNewOrdersCount } from "@/stores/orderStore";

export default {
  components: { OrderList },
  setup() {
    const router = useRouter();
    const activeTab = ref("new");
    const newOrders = ref([]);
    const canceledOrders = ref([]);
    const allOrders = ref([]);
    const loading = ref(false);
    const error = ref("");

    const getSellerId = () => {
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

    const sellerId = getSellerId();

    const fetchOrders = async () => {
      if (!sellerId) {
        console.error("Seller ID not found in session");
        error.value = "Seller ID not found.";
        return;
      }

      loading.value = true;
      error.value = "";

      try {
        const result = await fetchAndClassifySellerOrders(sellerId);
        // Include canceled orders in the "New Orders" tab as requested
        const unviewedCanceled = result.canceledOrders.filter(o => !o.isViewed);
        newOrders.value = [...result.newOrders, ...unviewedCanceled].sort((a, b) => b.orderId - a.orderId);
        canceledOrders.value = result.canceledOrders;
        allOrders.value = result.allOrders;
        setNewOrdersCount(result.newOrders.concat(result.allOrders).length);
      } catch (err) {
        console.error("Error fetching orders:", err);
        error.value = "Failed to load orders.";
      } finally {
        loading.value = false;
      }
    };

    const viewOrder = (order) => {
      // Navigate to detail page
      router.push({ name: 'sale-order-detail-page', params: { id: order.orderId } });
    };

    onMounted(fetchOrders);

    const formatDate = (dateString) => {
      if (!dateString) return "-";
      return new Date(dateString).toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    };

    const formatCurrency = (value) => {
      return (value || 0).toLocaleString();
    };

    const getImageUrl = (filename) => {
      // Use absolute path /uploads/ as Nginx has a root-level location for it
      return filename ? `/or4/itb-mshop/images/${encodeURIComponent(filename)}` : null;
    };

    const getStatusColor = (status) => {
      if (!status) return "text-gray-500";
      const s = status.toUpperCase();
      if (s === "COMPLETED") return "text-green-600";
      if (s.includes("CANCEL")) return "text-red-600"; // Handle CANCELLED / Canceled
      if (s === "PENDING") return "text-yellow-600";
      return "text-blue-600";
    };

    return {
      activeTab,
      newOrders,
      canceledOrders,
      allOrders,
      viewOrder,
      loading,
      error,
      globalNewOrdersCount,
      formatDate,
      formatCurrency,
      getImageUrl,
      getStatusColor,
    };
  },
};
</script>

<template>
  <div class="max-w-6xl mx-auto p-6 bg-white min-h-screen">
    <h1 class="text-3xl font-bold mb-6 text-gray-800">Sale Orders</h1>

    <!-- Tabs -->
    <div class="flex space-x-8 mb-6 border-b border-gray-200">
      <button
        @click="activeTab = 'new'"
        class="pb-2 px-1 font-medium text-base transition-colors relative itbms-new-orders-button"
        :class="activeTab === 'new' ? 'text-black border-b-2 border-black font-bold' : 'text-gray-500 hover:text-gray-700'"
      >
        New Orders
      </button>
      <button
        @click="activeTab = 'canceled'"
        class="pb-2 px-1 font-medium text-base transition-colors relative itbms-canceled-orders-button"
        :class="activeTab === 'canceled' ? 'text-black border-b-2 border-black font-bold' : 'text-gray-500 hover:text-gray-700'"
      >
        Canceled Orders
      </button>
      <button
        @click="activeTab = 'all'"
        class="pb-2 px-1 font-medium text-base transition-colors relative itbms-all-orders-button"
        :class="activeTab === 'all' ? 'text-black border-b-2 border-black font-bold' : 'text-gray-500 hover:text-gray-700'"
      >
        All Orders
      </button>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="alert alert-error mb-4">
      <span>{{ error }}</span>
    </div>

    <!-- Content -->
    <div v-else class="space-y-6">
      
      <!-- Empty States -->
      <div v-if="activeTab === 'new' && newOrders.length === 0" class="text-center py-12 text-gray-500 text-lg">
        No new orders.
      </div>
      <div v-if="activeTab === 'canceled' && canceledOrders.length === 0" class="text-center py-12 text-gray-500 text-lg">
        No canceled orders.
      </div>
      <div v-if="activeTab === 'all' && allOrders.length === 0" class="text-center py-12 text-gray-500 text-lg">
        No orders found.
      </div>

      <!-- Order List (Render based on active tab) -->
      <div v-for="order in (activeTab === 'new' ? newOrders : activeTab === 'canceled' ? canceledOrders : allOrders)" 
           :key="order.orderId" 
           class="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden"
      >
        <!-- Card Header -->
        <div class="p-6 bg-gray-50 border-b border-gray-100">
          <div class="grid grid-cols-1 md:grid-cols-12 gap-4 items-start">
            
            <!-- User Info -->
            <div class="md:col-span-3 flex items-center space-x-3">
              <div class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-gray-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
              <div>
                <div class="font-bold text-gray-900 itbms-nickname">{{ order.buyerNickname || 'Unknown' }}</div>
              </div>
            </div>

            <!-- Order Details Grid -->
            <div class="md:col-span-9 grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <p class="text-xs text-gray-500 font-semibold uppercase">Order No:</p>
                <p class="text-sm font-medium text-gray-900">{{ order.orderId }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-500 font-semibold uppercase">Order Date:</p>
                <p class="text-sm font-medium text-gray-900">{{ formatDate(order.orderTimestamp) }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-500 font-semibold uppercase">Payment Date:</p>
                <p class="text-sm font-medium text-gray-900">{{ formatDate(order.orderTimestamp) }}</p> <!-- Using order date for now as payment date isn't distinct -->
              </div>
              <div class="text-right">
                 <div class="mb-1">
                    <p class="text-xs text-gray-500 font-semibold uppercase">Total:</p>
                    <p class="text-lg font-bold text-gray-900">{{ formatCurrency(order.totalPrice) }}</p>
                 </div>
                 <div class="mb-2">
                    <p class="text-xs text-gray-500 font-semibold uppercase">Status:</p>
                    <p class="text-sm font-medium" :class="getStatusColor(order.orderStatus)">{{ order.orderStatus }}</p>
                 </div>
                 <button 
                  @click="viewOrder(order)" 
                  class="bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-4 py-1.5 rounded transition-colors itbms-view-button"
                 >
                   View
                 </button>
              </div>
            </div>
          </div>
          
          <!-- Shipping Address -->
          <div class="mt-4 pt-4 border-t border-gray-200 text-sm">
             <span class="font-bold text-gray-800">Shipped To: </span>
             <span class="text-gray-600">{{ order.shippingAddress }}</span>
          </div>
           <!-- Note -->
          <div class="mt-2 text-sm" v-if="order.note">
             <span class="font-bold text-gray-800">Note : </span>
             <span class="text-gray-600">{{ order.note }}</span>
          </div>
        </div>

        <!-- Order Items -->
        <div class="divide-y divide-gray-100">
          <div v-for="item in order.items" :key="item.saleItemId" class="p-4 flex items-center space-x-4 hover:bg-gray-50 transition-colors">
            <!-- Item Image -->
            <div class="w-16 h-16 bg-gray-100 rounded border border-gray-200 flex-shrink-0 overflow-hidden">
               <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover" alt="Item" />
               <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs">No Img</div>
            </div>

            <!-- Item Details -->
            <div class="flex-1 min-w-0">
               <p class="text-sm font-bold text-gray-900 mb-1">
                 {{ item.brandName }} {{ item.model }} ({{ item.storageGb }}, {{ item.color }})
               </p>
               <p class="text-xs text-gray-500 truncate">{{ item.description }}</p>
            </div>

            <!-- Qty -->
            <div class="text-sm text-gray-600 font-medium px-4">
              Qty {{ item.quantity }}
            </div>

            <!-- Price -->
            <div class="text-right w-32">
              <p class="text-sm text-gray-500">Price: <span class="font-bold text-gray-900 text-base">{{ formatCurrency(item.unitPrice * item.quantity) }}</span></p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>
