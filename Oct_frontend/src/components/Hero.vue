<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

function goToSaleItem() {
  router.push({ name: "sale-items-page" });
}
</script>

<template>
  <div class="container mx-auto px-5 overflow-x-hidden">
    <section
      class="flex flex-col lg:flex-row items-center justify-center min-h-[calc(100vh-96px-64px)] pb-10 relative"
    >
      <!-- Left Content -->
      <div class="lg:w-5/6 order-2 lg:order-none text-center lg:text-left">
        <h1 class="text-4xl xl:text-5xl font-bold font-theme-heading">
          ITB MShop
        </h1>
        <div class="flex justify-center lg:justify-start mt-10">
          <button class="btn btn-soft btn-primary" @click="goToSaleItem">
            Shop now
          </button>
        </div>
      </div>

      <!-- Right Image -->
      <div
        class="relative order-1 lg:order-none mb-8 lg:mt-0 lg:mb-0 lg:-mr-10"
      >
        <img
          class="z-10 w-full max-w-lg mx-auto"
          src="https://img.freepik.com/free-vector/shop-with-sign-we-are-open_23-2148553675.jpg"
        />
        <div
          class="-z-10 bg-theme-primary h-52 w-full sm:h-80 sm:w-full rounded-l-full absolute -right-16 md:-right-24 -bottom-8"
        ></div>
      </div>
    </section>
  </div>
</template>

<style scoped></style>
