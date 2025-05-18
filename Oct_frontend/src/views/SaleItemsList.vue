<template>
  <Header />
  <div class="px-6 pt-2">
    <div class="flex justify-between mb-4">
      <button
        class="text-white bg-blue-900 hover:bg-blue-500 text-lg px-6 py-3 rounded"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
      <button
        class="bg-orange-600 hover:bg-orange-400 text-white px-6 py-3 rounded"
        @click="goToManageBrand"
      >
        Manage Brand
      </button>
    </div>
    <div class="overflow-x-auto mt-10">
      <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-200 text-center">
          <tr>
            <th class="px-3 py-2 border-r border-gray-300">Id</th>
            <th class="px-3 py-2 border-r border-gray-300">Brand</th>
            <th class="px-3 py-2 border-r border-gray-300">Model</th>
            <th class="px-3 py-2 border-r border-gray-300">Ram</th>
            <th class="px-3 py-2 border-r border-gray-300">Storage</th>
            <th class="px-3 py-2 border-r border-gray-300">Color</th>
            <th class="px-3 py-2 border-r border-gray-300">Price</th>
            <th class="px-3 py-2">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in saleItems"
            :key="item.id"
            class="border-b border-gray-300 hover:bg-[#E3F2FD] even:bg-gray-100 text-center"
          >
            <td class="px-3 py-2 border-r border-gray-300">{{ item.id }}</td>
            <td class="px-3 py-2 border-r border-gray-300">{{ item.brand}}</td>
            <td class="px-3 py-2 border-r border-gray-300 text-left">{{ item.model }}</td>
            <td class="px-3 py-2 border-r border-gray-300">{{ item.ramGb }}</td>
            <td class="px-3 py-2 border-r border-gray-300">{{ item.storageGb }}</td>
            <td class="px-3 py-2 border-r border-gray-300">{{ item.color }}</td>
            <td class="px-3 py-2 border-r border-gray-300">{{ item.price.toLocaleString() }}</td>
            <td class="px-3 py-2 space-x-1">
              <!-- Edit ปุ่มสีเขียว -->
              <button
                class="btn btn-xs bg-green-600 hover:bg-green-500 text-white px-2 py-1 rounded"
                @click="goToEdit(item.id)"
                title="Edit"
              >
                E
              </button>

              <!-- Delete ปุ่มสีแดง -->
              <button
                class="btn btn-xs bg-red-600 hover:bg-red-500 text-white px-2 py-1 rounded"
                @click="deleteItem(item.id)"
                title="Delete"
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
import { fetchSaleItems, deleteSaleItem } from "@/services/saleItemService";
import Header from "@/components/Header.vue";

const saleItems = ref([]);
const router = useRouter();

const loadSaleItems = async () => {
  saleItems.value = await fetchSaleItems();
};

const goToAddSaleItem = () => {
  router.push({ name: "new-sale-item-page" });
};

const goToManageBrand = () => {
  router.push({ name: "brands-list-page" });
};

const goToEdit = (id) => {
  router.push({ name: "sale-items-page-byId", params: { id } });
};

const deleteItem = async (id) => {
  if (confirm("Are you sure you want to delete this item?")) {
    await deleteSaleItem(id);
    await loadSaleItems();
  }
};

onMounted(loadSaleItems);
</script>

<style scoped>
/* TailwindCSS is used, but you can add custom styles here if needed */
</style>
