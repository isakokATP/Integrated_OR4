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
          class="bg-orange-600 hover:bg-orange-400 text-white px-6 py-3 rounded"
          @click="goToAddBrand"
        >
          Add Brand
        </button>
      </div>
    </div>
    <div class="overflow-x-auto mt-10">
      <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-200 text-center">
          <tr>
            <th class="px-3 py-2 border-r border-gray-300 w-16">Id</th>
            <th class="px-3 py-2 border-r border-gray-300 w-[300px]">Name</th> <!-- กว้างกว่า และชิดซ้าย -->
            <th class="px-3 py-2 border-r border-gray-300 w-24">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="brand in brands"
            :key="brand.id"
            class="border-b border-gray-300 hover:bg-[#E3F2FD] even:bg-gray-100 text-center"
          >
            <td class="px-3 py-2 border-r border-gray-300">{{ brand.id }}</td>
            <td class="px-3 py-2 border-r border-gray-300">{{ brand.name }}</td> <!-- ชิดซ้าย -->
            <td class="px-3 py-2 border-r border-gray-300 text-center align-middle">
              <div class="flex justify-center items-center gap-2">
                <button
                  class="btn btn-xs bg-green-600 hover:bg-green-500 text-white px-3 py-1 rounded w-10"
                  @click="goToEditBrand(brand.id)"
                  title="Edit"
                >
                  E
                </button>
                <button
                  class="btn btn-xs bg-red-600 hover:bg-red-500 text-white px-3 py-1 rounded w-10"
                  @click="deleteBrandItem(brand.id)"
                  title="Delete"
                >
                  D
                </button>
              </div>
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
/* TailwindCSS is used, แต่ถ้าต้องการเพิ่มสไตล์อื่นๆ สามารถเพิ่มที่นี่ได้ */
</style>
