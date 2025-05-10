<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import { useRouter } from "vue-router";

const router = useRouter();
function goToAddSaleItem() {
  router.push("/sale-items/add");
}
</script>

<template>
  <Header />
  <div class="max-w-6xl mx-auto mt-8">
    <div class="grid grid-cols-5 gap-8">
      <button
        id="itbms-add-sale-item-button"
        class="bg-blue-600 text-white px-4 py-2 rounded"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
    </div>
  </div>
  <ItemsGallary />
</template>

<style scoped></style>
