<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import FilterGalleryByBrand from "../components/FilterGalleryByBrand.vue";
import Notification from "../components/Notification.vue";
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, watch, computed } from "vue";
import { fetchSaleItems, fetchSaleItemsV2 } from "../services/saleItemService.js";

const router = useRouter();
const route = useRoute();
const message = ref("");
const allItems = ref([]);
const loading = ref(false);
const selectedBrands = ref([]); // สำหรับเก็บแบรนด์ที่ถูกเลือก

// เพิ่ม state สำหรับ sortType และโหลดค่าจาก sessionStorage
const sortType = ref(sessionStorage.getItem("saleListSortType") || "default");

// Pagination state
const currentPage = ref(1);
const itemsPerPage = ref(10); // จำนวนรายการต่อหน้า
const totalPages = ref(0);
const totalElements = ref(0);

const itemsPerPageOptions = ref([5, 10, 20]);

function setItemsPerPage(option) {
  itemsPerPage.value = option;
  loadSaleItems();
}

function setSort(type) {
  sortType.value = type;
  sessionStorage.setItem("saleListSortType", type);
  loadSaleItems(); // โหลดข้อมูลใหม่เมื่อมีการเปลี่ยนการเรียงลำดับ
}

function goToAddSaleItem() {
  router.push({ name: "new-sale-item-page" });
}

// ดึง message จาก query string
watch(
  () => route.query.message,
  (newMessage) => {
    if (newMessage) {
      message.value = newMessage;
      setTimeout(() => {
        message.value = "";
        router.replace({ query: {} });
      }, 3000);
    }
  },
  { immediate: true }
);

// Watch for changes in selectedBrands and reload items
watch(selectedBrands, () => {
  currentPage.value = 1; // Reset to first page when filters change
  loadSaleItems();
});

async function loadSaleItems() {
  loading.value = true;
  try {
    const response = await fetchSaleItemsV2(
      currentPage.value,
      itemsPerPage.value,
      sortType.value,
      selectedBrands.value
    );
    allItems.value = response.content;
    totalPages.value = response.totalPages;
    totalElements.value = response.totalElements;
  } catch (error) {
    console.error("Failed to load sale items:", error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  // โหลดค่า sortType จาก sessionStorage ทุกครั้งที่ mount
  sortType.value = sessionStorage.getItem("saleListSortType") || "default";
  loadSaleItems();
});

const brands = computed(() => {
  // Use allItems to extract brands, even if filtered/paginated data is displayed
  const set = new Set(allItems.value.map((item) => item.brandName));
  return [...set];
});

const filteredItems = computed(() => {
  // With backend filtering, filteredItems just reflects the current allItems
  return allItems.value;
});

// ฟังก์ชันสำหรับเปลี่ยนหน้า
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    loadSaleItems(); // โหลดข้อมูลใหม่เมื่อมีการเปลี่ยนหน้า
  }
};
</script>

<template>
  <Header />
  <div class="max-w-6xl mx-auto mt-2">
    <Notification :message="message" />

    <!-- ปุ่ม Add -->
    <div class="mb-4">
      <button
        id="itbms-add-sale-item-button"
        class="bg-blue-900 hover:bg-blue-500 text-white px-6 py-3 rounded text-lg transition-colors duration-300"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
    </div>

    <!-- Filter + Sort Bar -->
    <div class="flex items-center justify-between mb-6">
      <!-- Filter -->
      <div class="flex-1">
        <FilterGalleryByBrand v-model="selectedBrands" :brands="brands" />
      </div>
      
      <!-- Size -->
      <div class="flex items-center gap-2 ml-4">
        Show 
        <select class="border border-gray-300 rounded-md px-2 py-1" v-model="itemsPerPage" @change="setItemsPerPage(itemsPerPage)">
          <option v-for="option in itemsPerPageOptions" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
      </div>
      <!-- Sort Buttons -->
      <div class="flex items-center gap-2 ml-4">
        <button
          :class="
            sortType === 'asc'
              ? 'bg-blue-500 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="itbms-brand-asc p-2 rounded transition-colors"
          @click="setSort('asc')"
          title="Sort by Brand A-Z"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M3 16h18M3 12h12M3 8h6"
            />
          </svg>
        </button>
        <button
          :class="
            sortType === 'desc'
              ? 'bg-blue-500 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="itbms-brand-desc p-2 rounded transition-colors"
          @click="setSort('desc')"
          title="Sort by Brand Z-A"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M3 8h18M3 12h12M3 16h6"
            />
          </svg>
        </button>
        <button
          :class="
            sortType === 'default'
              ? 'bg-blue-500 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="itbms-brand-none p-2 rounded transition-colors"
          @click="setSort('default')"
          title="No Sort"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>
    </div>

    <!-- แสดงรายการสินค้า -->
    <ItemsGallary :items="filteredItems" :loading="loading" />

    

    <!-- Pagination -->
    <div class="flex justify-center mt-4">
      <nav class="flex items-center space-x-2">
        <button
          @click="goToPage(1)"
          :disabled="currentPage === 1"
          class="px-3 py-1 rounded bg-gray-200 text-gray-700 disabled:opacity-50"
        >
          First
        </button>
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 1"
          class="px-3 py-1 rounded bg-gray-200 text-gray-700 disabled:opacity-50"
        >
          Prev
        </button>
        <button
          v-for="pageNumber in totalPages"
          :key="pageNumber"
          @click="goToPage(pageNumber)"
          :class="[
            'px-3 py-1 rounded',
            currentPage === pageNumber
              ? 'bg-blue-500 text-white'
              : 'bg-gray-200 text-gray-700',
          ]"
        >
          {{ pageNumber }}
        </button>
        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="px-3 py-1 rounded bg-gray-200 text-gray-700 disabled:opacity-50"
        >
          Next
        </button>
        <button
          @click="goToPage(totalPages)"
          :disabled="currentPage === totalPages"
          class="px-3 py-1 rounded bg-gray-200 text-gray-700 disabled:opacity-50"
        >
          Last
        </button>
      </nav>
    </div>
  </div>
</template>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
