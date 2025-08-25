<template>
  <Header />
  <Notification :message="message" />
  <div class="p-6">
    <div class="flex justify-between mb-4">
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link
          to="/sale-items/list"
          class="itbms-item-list text-blue-600 hover:underline font-medium"
          >Sale Item List</router-link
        >
        <span class="mx-1"> › </span>
        <span class="font-semibold"> Brands List </span>
      </nav>
      <div class="flex justify-end">
        <button
          class="itbms-add-button bg-orange-600 hover:bg-orange-400 text-white px-6 py-3 rounded"
          @click="goToAddBrand"
        >
          Add Brand
        </button>
      </div>
    </div>
    <div v-if="Array.isArray(brands) && brands.length === 0">
      <div class="flex justify-center w-full">No Brands Found</div>
    </div>
    <div v-else class="overflow-x-auto mt-10">
      <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-200 text-center">
          <tr>
            <th class="px-3 py-2 border-r border-gray-300 w-16">Id</th>
            <th class="px-3 py-2 border-r border-gray-300 w-[300px]">Name</th>
            <!-- กว้างกว่า และชิดซ้าย -->
            <th class="px-3 py-2 border-r border-gray-300 w-24">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="brand in brands"
            :key="brand.id"
            class="itbms-row border-b border-gray-300 hover:bg-[#E3F2FD] even:bg-gray-100 text-center"
          >
            <td class="itbms-id px-3 py-2 border-r border-gray-300">
              {{ brand.id }}
            </td>
            <td class="itbms-name px-3 py-2 border-r border-gray-300">
              {{ brand.name }}
            </td>
            <!-- ชิดซ้าย -->
            <td
              class="px-3 py-2 border-r border-gray-300 text-center align-middle"
            >
              <div class="flex justify-center items-center gap-2">
                <button
                  class="itbms-edit-button btn btn-xs bg-green-600 hover:bg-green-500 text-white px-3 py-1 rounded w-10"
                  @click="goToEditBrand(brand.id)"
                  title="Edit"
                >
                  E
                </button>
                <button
                  class="itbms-delete-button btn btn-xs bg-red-700 hover:bg-red-500 text-white px-3 py-1 rounded w-10"
                  @click="handleDelete(brand.id, brand.name)"
                  title="Delete"
                >
                  D
                </button>
              </div>
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
        <div class="mb-4">
          <template v-if="errorMessage">
            {{ errorMessage }}
          </template>
          <template v-else-if="cannotDelete">
            Delete {{ brandToDelete.name }} is not allowed. There are sale items
            with {{ brandToDelete.name }} brand.
          </template>
          <template v-else>
            Do you want to delete {{ brandToDelete.name }} brand?
          </template>
        </div>
        <div class="flex justify-center gap-4">
          <button
            v-if="!cannotDelete && !errorMessage"
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
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  fetchBrands,
  deleteBrand,
  fetchSaleItemsV2,
  fetchBrandById,
} from "../services/saleItemService";
import Header from "../components/Header.vue";
import Notification from "../components/Notification.vue";

const brands = ref([]);
const saleItems = ref([]);
const router = useRouter();
const route = useRoute();
const message = ref("");

// สำหรับ dialog
const showConfirm = ref(false);
const brandToDelete = ref({ id: null, name: "" });
const cannotDelete = ref(false);
const errorMessage = ref("");

const loadBrands = async () => {
  brands.value = await fetchBrands();
  console.log(brands.value);
};
const loadSaleItems = async () => {
  const response = await fetchSaleItemsV2(1, 1000, "default", {
    brands: [],
    priceMin: null,
    priceMax: null,
    storageSizes: []
  });
  saleItems.value = response.content;
};

const goToSaleItemList = () => {
  router.push({ name: "sale-items-list-page" });
};

const goToAddBrand = () => {
  router.push({ name: "new-brand-page" });
};

const goToEditBrand = (id) => {
  router.push({ name: "edit-brand-page", params: { id } });
};

const handleDelete = async (id, name) => {
  await loadSaleItems();
  const hasSale = await fetchBrandById(id);
  cannotDelete.value = hasSale.noOfSaleItems > 0;
  brandToDelete.value = { id, name };
  showConfirm.value = true;
};

const confirmDelete = async () => {
  if (cannotDelete.value) return;
  try {
    const result = await deleteBrand(brandToDelete.value.id);
    if (result) {
      await loadBrands();
      showConfirm.value = false;
      brandToDelete.value = { id: null, name: "" };
      cannotDelete.value = false;
      errorMessage.value = "";
      message.value = "The brand has been deleted.";
      setTimeout(() => (message.value = ""), 3000);
    }
  } catch (error) {
    // ตรวจสอบว่าเป็น 404 หรือไม่
    console.log(error);
    if (error === "HTTP error! status: 404") {
      errorMessage.value = "An error has occurred, the brand does not exist.";
      await loadBrands();
    } else {
      errorMessage.value = "Failed to delete brand";
    }
    // ไม่ต้องปิด dialog
  }
};

const cancelDelete = () => {
  showConfirm.value = false;
  brandToDelete.value = { id: null, name: "" };
  cannotDelete.value = false;
  errorMessage.value = "";
  loadBrands(); // เผื่อกรณีข้อมูลเปลี่ยนแปลงระหว่าง dialog เปิด
};

onMounted(async () => {
  await loadBrands();
  await loadSaleItems();
});

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
</script>

<style scoped>
/* TailwindCSS is used, แต่ถ้าต้องการเพิ่มสไตล์อื่นๆ สามารถเพิ่มที่นี่ได้ */
</style>
