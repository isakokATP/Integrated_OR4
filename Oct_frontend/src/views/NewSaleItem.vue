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
            @blur="touched.brandId = true"
            @change="touched.brandId = true"
            class="itbms-brand w-full border rounded px-2 py-1"
          >
            <option value="">Select Brand</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
          <span v-if="errors.brandId" class="text-red-500 text-sm">{{
            errors.brandId
          }}</span>
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Model<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.model"
            class="itbms-model w-full border rounded px-2 py-1"
            @focus="() => (touched.model = true)"
            @blur="
              () => {
                form.model = form.model.trim();
                updateError();
              }
            "
            @input="updateError"
          />
          <span v-if="errors.model" class="text-red-600 text-sm">
            {{ errors.model }}
          </span>
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Price (Baht)<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.price"
            type="number"
            @blur="touched.price = true"
            @input="touched.price = true"
            step="1"
            class="itbms-price w-full border rounded px-2 py-1"
          />

          <span v-if="errors.price" class="text-red-500 text-sm">{{
            errors.price
          }}</span>
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Description<span class="text-red-500"> *</span></label
          >
          <textarea
            v-model="form.description"
            v-trim
            @blur="touched.description = true"
            @input="touched.description = true"
            class="w-full border rounded px-2 py-1 itbms-description"
          ></textarea>
          <span v-if="errors.description" class="text-red-500 text-sm">{{
            errors.description
          }}</span>
        </div>

        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            type="number"
            @blur="touched.ramGb = true"
            @input="touched.ramGb = true"
            step="1"
            class="w-full border rounded px-2 py-1 itbms-ramGb"
          />
          <span v-if="errors.ramGb" class="text-red-500 text-sm">{{
            errors.ramGb
          }}</span>
        </div>

        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            type="number"
            step="0.01"
            @blur="touched.screenSizeInch = true"
            @input="touched.screenSizeInch = true"
            class="w-full border rounded px-2 py-1 itbms-screenSizeInch"
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
            class="w-full border rounded px-2 py-1 itbms-storageGb"
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
            class="w-full border rounded px-2 py-1 itbms-color"
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
            class="w-full border rounded px-2 py-1 itbms-quantity"
          />
          <span v-if="errors.quantity" class="text-red-500 text-sm">{{
            errors.quantity
          }}</span>
        </div>

        <div class="flex gap-4 mt-6">
          <button
            type="submit"
            class="itbms-save-button bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-500 transition-colors duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
            :disabled="!mandatoryValid"
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
import { ref, computed, onMounted } from "vue";
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
  quantity: "1",
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

function updateError(field) {
  if (field && touched.value.hasOwnProperty(field)) {
    touched.value[field] = true;
  }
}

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
    (!/^[0-9]+$/.test(form.value.price) || parseInt(form.value.price) < 0)
  )
    e.price = "* Price must be non-negative integer.";
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
  if (
    touched.value.quantity &&
    form.value.quantity !== "" &&
    (!/^[0-9]+$/.test(form.value.quantity) || parseInt(form.value.quantity) < 0)
  )
    e.quantity = "* Quantity must be non-negative integer.";
  return e;
});

const isFormValid = computed(() => Object.keys(errors.value).length === 0);

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

const mandatoryValid = computed(() => {
  const { brandId, model, price, description, quantity } = form.value;
  const m = model.trim();
  const d = description.trim();

  return (
    brandId &&
    m.length >= 1 &&
    m.length <= 60 &&
    /^[0-9]+$/.test(price) &&
    +price >= 0 &&
    d.length >= 1 &&
    d.length <= 16384 &&
    quantity !== "" &&
    /^[0-9]+$/.test(quantity) &&
    +quantity >= 0
  );
});

async function handleSave() {
  // Mark all fields as touched
  Object.keys(touched.value).forEach((key) => {
    touched.value[key] = true;
  });

  // Block save if mandatory fields are invalid
  if (!mandatoryValid.value) return;

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
    quantity: form.value.quantity === "" ? 1 : parseInt(form.value.quantity),
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
</script>

<style scoped>
.text-sm {
  font-size: 0.875rem;
}
</style>
