<template>
  <Header 
    :searchValue="searchQuery" 
    @search="handleSearchUpdate" 
  />
  <div class="px-6 pt-2">
    <Notification :message="message" />
    
    <!-- Navigation Bar -->
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items"
        class="text-blue-600 hover:underline font-medium"
      >Home</router-link>
      <span class="mx-1">›</span>
      <span class="font-semibold">Sale Items List</span>
    </nav>
    
    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-error mb-4">
      <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span>{{ error }}</span>
    </div>

    <!-- Content -->
    <div v-else>
    <div class="flex justify-between mb-4">
      <button
        class="itbms-sale-item-add text-white bg-blue-900 hover:bg-blue-500 text-lg px-6 py-3 rounded"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
      <button
        class="itbms-manage-brand bg-orange-600 hover:bg-orange-400 text-white px-6 py-3 rounded"
        @click="goToManageBrand"
      >
        Manage Brand
      </button>
    </div>
    <div v-if="!saleItems || saleItems.length === 0">
      <div class="flex justify-center w-full">no sale item</div>
    </div>
    <div v-else class="overflow-x-auto mt-10">
      <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-200 text-center">
          <tr>
            <th class="px-3 py-2 border-r border-gray-300">Id</th>
            <th class="px-3 py-2 border-r border-gray-300">Brand</th>
            <th class="px-3 py-2 border-r border-gray-300">Model</th>
            <th class="px-3 py-2 border-r border-gray-300">Ram</th>
            <th class="px-3 py-2 border-r border-gray-300">Storage</th>
            <th class="px-3 py-2 border-r border-gray-300">Color</th>
            <th class="px-3 py-2 border-r border-gray-300">Price</th>
            <th class="px-3 py-2">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in saleItems"
            :key="item.id"
            class="itbms-row border-b border-gray-300 hover:bg-[#E3F2FD] even:bg-gray-100 text-center"
          >
            <td class="itbms-id px-3 py-2 border-r border-gray-300">
              {{ item.id ? item.id : "-" }}
            </td>
            <td class="itbms-brand px-3 py-2 border-r border-gray-300">
              {{ item.brandName ? item.brandName : "-" }}
            </td>
            <td
              class="itbms-model px-3 py-2 border-r border-gray-300 text-left"
            >
              {{ item.model ? item.model : "-" }}
            </td>
            <td class="itbms-ramGb px-3 py-2 border-r border-gray-300">
              {{ item.ramGb ? item.ramGb : "-" }}
            </td>
            <td class="itbms-storageGb px-3 py-2 border-r border-gray-300">
              {{ item.storageGb ? item.storageGb : "-" }}
            </td>
            <td class="itbms-color px-3 py-2 border-r border-gray-300">
              {{ item.color ? item.color : "-" }}
            </td>
            <td class="itbms-price px-3 py-2 border-r border-gray-300">
              {{ item.price ? item.price.toLocaleString() : "-" }}
            </td>
            <td class="itbms-action px-3 py-2 space-x-1">
              <!-- Edit ปุ่มสีเขียว -->
              <button
                class="itbms-edit-button btn btn-xs bg-green-600 hover:bg-green-500 text-white px-2 py-1 rounded"
                @click="goToEdit(item.id)"
                title="Edit"
              >
                E
              </button>

              <!-- Delete ปุ่มสีแดง -->
              <button
                class="itbms-delete-button btn btn-xs bg-red-700 hover:bg-red-500 text-white px-2 py-1 rounded"
                @click="openDeleteDialog(item.id, item.model)"
                title="Delete"
              >
                D
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div
      v-if="showConfirm"
      class="itbms-message fixed inset-0 flex items-center justify-center bg-black bg-opacity-30 z-50"
    >
      <div class="bg-white p-6 rounded shadow-lg text-center">
        <div class="mb-4">Do you want to delete this sale item?</div>
        <div class="flex justify-center gap-4">
          <button
            class="itbms-confirm-button btn bg-red-700 text-white px-4 py-2 rounded"
            @click="confirmDelete"
          >
            Confirm
          </button>
          <button
            class="itbms-cancel-button btn bg-gray-400 text-white px-4 py-2 rounded"
            @click="cancelDelete"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { fetchSellerSaleItems, deleteSaleItem } from "../services/saleItemService";
import Header from "../components/Header.vue";
import Notification from "../components/Notification.vue";

const saleItems = ref([]);
const allSaleItems = ref([]); // Store all items for filtering
const router = useRouter();
const route = useRoute();
const message = ref("");
const showConfirm = ref(false);
const itemToDelete = ref({ id: null, model: "" });
const loading = ref(false);
const error = ref("");
const searchQuery = ref("");

// Get current user ID from token
const getCurrentUserId = () => {
  const token = sessionStorage.getItem('accessToken');
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

// Get user role from token
const getUserRole = () => {
  const token = sessionStorage.getItem('accessToken');
  if (!token) {
    return null;
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.role || payload.userType;
  } catch (e) {
    console.error('Error decoding token:', e);
    return null;
  }
};

const loadSaleItems = async () => {
  loading.value = true;
  error.value = "";
  
  try {
    const userId = getCurrentUserId();
    if (!userId) {
      error.value = 'Not authenticated';
      router.push({ name: 'login-page' });
      return;
    }

    const response = await fetchSellerSaleItems(userId, 0, 1000);
    allSaleItems.value = response.content || [];
    applySearchFilter();
    console.log(saleItems.value);
  } catch (err) {
    error.value = err.message || 'Failed to load sale items';
    console.error('Error loading sale items:', err);
    
    // Handle specific error cases
    if (err.message.includes('Unauthorized') || err.message.includes('Forbidden')) {
      router.push({ name: 'login-page' });
    }
  } finally {
    loading.value = false;
  }
};

// Filter items based on search query
const applySearchFilter = () => {
  if (!searchQuery.value || searchQuery.value.trim() === '') {
    saleItems.value = allSaleItems.value;
    return;
  }

  const query = searchQuery.value.toLowerCase().trim();
  saleItems.value = allSaleItems.value.filter(item => {
    // Search in model, brand, color, description
    const model = (item.model || '').toLowerCase();
    const brand = (item.brandName || '').toLowerCase();
    const color = (item.color || '').toLowerCase();
    const description = (item.description || '').toLowerCase();
    
    return model.includes(query) || 
           brand.includes(query) || 
           color.includes(query) ||
           description.includes(query);
  });
};

// Handle search updates from Header
const handleSearchUpdate = (query) => {
  searchQuery.value = query || '';
  applySearchFilter();
};

const goToAddSaleItem = () => {
  router.push({ name: "new-sale-item-page" });
};

const goToManageBrand = () => {
  router.push({ name: "brands-list-page" });
};

const goToEdit = (id) => {
  router.push({ name: "sale-items-page-byId", params: { id } });
};

const openDeleteDialog = (id, model) => {
  itemToDelete.value = { id, model };
  showConfirm.value = true;
};

const confirmDelete = async () => {
  try {
    const result = await deleteSaleItem(itemToDelete.value.id);
    if (result.ok) {
      await loadSaleItems();
      message.value = "The sale item has been deleted.";
      setTimeout(() => (message.value = ""), 3000);
    }
  } catch (error) {
    message.value = "Failed to delete sale item";
  } finally {
    showConfirm.value = false;
    itemToDelete.value = { id: null, model: "" };
  }
};

const cancelDelete = () => {
  showConfirm.value = false;
  itemToDelete.value = { id: null, model: "" };
};

onMounted(loadSaleItems);

watch(
  () => route.query.message,
  (newMessage) => {
    if (newMessage) {
      message.value = newMessage;
      // Clear message after 3 seconds
      setTimeout(() => {
        message.value = "";
        // Remove message from URL without refreshing
        router.replace({ query: {} });
      }, 3000);
    }
  },
  { immediate: true }
);
</script>

<style scoped>
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
