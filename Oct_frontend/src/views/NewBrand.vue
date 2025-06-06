<template>
  <Header />
  <div class="p-6 max-w-2xl mx-auto">
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items/list"
        class="text-blue-600 hover:underline font-medium"
        data-testid="itbms-item-list"
        >Sale Item List</router-link
      >
      <span class="mx-1">›</span>
      <router-link
        to="/brands"
        class="text-blue-600 hover:underline font-medium"
        data-testid="itbms-manage-brand"
        >Brand List</router-link
      >
      <span class="mx-1">›</span>
      <span class="font-semibold">New Brand</span>
    </nav>
    <div class="bg-blue-50 p-6 rounded shadow">
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="block mb-1 font-medium" for="name"
            >Name<span class="text-red-500">*</span></label
          >
          <input
            id="name"
            v-model="form.name"
            v-trim
            type="text"
            class="itbms-name input input-bordered w-full"
            @blur="handleBlur('name')"
            required
          />
          <div
            v-if="fieldErrors.name"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.name }}
          </div>
        </div>
        <div>
          <label class="lock mb-1 font-medium" for="websiteUrl"
            >Website URL</label
          >
          <input
            id="websiteUrl"
            v-model="form.websiteUrl"
            v-trim
            type="url"
            class="itbms-websiteUrl input input-bordered w-full"
            @blur="handleBlur('websiteUrl')"
          />
          <div
            v-if="fieldErrors.websiteUrl"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.websiteUrl }}
          </div>
        </div>
        <div class="flex items-center gap-2">
          <label class="font-medium" for="isActive">Active</label>
          <input
            id="isActive"
            v-model="form.isActive"
            type="checkbox"
            class="itbms-isActive toggle"
          />
        </div>
        <div>
          <label class="block mb-1 font-medium" for="countryOfOrigin"
            >Country Of Origin</label
          >
          <input
            id="countryOfOrigin"
            v-model="form.countryOfOrigin"
            v-trim
            type="text"
            class="itbms-countryOfOrigin input input-bordered w-full"
            @blur="handleBlur('countryOfOrigin')"
          />
          <div
            v-if="fieldErrors.countryOfOrigin"
            class="itbms-message text-red-500 text-sm mt-1"
          >
            {{ fieldErrors.countryOfOrigin }}
          </div>
        </div>
        <div class="flex gap-2 mt-4">
          <button
            type="submit"
            class="itbms-save-button btn text-white bg-blue-900 hover:bg-blue-500"
            :disabled="!isFormValid"
            :class="{ 'opacity-50 cursor-not-allowed': !isFormValid }"
          >
            Save
          </button>
          <button
            type="button"
            class="itbms-cancel-button btn text-white bg-red-700 hover:bg-red-500"
            @click="goBack"
          >
            Cancel
          </button>
        </div>
        <div v-if="error" class="text-red-500">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from "vue";
import { useRouter } from "vue-router";
import { createBrand } from "@/services/saleItemService";
import Header from "@/components/Header.vue";

const router = useRouter();
const error = ref("");
const fieldErrors = reactive({});

const form = ref({
  name: "",
  websiteUrl: "",
  isActive: true,
  countryOfOrigin: "",
});

const validateField = (field) => {
  let error = null;
  const value = form.value[field];

  switch (field) {
    case "name":
      const trimmedName = value ? value.trim() : "";
      if (!trimmedName) error = "Brand name must be 1-30 characters long.";
      else if (trimmedName.length > 30)
        error = "Brand name must be 1-30 characters long.";
      break;
    case "countryOfOrigin":
      if (value) {
        const trimmedCountry = value.trim();
        if (!trimmedCountry)
          error =
            "Brand country of origin must be 1-80 characters long or not specified.";
        else if (trimmedCountry.length > 80)
          error =
            "Brand country of origin must be 1-80 characters long or not specified.";
      }
      break;
    case "websiteUrl":
      if (value) {
        const trimmedUrl = value.trim();
        if (!trimmedUrl)
          error = "Brand URL must be a valid URL or not specified.";
        else {
          try {
            new URL(trimmedUrl);
          } catch {
            error = "Brand URL must be a valid URL or not specified.";
          }
        }
      }
      break;
  }

  fieldErrors[field] = error;
  return !error;
};

const validateAllFields = () => {
  let allValid = true;
  for (const field in form.value) {
    if (!validateField(field)) {
      allValid = false;
    }
  }
  return allValid;
};

const isFormValid = computed(() => {
  const hasValidationErrors = Object.values(fieldErrors).some(
    (error) => error !== null && error !== ""
  );
  return !hasValidationErrors && form.value.name.trim() !== "";
});

const handleSubmit = async () => {
  error.value = "";
  if (!validateAllFields()) {
    return;
  }

  try {
    await createBrand({
      name: form.value.name.trim(),
      websiteUrl: form.value.websiteUrl ? form.value.websiteUrl.trim() : null,
      countryOfOrigin: form.value.countryOfOrigin
        ? form.value.countryOfOrigin.trim()
        : null,
      isActive: form.value.isActive,
    });
    router.push({
      name: "brands-list-page",
      query: { message: "The brand has been added." },
    });
  } catch (err) {
    error.value = err.message || "Failed to add brand";
  }
};

const handleBlur = (field) => {
  validateField(field);
};

const goBack = () => {
  router.push({ name: "brands-list-page" });
};
</script>

<style scoped>
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
