<script setup>
import { ref, onMounted, computed } from "vue";
import { fetchSaleItemById, updateSaleItem } from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";

const route = useRoute();
const router = useRouter();
const id = route.params.id;
const isLoading = ref(true);
const errorMsg = ref("");
const originalData = ref(null);
const successMsg = ref("");

const brands = [
  { id: 1, name: "Samsung" },
  { id: 2, name: "Apple" },
  { id: 3, name: "Xiaomi" },
  { id: 4, name: "Huawei" },
  { id: 5, name: "OnePlus" },
  { id: 6, name: "Sony" },
  { id: 7, name: "LG" },
  { id: 8, name: "Nokia" },
  { id: 9, name: "Motorola" },
  { id: 10, name: "OPPO" },
  { id: 11, name: "Vivo" },
  { id: 12, name: "ASUS" },
  { id: 13, name: "Google" },
  { id: 14, name: "Realme" },
  { id: 15, name: "BlackBerry" },
  { id: 16, name: "HTC" },
  { id: 17, name: "ZTE" },
  { id: 18, name: "Lenovo" },
  { id: 19, name: "Honor" },
  { id: 20, name: "Nothing" },
];

const form = ref({
  brand: {
    id: "",
    name: "",
  },
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
    form.value.brand.id !== originalData.value.brand.id ||
    form.value.model.trim() !== originalData.value.model.trim() ||
    form.value.price !== originalData.value.price ||
    form.value.description.trim() !== originalData.value.description.trim() ||
    form.value.ramGb !== originalData.value.ramGb ||
    form.value.screenSizeInch !== originalData.value.screenSizeInch ||
    form.value.storageGb !== originalData.value.storageGb ||
    (form.value.color || " ").trim() !==
      (originalData.value.color || " ").trim() ||
    form.value.quantity !== originalData.value.quantity
  );
});

const isFormValid = computed(() => {
  return (
    form.value.brand.id && // ต้องมี id
    form.value.model.trim() &&
    form.value.description.trim() &&
    form.value.price &&
    form.value.quantity
  );
});

onMounted(async () => {
  try {
    const item = await fetchSaleItemById(id);

    if (item.status === "not_found") {
      errorMsg.value = "The requested sale item does not exist.";
      alert("The requested sale item does not exist.");
      router.push({ name: "sale-items-page" });
      return;
    }

    const brand = brands.find((b) => b.name === item.brandName);

    form.value = {
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

const handleSave = async () => {
  try {
    const dataToSend = {
      model:
        form.value.model && form.value.model.trim()
          ? form.value.model.trim()
          : null,
      brand: {
        id: parseInt(form.value.brand.id),
        name: form.value.brand.name,
      },
      description:
        form.value.description && form.value.description.trim()
          ? form.value.description.trim()
          : null,
      price: parseInt(form.value.price),
      ramGb: form.value.ramGb ? parseInt(form.value.ramGb) : null,
      screenSizeInch: form.value.screenSizeInch
        ? parseFloat(form.value.screenSizeInch)
        : null,
      quantity: parseInt(form.value.quantity) || 1,
      storageGb: form.value.storageGb ? parseInt(form.value.storageGb) : null,
      color:
        form.value.color && form.value.color.trim()
          ? form.value.color.trim()
          : null,
    };

    if (
      !dataToSend.model ||
      !dataToSend.description ||
      !dataToSend.price ||
      !dataToSend.brand.id
    ) {
      throw new Error("Please fill in all required fields");
    }

    console.log("Updating item with ID:", id);
    const response = await updateSaleItem(id, dataToSend);
    console.log("Update response:", response);

    if (response) {
      console.log("Redirecting to details page with ID:", id);
      router.push({
        name: "sale-items-page-byId",
        params: { id: id },
        query: { msg: "success", source: "edit" },
      });
    } else {
      throw new Error("Failed to update sale item");
    }
  } catch (err) {
    console.error("Error in handleSave:", err);
    alert("Error: " + err.message);
  }
};

const handleCancel = () => {
  confirm("Are you sure you want to cancel?") &&
    router.push({ name: "sale-items-page" });
};
</script>

<template>
  <Header />
  <div v-if="isLoading">Loading...</div>
  <div v-else-if="errorMsg" class="text-red-600 text-center mt-8">
    {{ errorMsg }}
  </div>
  <div v-else class="max-w-4xl mx-auto p-6">
    <div
      v-if="successMsg"
      class="itbms-message text-green-600 text-center mb-4"
    >
      {{ successMsg }}
    </div>
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
      <div>
        <div
          class="w-64 h-64 bg-gray-100 flex items-center justify-center text-2xl text-gray-400 mb-4"
        >
          No Picture
        </div>
        <div class="flex gap-2 mb-4">
          <div
            v-for="n in 4"
            :key="n"
            class="w-16 h-16 bg-gray-100 flex items-center justify-center text-gray-400 text-xs"
          >
            No Picture
          </div>
        </div>
      </div>

      <div>
        <div class="mb-3">
          <label class="block mb-1">Brand</label>
          <select
            v-model="form.brand.id"
            class="itbms-brand w-full border rounded px-2 py-1"
            @change="
              form.brand.name =
                brands.find((b) => b.id == form.brand.id)?.name || ''
            "
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
            class="itbms-model w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Price (Baht)</label>
          <input
            v-model="form.price"
            type="number"
            class="itbms-price w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Description</label>
          <textarea
            v-model="form.description"
            class="itbms-description w-full border rounded px-2 py-1"
          ></textarea>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            type="number"
            class="itbms-ramGb w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            type="number"
            step="0.1"
            class="itbms-screenSizeInch w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            type="number"
            class="itbms-storageGb w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            class="itbms-color w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Quantity</label>
          <input
            v-model="form.quantity"
            type="number"
            class="itbms-quantity w-full border rounded px-2 py-1"
          />
        </div>
        <div class="flex gap-4 mt-6">
          <button
            type="submit"
            class="itbms-save-button bg-blue-600 text-white px-4 py-2 rounded disabled:bg-gray-400 disabled:cursor-not-allowed"
            :disabled="!hasChanges || !isFormValid"
          >
            Save
          </button>
          <button
            type="button"
            class="itbms-cancel-button border border-gray-400 px-4 py-2 rounded"
            @click="handleCancel"
          >
            Cancel
          </button>
        </div>
      </div>
    </form>
  </div>
</template>
