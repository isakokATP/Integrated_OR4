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
            type="text"
            class="input input-bordered w-full"
            data-testid="itbms-name"
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
            class="input input-bordered w-full"
            data-testid="itbms-websiteUrl"
          />
        </div>
        <div class="flex items-center gap-2">
          <label class="font-medium" for="isActive">Active</label>
          <input
            id="isActive"
            v-model="form.isActive"
            type="checkbox"
            class="toggle"
            data-testid="itbms-isActive"
          />
        </div>
        <div>
          <label class="block mb-1 font-medium" for="countryOfOrigin"
            >Country Of Origin</label
          >
          <input
            id="countryOfOrigin"
            v-model="form.countryOfOrigin"
            type="text"
            class="input input-bordered w-full"
            data-testid="itbms-countryOfOrigin"
          />
        </div>
        <div>
          <label class="block mb-1 font-medium" for="createdOn"
            >Created On</label
          >
          <input
            id="createdOn"
            v-model="form.createdOn"
            type="text"
            class="input input-bordered w-full bg-gray-100"
            data-testid="itbms-createdOn"
            disabled
          />
        </div>
        <div>
          <label class="block mb-1 font-medium" for="updatedOn"
            >Updated On</label
          >
          <input
            id="updatedOn"
            v-model="form.updatedOn"
            type="text"
            class="input input-bordered w-full bg-gray-100"
            data-testid="itbms-updatedOn"
            disabled
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
import { ref } from "vue";
import { useRouter } from "vue-router";
import { createBrand } from "@/services/saleItemService";
import Header from "@/components/Header.vue";

const router = useRouter();
const error = ref("");

const form = ref({
  name: "",
  websiteUrl: "",
  isActive: false,
  countryOfOrigin: "",
  createdOn: new Date().toISOString().slice(0, 19).replace("T", " "),
  updatedOn: new Date().toISOString().slice(0, 19).replace("T", " "),
});

const handleSubmit = async () => {
  error.value = "";
  try {
    await createBrand({
      name: form.value.name,
      websiteUrl: form.value.websiteUrl,
      isActive: form.value.isActive,
      countryOfOrigin: form.value.countryOfOrigin,
    });
    router.push("/brands");
  } catch (err) {
    error.value = err.message || "Failed to add brand";
  }
};

const goBack = () => {
  router.push("/brands");
};
</script>

<style scoped>
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
