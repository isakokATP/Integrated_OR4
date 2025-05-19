<script setup>
import Header from "../components/Header.vue";
import ItemsGallary from "../components/ItemsGallary.vue";
import Notification from "../components/Notification.vue";
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, watch } from "vue";

const router = useRouter();
const route = useRoute();
const message = ref("");

function goToAddSaleItem() {
  router.push({ name: "new-sale-item-page" });
}

// Watch for route changes to get message from query params
watch(
  () => route.query.message,
  (newMessage) => {
    if (newMessage) {
      message.value = newMessage;
      // Clear message after 3 seconds
      setTimeout(() => {
        message.value = "";
        // Remove message from URL without refreshing
        router.replace({ query: {} });
      }, 3000);
    }
  },
  { immediate: true }
);
</script>

<template>
  <Header />
  <div class="max-w-6xl mx-auto mt-2">
    <Notification :message="message" />
    <div class="grid grid-cols-5 gap-8">
      <button
        id="itbms-add-sale-item-button"
        class="bg-blue-900 hover:bg-blue-500 text-white px-6 py-3 rounded text-lg transition-colors duration-300"
        @click="goToAddSaleItem"
      >
        Add Sale Item
      </button>
    </div>
  </div>
  <ItemsGallary />
</template>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
