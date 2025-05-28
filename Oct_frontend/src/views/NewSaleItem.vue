<template>
  <Header />

  <div class="max-w-4xl mx-auto p-6">
    <div class="flex items-center mb-6">
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link
          to="/sale-items"
          class="itbms-home text-blue-600 hover:underline font-medium"
          >Home</router-link
        >
        <span class="mx-1">›</span>
        <span class="itbms-new-sale-item font-semibold">New Sale Item</span>
      </nav>
    </div>
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
            @blur="handleBlur('brandId')"
          >
            <option value="">Select Brand</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
          <div
            v-if="fieldErrors.brandId"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.brandId }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Model</label>
          <input
            v-model="form.model"
            v-trim
            @blur="handleBlur('model')"
            class="itbms-model w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.model"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.model }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Price (Baht)</label>
          <input
            v-model="form.price"
            type="number"
            @blur="handleBlur('price')"
            class="itbms-price w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.price"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.price }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Description</label>
          <textarea
            v-model="form.description"
            v-trim
            @blur="handleBlur('description')"
            class="itbms-description w-full border rounded px-2 py-1"
          ></textarea>
          <div
            v-if="fieldErrors.description"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.description }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            type="number"
            @blur="handleBlur('ramGb')"
            class="itbms-ramGb w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.ramGb"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.ramGb }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            type="number"
            step="0.01"
            @blur="handleBlur('screenSizeInch')"
            class="itbms-screenSizeInch w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.screenSizeInch"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.screenSizeInch }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            type="number"
            @blur="handleBlur('storageGb')"
            class="itbms-storageGb w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.storageGb"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.storageGb }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            v-trim
            @blur="handleBlur('color')"
            class="itbms-color w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.color"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.color }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1">Quantity</label>
          <input
            v-model="form.quantity"
            type="number"
            @blur="handleBlur('quantity')"
            class="itbms-quantity w-full border rounded px-2 py-1"
          />
          <div
            v-if="fieldErrors.quantity"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.quantity }}
          </div>
        </div>
        <div class="flex gap-4 mt-6">
          <button
            type="submit"
            class="itbms-save-button btn text-white bg-blue-900 hover:bg-blue-500"
            :disabled="!isFormValid"
          >
            Save
          </button>
          <button
            type="button"
            class="itbms-cancel-button border border-gray-400 px-4 py-2 rounded hover:bg-blue-200 transition-colors duration-300"
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
import { ref, computed, onMounted, reactive } from "vue";
import { createSaleItem, fetchBrands } from "../services/saleItemService";
import Header from "../components/Header.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const brands = ref([]);

const loadBrands = async () => {
  try {
    brands.value = await fetchBrands();
  } catch (error) {
    console.error("Error loading brands:", error);
  }
};

onMounted(loadBrands);

const form = ref({
  brandId: "",
  model: "",
  price: "",
  description: "",
  ramGb: "",
  screenSizeInch: "",
  storageGb: "",
  color: "",
  quantity: 1,
});

const fieldErrors = reactive({});

// Function to validate a specific field
function validateField(field) {
  let isValid = true;
  fieldErrors[field] = ""; // Clear previous error for this field

  switch (field) {
    case "brandId":
      if (!form.value.brandId) {
        fieldErrors.brandId = "Brand must be selected.";
        isValid = false;
      }
      break;
    case "model":
      const trimmedModel = form.value.model.trim();
      if (!trimmedModel || trimmedModel.length > 60) {
        fieldErrors.model = "Model must be 1-60 characters long.";
        isValid = false;
      }
      break;
    case "description":
      const trimmedDescription = form.value.description.trim();
      if (!trimmedDescription || trimmedDescription.length > 65535) {
        fieldErrors.description =
          "Description must be 1-16,384 characters long.";
        isValid = false;
      }
      break;
    case "price":
      const price = parseInt(form.value.price);
      if (isNaN(price) || price < 0) {
        fieldErrors.price = "Price must be non-negative integer.";
        isValid = false;
      }
      break;
    case "quantity":
      const quantity = parseInt(form.value.quantity);
      if (isNaN(quantity) || quantity < 0) {
        fieldErrors.quantity = "Quantity must be non-negative integer.";
        isValid = false;
      }
      break;
    case "screenSizeInch":
      if (form.value.screenSizeInch) {
        const screenSize = parseFloat(form.value.screenSizeInch);
        if (
          isNaN(screenSize) ||
          screenSize <= 0 ||
          !/^\d+(\.\d{1,2})?$/.test(form.value.screenSizeInch)
        ) {
          fieldErrors.screenSizeInch =
            "Screen size must be positive number with at most 2 decimal points or not specified.";
          isValid = false;
        }
      }
      break;
    case "ramGb":
      if (form.value.ramGb) {
        const ramGb = parseInt(form.value.ramGb);
        if (isNaN(ramGb) || ramGb <= 0) {
          fieldErrors.ramGb =
            "RAM size must be positive integer or not specified.";
          isValid = false;
        }
      }
      break;
    case "storageGb":
      if (form.value.storageGb) {
        const storageGb = parseInt(form.value.storageGb);
        if (isNaN(storageGb) || storageGb <= 0) {
          fieldErrors.storageGb =
            "Storage size must be positive integer or not specified.";
          isValid = false;
        }
      }
      break;
    case "color":
      if (form.value.color) {
        const trimmedColor = form.value.color.trim();
        if (!trimmedColor || trimmedColor.length > 40) {
          fieldErrors.color =
            "Color must be 1-40 characters long or not specified.";
          isValid = false;
        }
      }
      break;
  }
  return isValid;
}

// Validate all fields
function validateAllFields() {
  let allValid = true;
  for (const field in form.value) {
    if (validateField(field) === false) {
      allValid = false;
    }
  }
  return allValid;
}

const isFormValid = computed(() => {
  // Check if there are any error messages in the fieldErrors object
  return Object.keys(fieldErrors).every((key) => !fieldErrors[key]);
});

// Update handleBlur to use the new validation logic
function handleBlur(field) {
  validateField(field);
}

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
  // Validate all fields before saving
  if (!validateAllFields()) {
    return; // Stop if validation fails
  }

  try {
    const brandObj = brands.value.find(
      (b) => b.id === parseInt(form.value.brandId)
    );
    const dataToSend = {
      model: form.value.model.trim(),
      brand: brandObj ? { id: brandObj.id, name: brandObj.name } : null,
      description: form.value.description.trim(),
      price: parseInt(form.value.price),
      ramGb: form.value.ramGb ? parseInt(form.value.ramGb) : null,
      screenSizeInch: form.value.screenSizeInch
        ? parseFloat(form.value.screenSizeInch)
        : null,
      quantity: parseInt(form.value.quantity),
      storageGb: form.value.storageGb ? parseInt(form.value.storageGb) : null,
      color: form.value.color.trim() || null,
    };

    await createSaleItem(dataToSend);
    setTimeout(1000);
    router.push({
      name: "sale-items-page",
      query: { message: "The sale item has been successfully added." },
    });
  } catch (err) {
    alert("เกิดข้อผิดพลาด: " + err);
  }
}
</script>
