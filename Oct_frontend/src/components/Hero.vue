<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

function goToSaleItem() {
  router.push({ name: "sale-items-page" });
}

function goToSaleItemList() {
  router.push({ name: "sale-items-list-page" });
}
</script>

<template>
  <div class="container mx-auto px-5 overflow-x-hidden">
    <section
      class="flex flex-col items-center justify-start min-h-[calc(100vh-96px-64px)] pb-10 relative pt-0"
    >
      <!-- Image -->
      <div class="relative mb-8 max-w-[720px] w-full mx-auto overflow-hidden rounded-xl -mt-2">
        <img
          src="../assets/homepage.jpg"
          alt="Mobile Home Page"
          class="z-10 w-full h-[450px] object-cover rounded-xl"
        />
        <div
          class="-z-10 bg-theme-primary h-52 w-full sm:h-80 rounded-full absolute -bottom-8 left-1/2 -translate-x-1/2"
        ></div>
      </div>

      <!-- Buttons -->
      <div class="flex justify-center space-x-4 max-w-md mx-auto w-full">
        <button
          class="flex-1 py-3 text-lg bg-blue-900 hover:bg-blue-500 text-white rounded transition duration-200"
          @click="goToSaleItem"
        >
          Shop now
        </button>
        <button
          class="flex-1 py-3 text-lg bg-blue-900 hover:bg-blue-500 text-white rounded transition duration-200"
          @click="goToSaleItemList"
        >
          Seller
        </button>
      </div>
    </section>
  </div>
</template>

<style scoped></style>