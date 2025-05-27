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

const startPage = ref(1);
const endPage = ref(10);

const displayedPages = computed(() => {
  // ใช้ค่า startPage และ endPage ในการสร้าง array ของหน้า
  const pages = [];
  const actualStart = Math.max(1, startPage.value);
  const actualEnd = Math.min(totalPages.value, endPage.value);

  if (actualStart > actualEnd) {
    // กรณี totalPages น้อยมาก หรือไม่มีหน้าเลย
    if (totalPages.value >= 1) return [1];
    return []; // หรือ [] ถ้าไม่มีหน้าเลย
  }

  for (let i = actualStart; i <= actualEnd; i++) {
    pages.push(i);
  }

  // กรณี totalPages เป็น 0 หรือ 1 และ window ไม่ได้แสดง 1
  if (totalPages.value >= 1 && pages.length === 0 && currentPage.value >= 1) {
    return [1];
  }

  return pages;
});

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
    const windowSize = 10;
    // ถ้าหน้าปัจจุบันเกิน endPage และ endPage ยังไม่ถึงหน้าสุดท้าย ให้เลื่อน window
    if (currentPage.value > endPage.value && endPage.value < totalPages.value) {
      let newStart = currentPage.value - Math.floor(windowSize / 2);
      newStart = Math.max(1, newStart); // ตรวจสอบไม่ให้ start น้อยกว่า 1
      let newEnd = newStart + windowSize - 1;
      newEnd = Math.min(totalPages.value, newEnd); // ตรวจสอบไม่ให้ end เกิน totalPages

      // ปรับ start อีกครั้งเผื่อ end ชนขอบ totalPages
      if (newEnd === totalPages.value) {
        newStart = Math.max(1, newEnd - windowSize + 1);
      }
      startPage.value = newStart;
      endPage.value = newEnd;
    }
    // ถ้าหน้าปัจจุบันชน endPage และ endPage ยังไม่ถึงหน้าสุดท้าย ให้ขยับ window
    else if (
      currentPage.value === endPage.value &&
      endPage.value < totalPages.value
    ) {
      startPage.value++;
      endPage.value++;
    }

    loadSaleItems();
  }
};

const goToPrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    const windowSize = 10;
    // ถ้าหน้าปัจจุบันน้อยกว่า startPage และ startPage ยังไม่ถึงหน้าแรก ให้เลื่อน window
    if (currentPage.value < startPage.value && startPage.value > 1) {
      let newEnd = currentPage.value + Math.floor(windowSize / 2);
      newEnd = Math.min(totalPages.value, newEnd); // ตรวจสอบไม่ให้ end เกิน totalPages
      let newStart = newEnd - windowSize + 1;
      newStart = Math.max(1, newStart); // ตรวจสอบไม่ให้ start น้อยกว่า 1

      // ปรับ end อีกครั้งเผื่อ start ชนขอบ 1
      if (newStart === 1) {
        newEnd = Math.min(totalPages.value, newStart + windowSize - 1);
      }
      startPage.value = newStart;
      endPage.value = newEnd;
    }
    // ถ้าหน้าปัจจุบันชน startPage และ startPage ยังไม่ถึงหน้าแรก ให้ขยับ window
    else if (currentPage.value === startPage.value && startPage.value > 1) {
      startPage.value--;
      endPage.value--;
    }

    loadSaleItems();
  }
};

const goToPage = (page) => {
  // การกดปุ่มหมายเลขหน้าโดยตรง ไม่ทำให้ window เลื่อน
  // แค่เปลี่ยนหน้าปัจจุบันและโหลดข้อมูล
  currentPage.value = page;
  loadSaleItems();
};

// ปรับ loadSaleItems เพื่อกำหนดค่า startPage และ endPage เริ่มต้นและเมื่อ totalPages เปลี่ยน
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
    const oldTotalPages = totalPages.value; // เก็บค่า totalPages เก่า
    totalPages.value = response.totalPages;
    totalElements.value = response.totalElements;

    const windowSize = 10;

    // ตรวจสอบว่าต้องปรับ window displayedPages หรือไม่
    // 1. ถ้า totalPages เปลี่ยน หรือ
    // 2. ถ้า currentPage อยู่นอก window ปัจจุบัน หรือ
    // 3. นี่คือการโหลดครั้งแรก (totalElements.value ก่อนหน้านี้เป็น 0 หรือยังไม่ได้โหลด)
    const shouldAdjustWindow =
      oldTotalPages !== totalPages.value ||
      currentPage.value < startPage.value ||
      currentPage.value > endPage.value ||
      totalElements.value === 0;

    if (shouldAdjustWindow) {
      if (totalPages.value <= windowSize) {
        // ถ้าจำนวนหน้าน้อยกว่าหรือเท่ากับ windowSize ให้แสดงทั้งหมด
        startPage.value = 1;
        endPage.value = totalPages.value > 0 ? totalPages.value : 1; // กำหนดให้เป็น 1 ถ้า totalPages เป็น 0
      } else {
        // ถ้าจำนวนหน้ามากกว่า windowSize ให้คำนวณ window ที่มี currentPage อยู่ตรงกลาง
        const middleOfWindow = Math.floor(windowSize / 2);
        let newStart = Math.max(1, currentPage.value - middleOfWindow);
        let newEnd = newStart + windowSize - 1;

        // ปรับ end ถ้าเกิน totalPages
        if (newEnd > totalPages.value) {
          newEnd = totalPages.value;
          newStart = Math.max(1, newEnd - windowSize + 1); // ปรับ start กลับ
        }

        // ปรับ start ถ้าน้อยกว่า 1 (เกิดจาก newEnd ไปชน totalPages)
        if (newStart < 1) {
          newStart = 1;
          newEnd = Math.min(totalPages.value, newStart + windowSize - 1);
        }

        startPage.value = newStart;
        endPage.value = newEnd;
      }
    }
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
    <div
      v-if="totalPages > 1"
      class="flex justify-center mt-4 px-3 py-1"
    >
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
        <template v-for="pageNumber in displayedPages" :key="pageNumber">
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
