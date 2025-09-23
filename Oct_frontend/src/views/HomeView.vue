<script setup>
import { useRouter } from "vue-router";
import Header from "../components/Header.vue";
import Hero from "../components/Hero.vue";
import Footer from "../components/Footer.vue";

const router = useRouter();

// Handle search updates from Header - redirect to sale items page with search
const handleSearchUpdate = (searchQuery) => {
  if (searchQuery && searchQuery.trim()) {
    // Redirect to sale items page with search query
    router.push({ 
      name: "sale-items-page",
      query: { search: searchQuery.trim() }
    });
  }
};
</script>

<template> 
  <Header 
    :searchValue="null" 
    @search="handleSearchUpdate"
  />
  <Hero/>
  <Footer/>
</template>


<style scoped>

</style>
