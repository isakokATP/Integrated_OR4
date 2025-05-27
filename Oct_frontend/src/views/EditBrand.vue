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
      <span class="font-semibold">Edit Brand</span>
    </nav>
    <div class="bg-blue-50 p-6 rounded shadow">
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="block mb-1 font-medium" for="name"
            >Name<span class="text-red-500"> *</span></label
          >
          <input
            id="name"
            v-model="form.name"
            v-trim
            type="text"
            class="itbms-name input input-bordered w-full"
            @blur="touched.name = true"
            @input="
              () => {
                touched.name = true;
                updateErrors();
              }
            "
          />
          <span v-if="errors.name" class="text-red-500 text-sm">{{
            errors.name
          }}</span>
        </div>
        <div>
          <label class="block mb-1 font-medium" for="websiteUrl"
            >Website URL</label
          >
          <input
            id="websiteUrl"
            v-model="form.websiteUrl"
            v-trim
            type="url"
            class="itbms-websiteUrl input input-bordered w-full"
            @blur="touched.websiteUrl = true"
            @input="updateErrors"
          />
          <span v-if="errors.websiteUrl" class="text-red-500 text-sm">{{
            errors.websiteUrl
          }}</span>
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
            >Country of Origin</label
          >
          <input
            id="countryOfOrigin"
            v-model="form.countryOfOrigin"
            v-trim
            type="text"
            class="itbms-countryOfOrigin input input-bordered w-full"
            @blur="touched.countryOfOrigin = true"
            @input="updateErrors"
          />
          <span v-if="errors.countryOfOrigin" class="text-red-500 text-sm">{{
            errors.countryOfOrigin
          }}</span>
        </div>
        <div class="flex gap-2 space-y-4">
          <button
            type="submit"
            class="itbms-save-button btn text-white bg-blue-900 hover:bg-blue-500"
            :disabled="!mandatoryValid || !hasChanges"
            :class="{
              'opacity-50 cursor-not-allowed': !mandatoryValid || !hasChanges,
            }"
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
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { fetchBrandById, updateBrand } from "@/services/saleItemService";
import Header from "@/components/Header.vue";

const router = useRouter();
const route = useRoute();
const error = ref("");

const form = reactive({
  name: "",
  websiteUrl: "",
  isActive: true,
  countryOfOrigin: "",
});

const originalData = ref({
  name: "",
  websiteUrl: "",
  isActive: false,
  countryOfOrigin: "",
});

const touched = reactive({
  name: false,
  websiteUrl: false,
  countryOfOrigin: false,
});

function isValidUrl(url) {
  if (!url) return true;
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}

const errors = reactive({});

function updateErrors() {
  if (touched.name) {
    if (form.name.trim().length < 1 || form.name.trim().length > 30) {
      errors.name = "Brand name must be 1-30 characters long.";
    } else {
      delete errors.name;
    }
  }

  if (touched.websiteUrl) {
    if (
      form.websiteUrl.trim().length > 40 ||
      !isValidUrl(form.websiteUrl.trim())
    ) {
      errors.websiteUrl = "Brand URL must be a valid URL or not specified.";
    } else {
      delete errors.websiteUrl;
    }
  }

  if (touched.countryOfOrigin) {
    if (
      form.countryOfOrigin.trim().length > 0 &&
      (form.countryOfOrigin.trim().length < 1 ||
        form.countryOfOrigin.trim().length > 80)
    ) {
      errors.countryOfOrigin =
        "Brand country of origin must be 1-80 characters long or not specified.";
    } else {
      delete errors.countryOfOrigin;
    }
  }
}

const mandatoryValid = computed(() => {
  return (
    form.name.trim().length >= 1 &&
    form.name.trim().length <= 30 &&
    (form.websiteUrl.trim() === "" ||
      (form.websiteUrl.trim().length <= 40 &&
        isValidUrl(form.websiteUrl.trim()))) &&
    (form.countryOfOrigin.trim() === "" ||
      (form.countryOfOrigin.trim().length >= 1 &&
        form.countryOfOrigin.trim().length <= 80)) &&
    Object.keys(errors).length === 0
  );
});

const hasChanges = computed(() => {
  return (
    form.name !== originalData.value.name ||
    (form.websiteUrl?.trim() || "") !==
      (originalData.value.websiteUrl?.trim() || "") ||
    form.isActive !== originalData.value.isActive ||
    (form.countryOfOrigin?.trim() || "") !==
      (originalData.value.countryOfOrigin?.trim() || "")
  );
});

const loadBrandData = async () => {
  try {
    const brandId = route.params.id;
    const brandData = await fetchBrandById(brandId);
    if (brandData.status === "not_found") {
      router.push({
        name: "brands-list-page",
        query: { message: "The brand is not found.", type: "error" },
      });
      return;
    }

    form.name = brandData.name || "";
    form.websiteUrl = brandData.websiteUrl || "";
    form.isActive = brandData.isActive ?? true;
    form.countryOfOrigin = brandData.countryOfOrigin || "";

    originalData.value = {
      name: form.name,
      websiteUrl: form.websiteUrl,
      isActive: form.isActive,
      countryOfOrigin: form.countryOfOrigin,
    };
  } catch (err) {
    error.value = "Cannot load brand data";
    console.error(err);
  }
};

const handleSubmit = async () => {
  touched.name = true;
  touched.websiteUrl = true;
  touched.countryOfOrigin = true;
  updateErrors();

  if (!mandatoryValid.value) return;

  error.value = "";
  try {
    const brandId = route.params.id;
    await updateBrand(brandId, {
      name: form.name.trim(),
      websiteUrl: form.websiteUrl.trim() || null,
      countryOfOrigin: form.countryOfOrigin.trim() || null,
      isActive: form.isActive,
    });

    router.push({
      name: "brands-list-page",
      query: { message: "The brand has been updated." },
    });
  } catch (err) {
    error.value = err.message || "Cannot update brand data";
  }
};

const goBack = () => {
  router.push({ name: "brands-list-page" });
};

onMounted(() => {
  loadBrandData();
});
</script>

<style scoped>
/* TailwindCSS is used */
</style>
