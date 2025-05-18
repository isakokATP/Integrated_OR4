<template>
  <Header />
  <div class="p-6">
    <header />
    <div class="flex justify-between mb-4">
      <button
        class="itbms-add-sale-item btn btn-primary"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
      <button
        class="itbms-manage-brand btn btn-secondary"
        @click="goToManageBrand"
      >
        Manage Brand
      </button>
    </div>
    <div class="overflow-x-auto">
      <table class="min-w-full bg-white border border-gray-200">
        <thead>
          <tr>
            <th>Id</th>
            <th>Brand</th>
            <th>Model</th>
            <th>Ram</th>
            <th>Storage</th>
            <th>Color</th>
            <th>Price</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in saleItems"
            :key="item.id"
            class="itbms-row border-t"
          >
            <td class="itbms-id">{{ item.id }}</td>
            <td class="itbms-brand">{{ item.brand }}</td>
            <td class="itbms-model">{{ item.model }}</td>
            <td class="itbms-ramGb">{{ item.ramGb }}</td>
            <td class="itbms-storageGb">{{ item.storageGb }}</td>
            <td class="itbms-color">{{ item.color }}</td>
            <td class="itbms-price">{{ item.price.toLocaleString() }}</td>
            <td>
              <button
                class="itbms-edit-sale-item btn btn-xs btn-outline mr-1"
                @click="goToEdit(item.id)"
              >
                E
              </button>
              <button
                class="itbms-delete-sale-item btn btn-xs btn-outline btn-error"
                @click="deleteItem(item.id)"
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
