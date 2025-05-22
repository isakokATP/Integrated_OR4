<template>
  <Header />
  <div class="p-6 max-w-2xl mx-auto">
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items/list"
        class="itbms-sale-items-list text-blue-600 hover:underline font-medium"
        >Sale Items</router-link
      >
      <span class="mx-1">›</span>
      <router-link
        to="/brands"
        class="itbms-brands-list text-blue-600 hover:underline font-medium"
        >Brands List</router-link
      >
      <span class="mx-1">›</span>
      <span class="font-semibold">Edit Brand</span>
    </nav>
    <div class="bg-blue-50 p-6 rounded shadow">
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="block mb-1 font-medium" for="name"
            >Name<span class="text-red-500">*</span></label
          >
          <input
            v-model="form.name"
            v-trim
            type="text"
            class="itbms-name input input-bordered w-full"
            required
          />
        </div>
        <div>
          <label class="block mb-1 font-medium" for="websiteUrl"
            >Website URL</label
          >
          <input
            v-model="form.websiteUrl"
            v-trim
            type="url"
            class="itbms-websiteUrl input input-bordered w-full"
          />
        </div>
        <div class="flex items-center gap-2">
          <label class="font-medium" for="isActive">Active</label>
          <input
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
            v-model="form.countryOfOrigin"
            v-trim
            type="text"
            class="itbms-countryOfOrigin input input-bordered w-full"
          />
        </div>
        <div class="flex gap-2 mt-4">
          <button
            type="submit"
            class="itbms-save-button btn text-white bg-blue-900 hover:bg-blue-500"
            :disabled="!hasChanges"
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
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { fetchBrandById, updateBrand } from "@/services/saleItemService";
import Header from "@/components/Header.vue";

const router = useRouter();
const route = useRoute();
const error = ref("");

const form = ref({
  name: "",
  websiteUrl: "",
  isActive: false,
  countryOfOrigin: "",
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
    console.log("brandData", brandData);
    form.value = {
      ...brandData,
      isActive: brandData.isActive,
      countryOfOrigin: brandData.countryOfOrigin,
    };
    originalData.value = {
      ...brandData,
      isActive: brandData.isActive,
      countryOfOrigin: brandData.countryOfOrigin,
    };
  } catch (err) {
    error.value = "Cannot load brand data";
    console.error(err);
  }
};

const handleSubmit = async () => {
  error.value = "";
  try {
    const brandId = route.params.id;
    await updateBrand(brandId, {
      name: form.value.name,
      websiteUrl: form.value.websiteUrl ? form.value.websiteUrl.trim() : null,
      isActive: form.value.isActive,
      countryOfOrigin: form.value.countryOfOrigin
        ? form.value.countryOfOrigin.trim()
        : null,
    });

    router.push({
      name: "brands-list-page",
      query: { message: "The brand has been updated." },
    });
  } catch (err) {
    error.value = err.message || "Cannot update brand data";
  }
};
const originalData = ref({
  name: "",
  websiteUrl: "",
  isActive: false,
  countryOfOrigin: "",
});
const hasChanges = computed(() => {
  return (
    form.value.name !== originalData.value.name ||
    (form.value.websiteUrl ? form.value.websiteUrl.trim() : null) !==
      (originalData.value.websiteUrl
        ? originalData.value.websiteUrl.trim()
        : null) ||
    form.value.isActive !== originalData.value.isActive ||
    (form.value.countryOfOrigin ? form.value.countryOfOrigin.trim() : null) !==
      (originalData.value.countryOfOrigin
        ? originalData.value.countryOfOrigin.trim()
        : null)
  );
});

const goBack = () => {
  router.push({ name: "brands-list-page" });
};

onMounted(() => {
  loadBrandData();
});
</script>

<style scoped>
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
