<script setup>
import { ref } from "vue";

const searchQuery = ref("");
</script>

<template>
  <!-- Navbar Start -->
  <div class="container mx-auto px-5">
    <header class="flex justify-between py-12 items-center">
      <div class="flex items-center space-x-10">
        <a href="/" class="flex items-center space-x-2">
          <img
            src="https://img.freepik.com/free-vector/shop-with-sign-we-are-open_23-2148553675.jpg"
            alt="Site Logo"
            class="w-10 h-10 object-cover rounded"
          />
          <span class="text-2xl font-semibold ml-1"> ITB MShop </span>
        </a>
        <div class="w-full max-w-md">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="ค้นหา..."
            class="w-full p-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>
      </div>

      <nav class="flex justify-end flex-col md:flex-row">
        <!-- Desktop Menu -->
        <ul
          class="space-x-10 font-theme-heading font-medium items-center hidden md:flex flex-col space-y-7 md:space-y-0 md:flex-row"
        >
          <li
            class="text-xl uppercase hover:text-theme-secondary transition duration-200"
          >
            <a href="#sign in" v-smooth-scroll>sign in</a>
          </li>
          <li
            class="text-xl uppercase hover:text-theme-secondary transition duration-200"
          >
            <a href="#register" v-smooth-scroll>register</a>
          </li>
        </ul>
      </nav>
    </header>
  </div>
  <!-- Navbar End -->
</template>
