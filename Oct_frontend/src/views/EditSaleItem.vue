<script setup>
import { ref, onMounted, computed, watch, reactive } from "vue";
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
  quantity: "", // Quantity might be loaded as a number, but input type number binds as string
});

const fieldErrors = reactive({});

// Validation logic - this will return the first error found, or null if valid
// const validateForm = () => { ... old validateForm ... };

// Function to validate a specific field and set its error
const validateField = (field) => {
  let error = null;
  const value = form.value[field];

  switch (field) {
    case "brandId":
      if (!value) error = "Brand must be selected.";
      break;
    case "model":
      const trimmedModel = value ? value.trim() : "";
      if (!trimmedModel) error = "Model must be 1-60 characters long.";
      else if (trimmedModel.length > 60)
        error = "Model must be 1-60 characters long.";
      break;
    case "description":
      const trimmedDescription = value ? value.trim() : "";
      // Assuming description max length based on previous discussion, PBI had 65,535
      if (!trimmedDescription)
        error = "Description must be 1-16,384 characters long.";
      else if (trimmedDescription.length > 65535)
        error = "Description must be 1-16,384 characters long.";
      break;
    case "price":
      const price = value === "" || value === null ? NaN : parseInt(value);
      if (isNaN(price) || price < 0)
        error = "Price must be non-negative integer.";
      break;
    case "quantity":
      const quantity = value === "" || value === null ? NaN : parseInt(value);
      if (isNaN(quantity) || quantity < 0)
        error = "Quantity must be non-negative integer.";
      break;
    case "ramGb":
      if (value !== "" && value !== null) {
        const ramGb = parseInt(value);
        if (isNaN(ramGb) || ramGb <= 0)
          error = "RAM size must be positive integer or not specified.";
      }
      break;
    case "screenSizeInch":
      if (value !== "" && value !== null) {
        const screenSize = parseFloat(value);
        // Using a regex to check for at most 2 decimal places
        if (
          isNaN(screenSize) ||
          screenSize <= 0 ||
          !/^\d+(\.\d{1,2})?$/.test(value)
        )
          error =
            "Screen size must be positive number with at most 2 decimal points or not specified.";
      }
      break;
    case "storageGb":
      if (value !== "" && value !== null) {
        const storageGb = parseInt(value);
        if (isNaN(storageGb) || storageGb <= 0)
          error = "Storage size must be positive integer or not specified.";
      }
      break;
    case "color":
      if (value) {
        const trimmedColor = value.trim();
        if (!trimmedColor)
          error = "Color must be 1-40 characters long or not specified.";
        // Should not be empty string if provided
        else if (trimmedColor.length > 40)
          error = "Color must be 1-40 characters long or not specified.";
      }
      break;
  }

  fieldErrors[field] = error; // Set the error message (or null)
  return !error; // Return true if valid, false if not
};

// Validate all fields and return overall validity
const validateAllFields = () => {
  let allValid = true;
  for (const field in form.value) {
    // Only validate fields that have validation rules defined in validateField
    // and exclude fields that are not directly in the form but part of originalData structure like brand object
    if (
      [
        "brandId",
        "model",
        "price",
        "description",
        "ramGb",
        "screenSizeInch",
        "storageGb",
        "color",
        "quantity",
      ].includes(field)
    ) {
      if (!validateField(field)) {
        allValid = false;
      }
    }
  }
  return allValid;
};

// const validationError = computed(() => { ... old validationError ... });

const isFormValid = computed(() => {
  // Form is valid if there are no validation errors AND hasChanges is true
  // Check if any field in fieldErrors has a non-null/non-empty value
  const hasValidationErrors = Object.values(fieldErrors).some(
    (error) => error !== null && error !== ""
  );
  return !hasValidationErrors && hasChanges.value;
});

// Keep the existing hasChanges computed property
const hasChanges = computed(() => {
  if (!originalData.value) return false;
  // Convert form values to comparable types
  const current = {
    brandId: form.value.brandId ? parseInt(form.value.brandId) : "",
    model: form.value.model.trim(),
    price: form.value.price ? parseInt(form.value.price) : "",
    description: form.value.description.trim(),
    ramGb: form.value.ramGb ? parseInt(form.value.ramGb) : null,
    screenSizeInch: form.value.screenSizeInch
      ? parseFloat(form.value.screenSizeInch)
      : null,
    storageGb: form.value.storageGb ? parseInt(form.value.storageGb) : null,
    color: form.value.color ? form.value.color.trim() : null,
    quantity: form.value.quantity ? parseInt(form.value.quantity) : "",
  };
  const original = {
    brandId: originalData.value.brand ? originalData.value.brand.id : "",
    model: originalData.value.model.trim(),
    price: originalData.value.price,
    description: originalData.value.description.trim(),
    ramGb: originalData.value.ramGb,
    screenSizeInch: originalData.value.screenSizeInch,
    storageGb: originalData.value.storageGb,
    color: originalData.value.color ? originalData.value.color.trim() : null,
    quantity: originalData.value.quantity,
  };

  // Compare all fields
  for (const key in current) {
    if (current[key] !== original[key]) {
      // Handle potential floating point comparison issues for screenSizeInch
      if (
        key === "screenSizeInch" &&
        typeof current[key] === "number" &&
        typeof original[key] === "number"
      ) {
        if (Math.abs(current[key] - original[key]) > 0.001) {
          return true; // Values are different
        }
      } else if (current[key] !== original[key]) {
        return true; // Values are different
      }
    }
  }

  return false; // No changes detected
});

onMounted(async () => {
  try {
    brands.value = await fetchBrands();
    console.log(brands.value);
    const item = await fetchSaleItemById(id);

    if (item.status === "not_found") {
      errorMsg.value = "The requested sale item does not exist.";
      router.push({ name: "sale-items-page" });
      return;
    }

    const brand = brands.value.find((b) => b.name === item.brandName);

    form.value = {
      brandId: brand ? brand.id : "",
      model: item.model,
      price: item.price !== null ? String(item.price) : "", // Store as string to work with input type="number"
      description: item.description,
      ramGb: item.ramGb !== null ? String(item.ramGb) : "",
      screenSizeInch:
        item.screenSizeInch !== null ? String(item.screenSizeInch) : "",
      storageGb: item.storageGb !== null ? String(item.storageGb) : "",
      color: item.color,
      quantity: item.quantity !== null ? String(item.quantity) : "1", // Store as string
    };

    // Store original data for comparison - ensure types match comparison logic
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

// Add blur handlers to trigger field validation
const handleBlur = (field) => {
  validateField(field);
};

const handleSave = async () => {
  // Validate all fields before saving
  if (!validateAllFields()) {
    // If validation fails, scroll to the first error message or highlight fields
    // Optionally, you could set a general error message here as well
    // errorMsg.value = "Please fix the errors in the form.";
    return; // Stop if validation fails
  }

  // Clear any general error messages before saving
  errorMsg.value = "";

  try {
    const dataToSend = {
      model: form.value.model.trim(),
      // Ensure brand object is correctly structured for the backend
      brand: form.value.brandId ? { id: parseInt(form.value.brandId) } : null, // Assuming backend needs brand ID for update
      description: form.value.description.trim(),
      price:
        form.value.price !== "" && form.value.price !== null
          ? parseInt(form.value.price)
          : 0, // Default price to 0 if empty
      ramGb:
        form.value.ramGb !== "" && form.value.ramGb !== null
          ? parseInt(form.value.ramGb)
          : null,
      screenSizeInch:
        form.value.screenSizeInch !== "" && form.value.screenSizeInch !== null
          ? parseFloat(form.value.screenSizeInch)
          : null,
      storageGb:
        form.value.storageGb !== "" && form.value.storageGb !== null
          ? parseInt(form.value.storageGb)
          : null,
      color: form.value.color ? form.value.color.trim() : null,
      quantity:
        form.value.quantity !== "" && form.value.quantity !== null
          ? parseInt(form.value.quantity)
          : 1, // Default quantity to 1 if not provided/null/empty string
    };

    await updateSaleItem(id, dataToSend);
    router.push({
      name: "sale-items-page-byId",
      params: { id },
      query: { message: "Item updated successfully!" },
    });
  } catch (err) {
    // Use errorMsg ref to display save errors if needed
    errorMsg.value = "Error saving item: " + (err.message || "Unknown error");
  }
};

const handleCancel = () => {
  if (hasChanges.value) {
    confirm("You have unsaved changes. Are you sure you want to cancel?") &&
      router.push({ name: "sale-items-page" });
  } else {
    router.push({ name: "sale-items-page" });
  }
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
  <!-- Display general error messages like loading errors -->
  <div v-if="errorMsg" class="text-red-600 text-center mt-8">
    {{ errorMsg }}
  </div>
  <!-- Remove the single validation error message div -->
  <!-- <div v-if="validationError" class="itbms-message text-red-500 text-center mt-4 mb-4"> -->
  <!--   {{ validationError }} -->
  <!-- </div> -->
  <div v-if="!errorMsg" class="max-w-4xl mx-auto p-6">
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
          <label class="block mb-1"
            >Brand<span class="text-red-500"> *</span></label
          >
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
          <label class="block mb-1"
            >Model<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.model"
            v-trim
            class="itbms-model w-full border rounded px-2 py-1"
            @blur="handleBlur('model')"
          />
          <div
            v-if="fieldErrors.model"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.model }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1"
            >Price (Baht)<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.price"
            type="number"
            step="1"
            class="itbms-price w-full border rounded px-2 py-1"
            @blur="handleBlur('price')"
          />
          <div
            v-if="fieldErrors.price"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.price }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1"
            >Description<span class="text-red-500"> *</span></label
          >
          <textarea
            v-model="form.description"
            v-trim
            class="itbms-description w-full border rounded px-2 py-1"
            @blur="handleBlur('description')"
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
            step="1"
            class="itbms-ramGb w-full border rounded px-2 py-1"
            @blur="handleBlur('ramGb')"
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
            class="itbms-screenSizeInch w-full border rounded px-2 py-1"
            @blur="handleBlur('screenSizeInch')"
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
            step="1"
            class="itbms-storageGb w-full border rounded px-2 py-1"
            @blur="handleBlur('storageGb')"
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
            class="itbms-color w-full border rounded px-2 py-1"
            @blur="handleBlur('color')"
          />
          <div
            v-if="fieldErrors.color"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.color }}
          </div>
        </div>
        <div class="mb-3">
          <label class="block mb-1"
            >Quantity<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.quantity"
            type="number"
            class="itbms-quantity w-full border rounded px-2 py-1"
            @blur="handleBlur('quantity')"
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
            class="itbms-save-button bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-500 transition-colors duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
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
