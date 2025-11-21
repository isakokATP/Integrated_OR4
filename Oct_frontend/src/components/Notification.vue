<template>
  <div
    v-if="message"
    :class="[
      'itbms-message fixed top-4 left-1/2 transform -translate-x-1/2 z-50 max-w-md w-full mx-4 p-4 border-l-4 rounded shadow-lg flex items-center animate-fade-in-out',
      type === 'success' 
        ? 'bg-green-50 border-green-500 text-green-700' 
        : 'bg-red-50 border-red-500 text-red-700'
    ]"
  >
    <svg
      v-if="type === 'success'"
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
    <svg
      v-else
      class="w-6 h-6 mr-3 text-red-500"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M6 18L18 6M6 6l12 12"
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
