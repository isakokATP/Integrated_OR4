<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

function goToSaleItem() {
  router.push({ name: "sale-items-page" });
}
</script>

<template> 
  <div class="container mx-auto px-5 overflow-x-hidden lg:overflow-x-visible">
  <section class="flex flex-col lg:flex-row items-center justify-center min-h-screen pb-32">
    <!-- Left Content -->
    <div class="lg:w-5/6 order-2 lg:order-none text-center lg:text-left">
      <h1 class="text-4xl xl:text-5xl font-bold font-theme-heading">ITB MShop</h1>
      <p class="lg:text-xl mt-10 font-theme-content text-theme-grayish-blue pr-64">
        Discover a seamless and enjoyable online shopping experience with ITB MShop — your one-stop destination for quality products, 
        exclusive deals, and fast delivery. Browse our curated collections, enjoy secure checkout, 
        and let us bring your favorite items right to your doorstep
      </p>
      <div class="flex justify-center lg:justify-start mt-10">
        <button class="btn btn-soft btn-primary" @click="goToSaleItem">goToSaleItem</button>
      </div>
    </div>

    <!-- Right Image -->
    <div class="relative order-1 lg:order-none mb-20 lg:mt-0 lg:mb-0 lg:-mr-10">
      <img class="z-10 w-full max-w-lg mx-auto" src="https://img.freepik.com/free-vector/shop-with-sign-we-are-open_23-2148553675.jpg" />
      <div class="-z-10 bg-theme-primary h-52 w-full sm:h-80 sm:w-full rounded-l-full absolute -right-28 md:-right-48 -bottom-8"></div>
    </div>
  </section>
</div>
</template>

<style scoped>

</style>
