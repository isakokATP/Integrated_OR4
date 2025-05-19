<template>
  <div
    v-if="message"
    class="itbms-message mb-4 p-4 bg-green-50 border-l-4 border-green-500 text-green-700 rounded shadow-md flex items-center animate-fade-in-out"
  >
    <svg
      class="w-6 h-6 mr-3 text-green-500"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M5 13l4 4L19 7"
      ></path>
    </svg>
    <span class="font-medium">{{ message }}</span>
  </div>
</template>

<script setup>
import { defineProps } from "vue";

defineProps({
  message: {
    type: String,
    default: "",
  },
  type: {
    type: String,
    default: "success",
  },
});
</script>

<style scoped>
.animate-fade-in-out {
  animation: fadeInOut 3s ease-in-out;
}

@keyframes fadeInOut {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  10% {
    opacity: 1;
    transform: translateY(0);
  }
  90% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(-10px);
  }
}
</style>
