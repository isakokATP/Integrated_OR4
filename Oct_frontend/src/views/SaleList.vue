<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import { useRouter } from "vue-router";

const router = useRouter();
function goToAddSaleItem() {
  router.push({ name: "new-sale-item-page" });
}
</script>

<template>
  <Header />
  <div class="max-w-6xl mx-auto mt-2">
    <div class="grid grid-cols-5 gap-8">
      <button
        id="itbms-add-sale-item-button"
        class="bg-blue-900 hover:bg-blue-500 text-white px-6 py-3 rounded text-lg"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
    </div>
  </div>
  <ItemsGallary />
</template>

<style scoped></style>
