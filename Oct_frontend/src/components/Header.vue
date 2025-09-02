<script setup>
import { ref, watch, defineProps, defineEmits } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// Props for search value from parent
const props = defineProps({
  searchValue: {
    type: String,
    default: ""
  }
});

const searchQuery = ref(props.searchValue);

// Emit search query to parent component
const emit = defineEmits(["search"]);

// Watch for search query changes and emit to parent
watch(searchQuery, (newQuery) => {
  emit("search", newQuery);
});

// Watch for props changes to sync search input
watch(() => props.searchValue, (newValue) => {
  searchQuery.value = newValue || "";
});

// Handle Enter key press
function handleSearch(event) {
  if (event.key === "Enter") {
    emit("search", searchQuery.value);
  }
}

// Handle search icon click
function handleSearchClick() {
  emit("search", searchQuery.value);
}

// Clear search
function clearSearch() {
  searchQuery.value = "";
  emit("search", "");
}
</script>

<template>
  <!-- แถบบน -->
  <div class="w-full bg-blue-900 h-12 flex items-center justify-end px-8">
    <a
      href="#signin"
      class="text-white text-sm font-semibold ml-6 hover:text-amber-400 transition"
      >Signin</a
    >
    <a
      href="#register"
      class="text-white text-sm font-semibold ml-6 hover:text-amber-400 transition"
      >Register</a
    >
  </div>
  <!-- ส่วน header หลัก -->
  <div class="container mx-auto px-5 py-6 flex items-center justify-between">
    <!-- logo + search -->
    <div class="flex items-center space-x-4 flex-1">
      <router-link to="/">
        <span class="text-3xl font-bold">ITB MShop</span>
      </router-link>
      <div class="w-full max-w-md relative">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search for description, model, or color..."
          @keyup="handleSearch"
          class="w-full p-2 pr-20 rounded border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
        
        <!-- Search Icon -->
        <button
          @click="handleSearchClick"
          class="absolute right-12 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-blue-600 p-1"
          title="Search"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
          </svg>
        </button>
        
        <!-- Clear Button -->
        <button
          v-if="searchQuery"
          @click="clearSearch"
          class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-red-600 p-1"
          title="Clear search"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>
    <!-- ไอคอน user/cart -->
    <div class="flex items-center space-x-6 ml-6">
      <!-- user icon -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="size-7 icon-hover"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"
        />
      </svg>

      <!-- cart icon -->
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke-width="1.5"
        stroke="currentColor"
        class="size-7 icon-hover"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          d="M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m11.356-1.993 1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 0 1-1.12-1.243l1.264-12A1.125 1.125 0 0 1 5.513 7.5h12.974c.576 0 1.059.435 1.119 1.007ZM8.625 10.5a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm7.5 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z"
        />
      </svg>
    </div>
  </div>
</template>

<style scoped>
.icon-hover {
  cursor: pointer;
  transition: stroke 0.2s ease;
}

.icon-hover:hover {
  stroke: #2563eb; /* สีฟ้าอ่อน (Tailwind's blue-300) */
}
</style>
