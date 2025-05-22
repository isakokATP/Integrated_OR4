<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import FilterGalleryByBrand from "../components/FilterGalleryByBrand.vue";
import Notification from "../components/Notification.vue";
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, watch, computed } from "vue";
import { fetchSaleItems } from "../services/saleItemService.js";

const router = useRouter();
const route = useRoute();
const message = ref("");
const allItems = ref([]);
const loading = ref(false);
const selectedBrands = ref([]); // สำหรับเก็บแบรนด์ที่ถูกเลือก

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

async function loadSaleItems() {
  loading.value = true;
  try {
    allItems.value = await fetchSaleItems();
  } catch (error) {
    console.error("Failed to load sale items:", error);
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  loading.value = true;
  try {
    allItems.value = await fetchSaleItems();
    console.log("Loaded items:", allItems.value);
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
});

const brands = computed(() => {
  const set = new Set(allItems.value.map((item) => item.brandName));
  return [...set];
});

const filteredItems = computed(() => {
  if (selectedBrands.value.length === 0) {
    return allItems.value;
  }
  return allItems.value.filter((item) =>
    selectedBrands.value.includes(item.brandName)
  );
});
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

    <!-- Filter ด้านล่างปุ่ม Add -->
    <div class="mb-6">
      <FilterGalleryByBrand v-model="selectedBrands" :brands="brands" />
    </div>

    <!-- แสดงรายการสินค้า -->
    <ItemsGallary :items="filteredItems" :loading="loading" />
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
