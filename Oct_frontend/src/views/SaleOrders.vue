<script>
import { ref, onMounted } from "vue";
import {
  fetchAndClassifySellerOrders,
  markOrderViewed,
} from "@/services/orderService";
import OrderList from "@/components/OrderList.vue";
import OrderDetailModal from "@/components/OrderDetailModal.vue";
import { newOrdersCount as globalNewOrdersCount, setNewOrdersCount } from "@/stores/orderStore";

export default {
  components: { OrderList, OrderDetailModal },
  setup() {
    const activeTab = ref("new");
    const newOrders = ref([]);
    const canceledOrders = ref([]);
    const allOrders = ref([]);
    const selectedOrder = ref(null);
    const showModal = ref(false);
    const loading = ref(false);
    const error = ref("");

    const getSellerId = () => {
      const token = sessionStorage.getItem("accessToken");
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
        newOrders.value = result.newOrders;
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

    const viewOrder = async (order) => {
      selectedOrder.value = order;
      showModal.value = true;

      if (!order.isViewed && order.status === "Completed") {
        await markOrderViewed(order.orderId);
        await fetchOrders();
      }
    };

    const closeModal = () => {
      showModal.value = false;
      selectedOrder.value = null;
    };

    onMounted(fetchOrders);

    return {
      activeTab,
      newOrders,
      canceledOrders,
      allOrders,
      selectedOrder,
      showModal,
      viewOrder,
      closeModal,
      loading,
      error,
      globalNewOrdersCount,
    };
  },
};
</script>

<template>
  <div class="sale-orders-page">
    <h1>Sale Orders</h1>

    <!-- Tabs -->
    <div class="tabs">
      <button
        @click="activeTab = 'new'"
        :class="{ active: activeTab === 'new' }"
      >
        New Orders
      </button>
      <button
        @click="activeTab = 'canceled'"
        :class="{ active: activeTab === 'canceled' }"
      >
        Canceled Orders
      </button>
      <button
        @click="activeTab = 'all'"
        :class="{ active: activeTab === 'all' }"
      >
        All Orders
      </button>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="flex justify-center items-center h-32">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="alert alert-error mb-4">
      {{ error }}
    </div>

    <!-- Tab Content -->
    <div v-else>
      <div v-if="activeTab === 'new' && newOrders.length === 0" class="text-center py-8">
        No new orders.
      </div>
      <div v-if="activeTab === 'canceled' && canceledOrders.length === 0" class="text-center py-8">
        No canceled orders.
      </div>
      <div v-if="activeTab === 'all' && allOrders.length === 0" class="text-center py-8">
        No orders.
      </div>

      <OrderList
        v-if="activeTab === 'new' && newOrders.length > 0"
        :orders="newOrders"
        @viewOrder="viewOrder"
      />
      <OrderList
        v-if="activeTab === 'canceled' && canceledOrders.length > 0"
        :orders="canceledOrders"
        @viewOrder="viewOrder"
      />
      <OrderList
        v-if="activeTab === 'all' && allOrders.length > 0"
        :orders="allOrders"
        @viewOrder="viewOrder"
      />
    </div>

    <!-- Modal -->
    <OrderDetailModal
      :order="selectedOrder"
      :visible="showModal"
      @close="closeModal"
    />
  </div>
</template>
