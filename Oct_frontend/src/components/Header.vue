<script setup>
import { ref, watch, defineProps, defineEmits, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useCartCount, updateCartCount } from "../composables/useCartCount";

const router = useRouter();
const { cartCount } = useCartCount();

// Force reactivity for login status
const loginTrigger = ref(0);

// Check if user is logged in
const isLoggedIn = computed(() => {
  loginTrigger.value; // Force dependency
  return !!sessionStorage.getItem('accessToken');
});

// Get user nickname from token
const userNickname = computed(() => {
  loginTrigger.value; // Force dependency
  const token = sessionStorage.getItem('accessToken');
  if (!token) return null;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.nickname || null;
  } catch (e) {
    return null;
  }
});

// Get user role from token
const userRole = computed(() => {
  loginTrigger.value; // Force dependency
  const token = sessionStorage.getItem('accessToken');
  if (!token) return null;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.role || payload.userType || null;
  } catch (e) {
    return null;
  }
});

// Logout function
const handleLogout = () => {
  router.push({ name: 'signout-page' });
};

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

// Update cart count on mount and when route changes
onMounted(() => {
  updateCartCount();
});

// Watch for route changes to update cart count and force reactivity
watch(() => router.currentRoute.value.path, () => {
  updateCartCount();
  loginTrigger.value++; // Force login status update
});
</script>

<template>
  <!-- แถบบน -->
  <div class="w-full bg-blue-900 h-12 flex items-center justify-end px-8">
    <!-- Show login/register when not logged in -->
    <template v-if="!isLoggedIn">
      <router-link
        :to="{ name: 'login-page' }"
        class="text-white text-sm font-semibold ml-6 hover:text-amber-400 transition"
        >Signin</router-link>
      <router-link
        :to="{ name: 'register-page' }"
        class="text-white text-sm font-semibold ml-6 hover:text-amber-400 transition"
        >Register</router-link>
    </template>
    
    <!-- Show nickname, your orders, and logout when logged in -->
    <template v-else>
      <span class="text-white text-sm font-semibold ml-6">
        Welcome, {{ userNickname || 'User' }}
      </span>
      <router-link
        :to="{ name: 'your-orders-page' }"
        class="text-white text-sm font-semibold ml-6 hover:text-amber-400 transition"
      >
        Your Orders
      </router-link>
      <button
        @click="handleLogout"
        class="text-white text-sm font-semibold ml-6 hover:text-amber-400 transition cursor-pointer"
      >
        Logout
      </button>
    </template>
  </div>
  <!-- ส่วน header หลัก -->
  <div class="container mx-auto px-5 py-6 flex items-center justify-between">
    <!-- logo + search -->
    <div class="flex items-center space-x-6 flex-1">
      <router-link to="/" class="group">
        <span class="text-3xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent group-hover:from-blue-700 group-hover:to-purple-700 transition-all duration-300">
          ITB MShops
        </span>
      </router-link>
      
      <!-- Enhanced Search Bar -->
      <div class="w-full max-w-lg relative">
        <div class="relative">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Search for description, model, or color..."
            @keyup="handleSearch"
            class="w-full p-3 pr-24 rounded-xl border-2 border-gray-200 focus:border-blue-400 focus:outline-none focus:ring-4 focus:ring-blue-100 transition-all duration-300 shadow-sm hover:shadow-md bg-white"
          />
          
          <!-- Search Icon -->
          <button
            @click="handleSearchClick"
            class="absolute right-16 top-1/2 transform -translate-y-1/2 p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all duration-200"
            title="Search"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
            </svg>
          </button>
          
          <!-- Clear Search Button -->
          <button
            v-if="searchQuery"
            @click="clearSearch"
            class="absolute right-2 top-1/2 transform -translate-y-1/2 p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all duration-200"
            title="Clear search only"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>
    
    <!-- Enhanced User/Cart Icons -->
    <div class="flex items-center space-x-4 ml-6">
      <!-- user icon -->
      <router-link
        :to="{ name: 'profile-page' }"
        class="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all duration-200 group"
        title="Profile"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-6 h-6 group-hover:scale-110 transition-transform duration-200"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z"
          />
        </svg>
      </router-link>

      <!-- cart icon -->
      <router-link
        :to="{ name: 'cart-page' }"
        class="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all duration-200 group relative"
        title="Cart"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="w-6 h-6 group-hover:scale-110 transition-transform duration-200"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m11.356-1.993 1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 0 1-1.12-1.243l1.264-12A1.125 1.125 0 0 1 5.513 7.5h12.974c.576 0 1.059.435 1.119 1.007ZM8.625 10.5a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm7.5 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z"
          />
        </svg>
        <!-- Cart Badge -->
        <span v-if="cartCount > 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-bold">
          {{ cartCount }}
        </span>
      </router-link>
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
