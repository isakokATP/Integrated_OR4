<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import FilterGalleryByBrand from "../components/FilterGalleryByBrand.vue";
import Notification from "../components/Notification.vue";
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, watch, computed } from "vue";
import {
  fetchSaleItemsV2,
} from "../services/saleItemService.js";

const router = useRouter();
const route = useRoute();
const message = ref("");
const allItems = ref([]);

// Filter settings state - เปลี่ยนจาก selectedBrands เป็น filterSettings
const filterSettings = ref({
  brands: [],
  priceMin: null,
  priceMax: null,
  storageSizes: []
});

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
  // Save itemsPerPage to session storage
  sessionStorage.setItem("itemsPerPage", option);
  currentPage.value = 1;
  loadSaleItems();
}

function setSort(type) {
  sortType.value = type;
  sessionStorage.setItem("saleListSortType", type); // เก็บค่าใน sessionStorage
  goToPage(1);
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

// Handle filter updates from FilterGalleryByBrand
function handleFilterUpdate(newFilters) {
  filterSettings.value = newFilters;
  // Reset to first page when filters change
  currentPage.value = 1;
  sessionStorage.setItem("currentPage", 1);
  loadSaleItems();
}

// เพิ่ม onMounted hook เพื่อโหลดค่า sortType จาก sessionStorage
onMounted(async () => {
  // โหลดค่า sortType จาก sessionStorage ทุกครั้งที่ mount
  sortType.value = sessionStorage.getItem("saleListSortType") || "default";

  // Load itemsPerPage from session storage
  const savedItemsPerPage = sessionStorage.getItem("itemsPerPage");
  if (savedItemsPerPage) {
    const parsedItemsPerPage = parseInt(savedItemsPerPage);
    if (
      !isNaN(parsedItemsPerPage) &&
      itemsPerPageOptions.value.includes(parsedItemsPerPage)
    ) {
      itemsPerPage.value = parsedItemsPerPage;
    } else {
      // Clear invalid data from session storage
      sessionStorage.removeItem("itemsPerPage");
    }
  }

  // Load currentPage from session storage
  const savedCurrentPage = sessionStorage.getItem("currentPage");
  if (savedCurrentPage) {
    const parsedCurrentPage = parseInt(savedCurrentPage);
    // Check if it's a valid positive integer
    if (!isNaN(parsedCurrentPage) && parsedCurrentPage >= 1) {
      currentPage.value = parsedCurrentPage;
    } else {
      // Clear invalid data from session storage
      sessionStorage.removeItem("currentPage");
    }
  }

  // Load filter settings from session storage
  const savedFilterSettings = sessionStorage.getItem("filterSettings");
  if (savedFilterSettings) {
    try {
      const parsedSettings = JSON.parse(savedFilterSettings);
      if (parsedSettings && typeof parsedSettings === 'object') {
        // Ensure all required properties exist
        const defaultSettings = {
          brands: [],
          priceMin: null,
          priceMax: null,
          storageSizes: []
        };
        const mergedSettings = { ...defaultSettings, ...parsedSettings };
        filterSettings.value = mergedSettings;
      } else {
        console.error("Invalid data in session storage for filterSettings.");
        sessionStorage.removeItem("filterSettings");
      }
    } catch (e) {
      console.error("Failed to parse filterSettings from session storage:", e);
      sessionStorage.removeItem("filterSettings");
    }
  }

  loadSaleItems();
});

// Filter settings are now properly defined above

async function loadSaleItems() {
  try {
    // Temporarily store the requested page before fetching
    const requestedPage = currentPage.value;

    const response = await fetchSaleItemsV2(
      requestedPage, // Use the potentially loaded page
      itemsPerPage.value,
      sortType.value,
      filterSettings.value  // ส่ง filter ทั้งหมดไป Backend
    );
    allItems.value = response.content;
    totalPages.value = response.totalPages;
    totalElements.value = response.totalElements;

    // After fetching totalPages, check if the requested page is still valid
    if (requestedPage > totalPages.value && totalPages.value > 0) {
      // If the requested page is now invalid, revert to the last valid page (totalPages)
      currentPage.value = totalPages.value;
      // Optionally save this change back to session storage
      sessionStorage.setItem("currentPage", totalPages.value);
      // Re-fetch data for the corrected page
      await loadSaleItems(); // Recursive call to fetch the correct page
      return; // Exit the current loadSaleItems call
    } else if (totalPages.value === 0) {
      // If there are no pages, set currentPage to 1 and clear from session storage
      currentPage.value = 1;
      sessionStorage.removeItem("currentPage");
    } else if (requestedPage !== currentPage.value) {
      // If for some other reason currentPage changed during fetch (unlikely but defensive)
      // Ensure session storage is updated with the final valid page
      sessionStorage.setItem("currentPage", currentPage.value);
    }

    displayedPages();
  } catch (error) {
    console.error("Failed to load sale items:", error);
  }
}

// Removed unused computed property - brands are now loaded from API

const filteredItems = computed(() => {
  return allItems.value;
});

const element = ref([]);

function displayedPages() {
  element.value = [];

  if (totalPages.value <= 10) {
    // ถ้าจำนวนหน้าน้อยกว่าหรือเท่ากับ 10 แสดงทุกหน้า
    for (let i = 1; i <= totalPages.value; i++) {
      element.value.push(i);
    }
  } else {
    // ถ้าจำนวนหน้ามากกว่า 10
    let start = Math.max(1, currentPage.value - 4); // ให้หน้าปัจจุบันอยู่ตรงกลาง
    let end = start + 9; // แสดง 10 หน้า (start + 9)

    // ปรับค่าให้ไม่เกินจำนวนหน้าทั้งหมด
    if (end > totalPages.value) {
      end = totalPages.value;
      start = Math.max(1, end - 9);
    }

    for (let i = start; i <= end; i++) {
      element.value.push(i);
    }
  }
  return element.value;
}

function goToPage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    // Save currentPage to session storage
    sessionStorage.setItem("currentPage", page);
    loadSaleItems();
  }
}

function goToNextPage() {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    loadSaleItems();
  }
}

function goToPrevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
    loadSaleItems();
  }
}
</script>

<template>
  <Header />
  <div class="max-w-6xl mx-auto mt-2">
    <Notification :message="message" />

    <!-- ปุ่ม Add -->
    <div class="mb-4">
      <button
        class="itbms-sale-item-add bg-blue-900 hover:bg-blue-500 text-white px-6 py-3 rounded text-lg transition-colors duration-300"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
    </div>

    <!-- Filter + Sort Bar -->
    <div class="flex items-center justify-between mb-6">
      <!-- Filter -->
      <div class="flex-1">
        <FilterGalleryByBrand
          :modelValue="filterSettings"
          @update:modelValue="handleFilterUpdate"
        />
      </div>

      <!-- Size -->
      <div class="flex items-center gap-2 ml-4">
        Show
        <select
          class="itbms-page-size border border-gray-300 rounded-md px-2 py-1"
          v-model="itemsPerPage"
          @change="setItemsPerPage(itemsPerPage)"
        >
          <option
            v-for="option in itemsPerPageOptions"
            :key="option"
            :value="option"
          >
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
    <ItemsGallary :items="filteredItems" />

    <!-- Pagination -->
    <div v-show="totalPages > 1" class="flex justify-center mt-4 px-3 py-1">
      <nav class="bg-gray-200 flex items-center space-x-2">
        <button
          @click="goToPage(1)"
          :disabled="currentPage === 1"
          class="itbms-page-first px-3 py-1 rounded text-gray-700 disabled:opacity-50"
        >
          First
        </button>
        <button
          @click="goToPrevPage"
          :disabled="currentPage === 1"
          class="itbms-page-prev px-3 py-1 text-gray-700 disabled:opacity-50"
        >
          Prev
        </button>
        <template v-for="pageNumber in element" :key="pageNumber">
          <button
            @click="goToPage(pageNumber)"
            :disabled="currentPage === pageNumber"
            :class="[
              `px-3 py-1 rounded itbms-page-${pageNumber - 1}`,
              currentPage === pageNumber
                ? 'bg-blue-500 text-white'
                : 'bg-gray-200 text-gray-700',
            ]"
          >
            {{ pageNumber }}
          </button>
        </template>
        <button
          @click="goToNextPage"
          :disabled="currentPage === totalPages"
          class="itbms-page-next px-3 py-1 rounded text-gray-700 disabled:opacity-50"
        >
          Next
        </button>
        <button
          @click="goToPage(totalPages)"
          :disabled="currentPage === totalPages"
          class="itbms-page-last px-3 py-1 rounded text-gray-700 disabled:opacity-50"
        >
          Last
        </button>
      </nav>
    </div>
  </div>
</template>

<!-- Removed unused CSS animations -->
