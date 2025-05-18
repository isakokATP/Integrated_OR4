<template>
  <Header />
  <div class="p-6">
    <div class="flex justify-between mb-4">
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link
          to="/sale-items/list"
          class="itbms-sale-items-list text-blue-600 hover:underline font-medium"
          >Sale Item List</router-link
        >
        <span class="mx-1"> › </span>
        <span class="font-semibold"> Brands List </span>
      </nav>
      <div class="flex justify-end">
        <button
          class="itbms-add-brand btn btn-primary"
          @click="goToAddBrand"
        >
          Add Brand
        </button>
      </div>
    </div>
    <div class="overflow-x-auto">
      <table class="min-w-full bg-white border border-gray-200">
        <thead>
          <tr>
            <th>Id</th>
            <th>Name</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="brand in brands"
            :key="brand.id"
            class="itbms-row border-t"
          >
            <td>{{ brand.id }}</td>
            <td>{{ brand.name }}</td>
            <td>
              <button
                class="itbms-edit-brand btn btn-xs btn-outline mr-1"
                @click="goToEditBrand(brand.id)"
              >
                E
              </button>
              <button
                class="itbms-delete-brand btn btn-xs btn-outline btn-error"
                @click="deleteBrandItem(brand.id)"
              >
                D
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { fetchBrands, deleteBrand } from "@/services/saleItemService";
import Header from "@/components/Header.vue";

const brands = ref([]);
const router = useRouter();

const loadBrands = async () => {
  brands.value = await fetchBrands();
};

const goToSaleItemList = () => {
  router.push({ name: "sale-items-list-page" });
};

const goToAddBrand = () => {
  router.push({ name: "new-brand-page" });
};

const goToEditBrand = (id) => {
  router.push({ name: "edit-brand-page", params: { id } });
};

const deleteBrandItem = async (id) => {
  if (confirm("Are you sure you want to delete this brand?")) {
    await deleteBrand(id);
    await loadBrands();
  }
};

onMounted(loadBrands);
</script>

<style scoped>
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
