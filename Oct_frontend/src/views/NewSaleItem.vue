<template>
  <Header />
  <div class="max-w-4xl mx-auto p-6">
    <div class="flex items-center mb-6">
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link
          to="/sale-items"
          class="text-blue-600 hover:underline font-medium"
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
            @blur="handleBlur('brandId')"
            @change="handleBlur('brandId')"
            class="itbms-brand w-full border rounded px-2 py-1"
          >
            <option value="">Select Brand</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
          <span
            v-if="fieldErrors.brandId"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.brandId }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Model<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.model"
            class="itbms-model w-full border rounded px-2 py-1"
            @blur="handleBlur('model')"
            @input="handleBlur('model')"
          />
          <span
            v-if="fieldErrors.model"
            class="itbms-message text-red-600 text-sm"
          >
            {{ fieldErrors.model }}
          </span>
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Price (Baht)<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.price"
            type="number"
            @blur="handleBlur('price')"
            @input="handleBlur('price')"
            step="1"
            class="itbms-price w-full border rounded px-2 py-1"
          />

          <span
            v-if="fieldErrors.price"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.price }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Description<span class="text-red-500"> *</span></label
          >
          <textarea
            v-model="form.description"
            v-trim
            @blur="handleBlur('description')"
            @input="handleBlur('description')"
            class="w-full border rounded px-2 py-1 itbms-description"
          ></textarea>
          <span
            v-if="fieldErrors.description"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.description }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            type="number"
            @blur="handleBlur('ramGb')"
            @input="handleBlur('ramGb')"
            step="1"
            class="w-full border rounded px-2 py-1 itbms-ramGb"
          />
          <span
            v-if="fieldErrors.ramGb"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.ramGb }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            type="number"
            step="0.01"
            @blur="handleBlur('screenSizeInch')"
            @input="handleBlur('screenSizeInch')"
            class="w-full border rounded px-2 py-1 itbms-screenSizeInch"
          />
          <span
            v-if="fieldErrors.screenSizeInch"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.screenSizeInch }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            type="number"
            @blur="handleBlur('storageGb')"
            @input="handleBlur('storageGb')"
            step="1"
            class="w-full border rounded px-2 py-1 itbms-storageGb"
          />
          <span
            v-if="fieldErrors.storageGb"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.storageGb }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            v-trim
            @blur="handleBlur('color')"
            @input="handleBlur('color')"
            class="w-full border rounded px-2 py-1 itbms-color"
          />
          <span
            v-if="fieldErrors.color"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.color }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Quantity<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.quantity"
            type="number"
            @blur="handleBlur('quantity')"
            @input="handleBlur('quantity')"
            class="w-full border rounded px-2 py-1 itbms-quantity"
          />
          <span
            v-if="fieldErrors.quantity"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.quantity }}</span
          >
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
            class="border border-gray-400 px-4 py-2 rounded hover:bg-blue-200 transition-colors duration-300"
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
  quantity: "",
});

const fieldErrors = reactive({});

const validateField = (field) => {
  let error = null;
  const value = form.value[field];

  switch (field) {
    case "brandId":
      if (!value) error = "Brand must be selected.";
      break;
    case "model":
      const trimmedModel = value ? value.trim() : "";
      if (!trimmedModel || trimmedModel.length > 60)
        error = "Model must be 1-60 characters long.";
      break;
    case "price":
      if (!/^[0-9]+$/.test(value) || parseInt(value) < 0)
        error = "Price must be non-negative integer.";
      break;
    case "description":
      const trimmedDescription = value ? value.trim() : "";
      if (!trimmedDescription || trimmedDescription.length > 16384)
        error = "Description must be 1-16,384 characters long.";
      break;
    case "ramGb":
      if (value !== "" && (!/^[0-9]+$/.test(value) || parseInt(value) <= 0))
        error = "RAM size must be positive integer or not specified.";
      break;
    case "screenSizeInch":
      if (value !== "") {
        const val = parseFloat(value);
        if (isNaN(val) || val <= 0 || !/^\d+(\.\d{1,2})?$/.test(value)) {
          error =
            "Screen size must be positive number with at most 2 decimal points or not specified.";
        }
      }
      break;
    case "storageGb":
      if (value !== "" && (!/^[0-9]+$/.test(value) || parseInt(value) <= 0))
        error = "Storage size must be positive integer or not specified.";
      break;
    case "color":
      if (value && value.length > 40)
        error = "Color must be 1-40 characters long or not specified.";
      break;
    case "quantity":
      if (!/^[0-9]+$/.test(value) || parseInt(value) < 0)
        error = "Quantity must be non-negative integer.";
      break;
  }

  fieldErrors[field] = error;
  return !error;
};

const validateAllFields = () => {
  let allValid = true;
  const fieldsToValidate = [
    "brandId",
    "model",
    "price",
    "description",
    "ramGb",
    "screenSizeInch",
    "storageGb",
    "color",
    "quantity",
  ];
  fieldsToValidate.forEach((field) => {
    if (!validateField(field)) {
      allValid = false;
    }
  });
  return allValid;
};

const isFormValid = computed(() => {
  const hasValidationErrors = Object.values(fieldErrors).some(
    (error) => error !== null && error !== ""
  );
  return (
    !hasValidationErrors &&
    form.value.brandId &&
    form.value.model.trim() &&
    form.value.price !== "" &&
    form.value.description.trim() &&
    form.value.quantity !== ""
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
  router.push({ name: "sale-items-list-page" });
}

async function handleSave() {
  if (!validateAllFields()) {
    return;
  }

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

  try {
    await createSaleItem(dataToSend);
    alert("สร้างรายการขายสำเร็จ!");
    router.push({
      name: "sale-items-list-page",
      query: { message: "The sale item has been successfully added." },
    });
  } catch (err) {
    alert("เกิดข้อผิดพลาด: " + err);
  }
}

const handleBlur = (field) => {
  validateField(field);
};
</script>

<style scoped>
.text-sm {
  font-size: 0.875rem;
}
</style>
