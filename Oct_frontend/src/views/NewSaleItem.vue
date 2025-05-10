<template>
  <Header />

  <div class="max-w-4xl mx-auto p-6">
    <div class="flex items-center mb-6">
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link to="/" class="text-blue-600 hover:underline font-medium"
          >Home</router-link
        >
        <span class="mx-1">›</span>
        <span class="font-semibold">New Sale Item</span>
      </nav>
    </div>
    <form
      @submit.prevent="handleSave"
      class="grid grid-cols-1 md:grid-cols-2 gap-8"
    >
      <!-- Left: Picture -->
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
      <!-- Right: Form -->
      <div>
        <div class="mb-3">
          <label class="block mb-1">Brand</label>
          <select
            v-model="form.brandId"
            id="itbms-brand"
            class="w-full border rounded px-2 py-1"
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
            id="itbms-model"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Price (Baht)</label>
          <input
            v-model="form.price"
            id="itbms-price"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Description</label>
          <textarea
            v-model="form.description"
            class="w-full border rounded px-2 py-1"
          ></textarea>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            id="itbms-ramGb"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            id="itbms-screenSizeInch"
            type="number"
            step="0.1"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            id="itbms-storageGb"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            id="itbms-color"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="mb-3">
          <label class="block mb-1">Quantity</label>
          <input
            v-model="form.quantity"
            id="itbms-quantity"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div class="flex gap-4 mt-6">
          <button
            id="itbms-save-button"
            type="submit"
            class="bg-blue-600 text-white px-4 py-2 rounded disabled:bg-gray-400 disabled:cursor-not-allowed"
            :disabled="!isFormValid"
          >
            Save
          </button>
          <button
            id="itbms-cancel-button"
            type="button"
            class="border border-gray-400 px-4 py-2 rounded"
            @click="handleCancel"
          >
            Cancel
          </button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { createSaleItem } from "../services/saleItemService";
import Header from "../components/Header.vue";
import { useRouter } from "vue-router";

const router = useRouter();
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

const isFormValid = computed(() => {
  return (
    form.value.brandId &&
    form.value.model.trim() &&
    form.value.description.trim() &&
    form.value.price &&
    form.value.quantity
  );
});

function handleCancel() {
  form.value = {
    brandId: "",
    model: "",
    price: "",
    description: "",
    ramGb: "",
    screenSizeInch: "",
    storageGb: "",
    color: "",
    quantity: "",
  };
  router.push({ name: "sale-items-page" });
}

async function handleSave() {
  try {
    const brandObj = brands.find((b) => b.id === parseInt(form.value.brandId));
    const dataToSend = {
      model: form.value.model.trim(),
      brand: brandObj ? { id: brandObj.id, name: brandObj.name } : null,
      description: form.value.description.trim(), // Added trim() here
      price: parseInt(form.value.price),
      ramGb: form.value.ramGb ? parseInt(form.value.ramGb) : null,
      screenSizeInch: form.value.screenSizeInch
        ? parseFloat(form.value.screenSizeInch)
        : null,
      quantity: parseInt(form.value.quantity) || 1,
      storageGb: form.value.storageGb ? parseInt(form.value.storageGb) : null,
      color: form.value.color.trim() || null,
    };

    //console.log("ข้อมูลที่จะส่ง:", dataToSend);
    await createSaleItem(dataToSend);
    alert("สร้างรายการขายสำเร็จ!");
    handleCancel();
  } catch (err) {
    alert("เกิดข้อผิดพลาด: " + err);
  }
}
</script>
