<script setup>
import { ref } from "vue";

const searchQuery = ref("");
</script>

<template>
  <!-- แถบแดงบน -->
  <div class="w-full bg-red-600 h-12 flex items-center justify-end px-8">
    <a href="#signin" class="text-white text-sm font-semibold ml-6">Signin</a>
    <a href="#register" class="text-white text-sm font-semibold ml-6"
      >Register</a
    >
  </div>
  <!-- ส่วน header หลัก -->
  <div class="container mx-auto px-5 py-6 flex items-center justify-between">
    <!-- logo + search -->
    <div class="flex items-center space-x-4 flex-1">
      <a href="/"><span class="text-3xl font-bold">ITB MShop</span></a>
      <div class="w-full max-w-md">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search..."
          class="w-full p-2 rounded border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
      </div>
    </div>
    <!-- ไอคอน user/cart -->
    <div class="flex items-center space-x-6 ml-6">
      <!-- user icon -->

      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-6 w-6 text-gray-700"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M5.121 17.804A9 9 0 1112 21a9 9 0 01-6.879-3.196z"
        />
      </svg>
      <!-- cart icon -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-6 w-6 text-gray-700"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13l-1.35 2.7A1 1 0 007 17h10a1 1 0 00.95-.68l3.24-7.24A1 1 0 0020 8H6.21"
        />
      </svg>
    </div>
  </div>
</template>
