<script setup>
import { ref, onMounted, computed, watch } from "vue";
import {
  fetchSaleItemById,
  updateSaleItem,
  deleteSaleItem,
} from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import { fetchBrands } from "@/services/saleItemService";
import Header from "@/components/Header.vue";
import Notification from "@/components/Notification.vue";
const route = useRoute();
const router = useRouter();
const id = route.params.id;
const isLoading = ref(true);
const errorMsg = ref("");
const message = ref("");
const originalData = ref(null); // Store original data for comparison
// เพิ่มตัวแปร brands
const brands = ref([]);

const form = ref({
  brandId: "",
  model: "",
  price: "",
  description: "",
  ramGb: "",
  screenSizeInch: "",
  storageGb: "",
  color: "",
  quantity: "",
});

const hasChanges = computed(() => {
  if (!originalData.value) return false;
  return (
    form.value.brandId !== originalData.value.brandId ||
    form.value.model.trim() !== originalData.value.model.trim() ||
    form.value.price !== originalData.value.price ||
    form.value.description.trim() !== originalData.value.description.trim() ||
    form.value.ramGb !== originalData.value.ramGb ||
    form.value.screenSizeInch !== originalData.value.screenSizeInch ||
    form.value.storageGb !== originalData.value.storageGb ||
    form.value.color.trim() !== originalData.value.color.trim() ||
    form.value.quantity !== originalData.value.quantity
  );
});

const isFormValid = computed(() => {
  return (
    form.value.brandId && // ต้องมี id
    form.value.model.trim() &&
    form.value.description.trim() &&
    form.value.price
  );
});

onMounted(async () => {
  try {
    brands.value = await fetchBrands();
    console.log(brands.value);
    const item = await fetchSaleItemById(id);

    if (item.status === "not_found") {
      errorMsg.value = "The requested sale item does not exist.";
      alert("The requested sale item does not exist.");
      router.push({ name: "sale-items-page" });
      return;
    }

    const brand = brands.value.find((b) => b.name === item.brandName);

    form.value = {
      brandId: brand.id,
      model: item.model,
      price: item.price,
      description: item.description,
      ramGb: item.ramGb,
      screenSizeInch: item.screenSizeInch,
      storageGb: item.storageGb,
      color: item.color,
      quantity: item.quantity,
    };

    // Store original data for comparison
    originalData.value = {
      brand: {
        id: brand ? brand.id : "",
        name: brand ? brand.name : "",
      },
      model: item.model,
      price: item.price,
      description: item.description,
      ramGb: item.ramGb,
      screenSizeInch: item.screenSizeInch,
      storageGb: item.storageGb,
      color: item.color,
      quantity: item.quantity,
    };
  } catch (error) {
    errorMsg.value = "Error loading item: " + error.message;
  } finally {
    isLoading.value = false;
  }
});

// Watch for route changes to get message from query params
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

const handleSave = async () => {
  try {
    const dataToSend = {
      model: form.value.model.trim(),
      brand: {
        id: parseInt(form.value.brand.id),
        name: form.value.brand.name,
      },
      description: form.value.description.trim(),
      price: parseInt(form.value.price),
      ramGb: form.value.ramGb ? parseInt(form.value.ramGb) : null,
      screenSizeInch: form.value.screenSizeInch
        ? parseFloat(form.value.screenSizeInch)
        : null,
      storageGb: form.value.storageGb ? parseInt(form.value.storageGb) : null,
      color: form.value.color.trim() || null,
      quantity: parseInt(form.value.quantity) || 1,
    };

    if (
      !dataToSend.model ||
      !dataToSend.description ||
      !dataToSend.price ||
      !dataToSend.brand.id
    ) {
      throw new Error("Please fill in all required fields");
    }

    await updateSaleItem(id, dataToSend);
    router.push({
      name: "sale-items-page-byId",
      params: { id },
      query: { message: "Item updated successfully!" },
    });
  } catch (err) {
    alert("Error: " + err.message);
  }
};

const handleCancel = () => {
  confirm("Are you sure you want to cancel?") &&
    router.push({ name: "sale-items-page" });
};

const handleDelete = async () => {
  if (confirm("Are you sure you want to delete this item?")) {
    try {
      // เรียก service เพื่อลบ
      await deleteSaleItem(id);
      router.push({
        name: "sale-items-page",
        query: { message: "Item deleted successfully!" },
      });
    } catch (error) {
      alert("Failed to delete item");
    }
  }
};
</script>

<template>
  <Header />
  <Notification :message="message" />
  <div v-if="isLoading">Loading...</div>
  <div v-else-if="errorMsg" class="text-red-600 text-center mt-8">
    {{ errorMsg }}
  </div>
  <div v-else class="max-w-4xl mx-auto p-6">
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items"
        class="text-blue-600 hover:underline font-medium"
        >Home</router-link
      >
      <span class="mx-1">›</span>
      <span class="font-semibold">Edit Sale Item</span>
    </nav>

    <form
      @submit.prevent="handleSave"
      class="grid grid-cols-1 md:grid-cols-2 gap-8"
    >
      <!-- Left: Picture -->
      <div class="mt-2">
        <div
          class="w-70 h-70 bg-gray-100 flex items-center justify-center text-2xl text-gray-400 mb-2 border border-gray-300 rounded"
        >
          No Picture
        </div>
        <div class="flex gap-2 mb-4">
          <div
            v-for="n in 4"
            :key="n"
            class="w-16 h-16 bg-gray-100 flex items-center justify-center text-gray-400 text-xs border border-gray-300 rounded"
          >
            No Picture
          </div>
        </div>
      </div>

      <!-- Right: Form -->
      <div>
        <div class="mb-3">
          <label class="block mb-1">Brand</label>
          <select
            v-model="form.brandId"
            class="itbms-brand w-full border rounded px-2 py-1"
          >
            <option value="">Select Brand</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Model</label>
          <input
            v-model="form.model"
            v-trim
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Price (Baht)</label>
          <input
            v-model="form.price"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Description</label>
          <textarea
            v-model="form.description"
            v-trim
            class="w-full border rounded px-2 py-1"
          ></textarea>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            type="number"
            step="0.1"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            v-trim
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Quantity</label>
          <input
            v-model="form.quantity"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="flex gap-4 mt-6">
          <button
            id="itbms-save-button"
            type="submit"
            class="bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-500 transition-colors duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
            :disabled="!isFormValid || !hasChanges"
          >
            Save
          </button>
          <button
            id="itbms-cancel-button"
            type="button"
            class="border border-gray-400 px-4 py-2 rounded hover:bg-blue-200 transition-colors duration-300"
            @click="handleCancel"
          >
            Cancel
          </button>
        </div>
      </div>
    </form>

    <button
      class="bg-red-700 text-white px-4 py-2 rounded hover:bg-red-500"
      @click="handleDelete"
    >
      Delete
    </button>
  </div>
</template>
