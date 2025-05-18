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
            id="name"
            v-model="form.name"
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
            id="websiteUrl"
            v-model="form.websiteUrl"
            type="url"
            class="itbms-websiteUrl input input-bordered w-full"
          />
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
            type="text"
            class="itbms-countryOfOrigin input input-bordered w-full"
          />
        </div>
        <div class="flex gap-2 mt-4">
          <button
            type="submit"
            class="btn btn-primary"
            data-testid="itbms-save-button"
          >
            Save
          </button>
          <button
            type="button"
            class="btn btn-secondary"
            data-testid="itbms-cancel-button"
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
import { ref, onMounted } from "vue";
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
    form.value = {
      ...brandData,
      isActive: brandData.isActive === 1,
      countryOfOrigin: brandData.countryOfOrigin || "",
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
    await updateBrand( {
      name: form.value.name,
      websiteUrl: form.value.websiteUrl,
      isActive: form.value.isActive,
      countryOfOrigin: form.value.countryOfOrigin,
    });
    router.push({ name: "brands-list-page" });
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
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
