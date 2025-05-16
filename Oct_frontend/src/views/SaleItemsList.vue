<template>
  <Header />
  <div class="p-6">
    <header />
    <div class="flex justify-between mb-4">
      <button
        class="btn btn-primary"
        data-testid="itbms-sale-item-add"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
      <button
        class="btn btn-secondary"
        data-testid="itbms-manage-brand"
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
            data-testid="itbms-row"
            class="border-t"
          >
            <td data-testid="itbms-id">{{ item.id }}</td>
            <td data-testid="itbms-brand">{{ item.brand }}</td>
            <td data-testid="itbms-model">{{ item.model }}</td>
            <td data-testid="itbms-ramGb">{{ item.ramGb }}</td>
            <td data-testid="itbms-storageGb">{{ item.storageGb }}</td>
            <td data-testid="itbms-color">{{ item.color }}</td>
            <td data-testid="itbms-price">{{ item.price.toLocaleString() }}</td>
            <td>
              <button
                class="btn btn-xs btn-outline mr-1"
                data-testid="itbms-edit-button"
                @click="goToEdit(item.id)"
              >
                E
              </button>
              <button
                class="btn btn-xs btn-outline btn-error"
                data-testid="itbms-delete-button"
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
  router.push("/sale-items/add");
};

const goToManageBrand = () => {
  router.push("/brands");
};

const goToEdit = (id) => {
  router.push(`/sale-items/edit/${id}`);
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
