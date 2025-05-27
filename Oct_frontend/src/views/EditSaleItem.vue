<script setup>
import { ref, onMounted, computed, watch } from "vue";
import {
  fetchSaleItemById,
  updateSaleItem,
  deleteSaleItem,
  fetchBrands,
} from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";
import Notification from "@/components/Notification.vue";

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const isLoading = ref(true);
const errorMsg = ref("");
const message = ref("");
const brands = ref([]);
const originalData = ref(null);

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

const touched = ref({
  brandId: false,
  model: false,
  price: false,
  description: false,
  ramGb: false,
  screenSizeInch: false,
  storageGb: false,
  color: false,
  quantity: false,
});

const errors = computed(() => {
  const e = {};
  if (touched.value.brandId && !form.value.brandId)
    e.brandId = "* Brand must be selected.";
  if (
    touched.value.model &&
    (form.value.model.trim().length < 1 || form.value.model.length > 60)
  )
    e.model = "* Model must be 1-60 characters long.";
  if (
    touched.value.description &&
    (form.value.description.trim().length < 1 ||
      form.value.description.length > 16384)
  )
    e.description = "* Description must be 1-16,384 characters long.";
  if (
    touched.value.price &&
    (!/^[0-9]+$/.test(form.value.price) || parseInt(form.value.price) < 1)
  )
    e.price = "* Price must be a positive integer.";
  if (
    touched.value.quantity &&
    form.value.quantity !== "" &&
    (!/^[0-9]+$/.test(form.value.quantity) || parseInt(form.value.quantity) < 0)
  )
    e.quantity = "* Quantity must be non-negative integer.";
  if (
    touched.value.ramGb &&
    form.value.ramGb !== "" &&
    (!/^[0-9]+$/.test(form.value.ramGb) || parseInt(form.value.ramGb) <= 0)
  )
    e.ramGb = "* RAM size must be positive integer or not specified.";
  if (touched.value.screenSizeInch && form.value.screenSizeInch !== "") {
    const val = parseFloat(form.value.screenSizeInch);
    if (
      isNaN(val) ||
      val <= 0 ||
      val >= 100 ||
      !/^\d+(\.\d{1,2})?$/.test(form.value.screenSizeInch)
    ) {
      e.screenSizeInch =
        "* Screen size must be in the range of units to tens with at most 2 decimal points or not specified.";
    }
  }
  if (
    touched.value.storageGb &&
    form.value.storageGb !== "" &&
    (!/^[0-9]+$/.test(form.value.storageGb) ||
      parseInt(form.value.storageGb) <= 0)
  )
    e.storageGb = "* Storage size must be positive integer or not specified.";
  if (
    touched.value.color &&
    form.value.color &&
    (form.value.color.length < 1 || form.value.color.length > 40)
  )
    e.color = "* Color must be 1-40 characters long or not specified.";
  return e;
});

const mandatoryValid = computed(() => {
  return (
    form.value.brandId &&
    form.value.model.trim() &&
    form.value.description.trim() &&
    /^[0-9]+$/.test(form.value.price)
  );
});

const isFormValid = computed(() => {
  return Object.keys(errors.value).length === 0 && mandatoryValid.value;
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

const updateError = () => {
  touched.value = { ...touched.value };
};

onMounted(async () => {
  try {
    brands.value = await fetchBrands();
    const item = await fetchSaleItemById(id);

    if (item.status === "not_found") {
      errorMsg.value = "The requested sale item does not exist.";
      alert(errorMsg.value);
      router.push({ name: "sale-items-page" });
      return;
    }

    const brand = brands.value.find((b) => b.name === item.brandName);

    form.value = {
      brandId: brand?.id || "",
      model: item.model,
      price: item.price,
      description: item.description,
      ramGb: item.ramGb,
      screenSizeInch: item.screenSizeInch,
      storageGb: item.storageGb,
      color: item.color,
      quantity: item.quantity,
    };

    originalData.value = { ...form.value };
  } catch (error) {
    errorMsg.value = "Error loading item: " + error.message;
  } finally {
    isLoading.value = false;
  }
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

const handleSave = async () => {
  try {
    if (!isFormValid.value) {
      alert("Please correct validation errors.");
      return;
    }

    const dataToSend = {
      model: form.value.model.trim(),
      brand: {
        id: parseInt(form.value.brandId),
        name:
          brands.value.find((b) => b.id === parseInt(form.value.brandId))
            ?.name || "",
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
  if (confirm("Are you sure you want to cancel?")) {
    router.push({ name: "sale-items-page" });
  }
};

const handleDelete = async () => {
  if (confirm("Are you sure you want to delete this item?")) {
    try {
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

      <div>
        <div class="mb-3">
          <label class="block mb-1">
            Brand<span class="text-red-500"> *</span>
          </label>
          <select
            v-model="form.brandId"
            @blur="
              touched.brandId = true;
              updateError();
            "
            class="itbms-brand w-full border rounded px-2 py-1"
          >
            <option value="">Select Brand</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
          <div v-if="errors.brandId" class="text-red-600 text-sm mt-1">
            {{ errors.brandId }}
          </div>
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Model<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.model"
            @focus="() => (touched.model = true)"
            @blur="
              touched.model = true;
              form.model = form.model.trim();
              updateError();
            "
            class="w-full border rounded px-2 py-1"
          />
          <div v-if="errors.model" class="text-red-600 text-sm mt-1">
            {{ errors.model }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1"
            >Price (Baht)<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.price"
            @focus="() => (touched.price = true)"
            step="1"
            @blur="
              touched.price = true;
              updateError();
            "
            type="number"
            class="w-full border rounded px-2 py-1"
          />
          <div v-if="errors.price" class="text-red-600 text-sm mt-1">
            {{ errors.price }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1"
            >Description<span class="text-red-500"> *</span></label
          >
          <textarea
            v-model="form.description"
            @focus="() => (touched.description = true)"
            @blur="
              touched.description = true;
              form.description = form.description.trim();
              updateError();
            "
            class="w-full border rounded px-2 py-1"
          ></textarea>
          <div v-if="errors.description" class="text-red-600 text-sm mt-1">
            {{ errors.description }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            @focus="() => (touched.ramGb = true)"
            @blur="touched.ramGb = true"
            @input="touched.ramGb = true"
            type="number"
            class="w-full border rounded px-2 py-1"
          />
          <span v-if="errors.ramGb" class="text-red-500 text-sm">{{
            errors.ramGb
          }}</span>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            @focus="() => (touched.screenSizeInch = true)"
            type="number"
            step="0.01"
            @blur="touched.screenSizeInch = true"
            @input="touched.screenSizeInch = true"
            class="w-full border rounded px-2 py-1"
          />
          <span v-if="errors.screenSizeInch" class="text-red-500 text-sm">{{
            errors.screenSizeInch
          }}</span>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            type="number"
            @blur="touched.storageGb = true"
            @input="touched.storageGb = true"
            step="1"
            class="w-full border rounded px-2 py-1"
          />
          <span v-if="errors.storageGb" class="text-red-500 text-sm">{{
            errors.storageGb
          }}</span>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            v-trim
            @blur="touched.color = true"
            @input="touched.color = true"
            class="w-full border rounded px-2 py-1"
          />
          <span v-if="errors.color" class="text-red-500 text-sm">{{
            errors.color
          }}</span>
        </div>
        <div class="mb-3">
          <label class="block mb-1"
            >Quantity<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.quantity"
            type="number"
            @blur="touched.quantity = true"
            @input="touched.quantity = true"
            class="w-full border rounded px-2 py-1"
          />
          <span v-if="errors.quantity" class="text-red-500 text-sm">{{
            errors.quantity
          }}</span>
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
      class="bg-red-700 text-white px-4 py-2 rounded hover:bg-red-500 mt-6"
      @click="handleDelete"
    >
      Delete
    </button>
  </div>
</template>
