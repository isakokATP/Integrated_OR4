<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import FilterGalleryByBrand from "../components/FilterGalleryByBrand.vue";
import Notification from "../components/Notification.vue";
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, watch, computed } from "vue";
import {
  fetchSaleItems,
  fetchSaleItemsV2,
} from "../services/saleItemService.js";
import { onBeforeMount } from "vue";

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
  sessionStorage.setItem("saleListSortType", type); // เก็บค่าใน sessionStorage
  loadSaleItems();
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

// Watch for changes in selectedBrands and reload items
watch(selectedBrands, () => {
  currentPage.value = 1; // Reset to first page when filters change
  loadSaleItems();
});

// เพิ่ม onMounted hook เพื่อโหลดค่า sortType จาก sessionStorage
onMounted(() => {
  // โหลดค่า sortType จาก sessionStorage ทุกครั้งที่ mount
  sortType.value = sessionStorage.getItem("saleListSortType") || "default";

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

    displayedPages();
  } catch (error) {
    console.error("Failed to load sale items:", error);
  } finally {
    loading.value = false;
  }
}

const brands = computed(() => {
  // Use allItems to extract brands, even if filtered/paginated data is displayed
  const set = new Set(allItems.value.map((item) => item.brandName));
  return [...set];
});

const filteredItems = computed(() => {
  let sortedItems = [...allItems.value];

  // เรียงตาม sortType
  switch (sortType.value) {
    case "asc":
      // เรียงแบรนด์ A-Z แล้วเรียง id ในแต่ละแบรนด์
      sortedItems.sort((a, b) => {
        const brandCompare = a.brandName.localeCompare(b.brandName);
        return brandCompare !== 0 ? brandCompare : a.id - b.id;
      });

      break;
    case "desc":
      // เรียงแบรนด์ Z-A แล้วเรียง id ในแต่ละแบรนด์
      sortedItems.sort((a, b) => {
        const brandCompare = b.brandName.localeCompare(a.brandName);
        return brandCompare !== 0 ? brandCompare : a.id - b.id;
      });

      break;

    default:
      // default เรียงตาม id
      sortedItems.sort((a, b) => a.id - b.id);
  }

  return sortedItems;
});

const startPage = ref(1);
const windowsize = ref(10); // กำหนดให้แสดง 10 หน้า
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
        <FilterGalleryByBrand v-model="selectedBrands" :brands="brands" />
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
    <ItemsGallary :items="filteredItems" :loading="loading" />

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center mt-4 px-3 py-1">
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
