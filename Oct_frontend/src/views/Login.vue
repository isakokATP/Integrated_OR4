<template>
  <div class="max-w-md mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Login</h1>
    <form @submit.prevent="onSubmit" class="space-y-4">
      <div>
        <label class="block text-sm font-medium">Email</label>
        <input v-model="email" type="email" class="w-full border p-2 rounded" />
      </div>
      <div>
        <label class="block text-sm font-medium">Password</label>
        <input v-model="password" type="password" class="w-full border p-2 rounded" />
      </div>
      <button :disabled="loading" class="px-4 py-2 rounded bg-blue-600 text-white disabled:opacity-50">Login</button>
      <button 
        @click="router.push({ name: 'home-page' })"
          type="button" 
          class="px-6 py-2 bg-gray-500 text-white rounded"
        >
          cancel</button>
    </form>
    <p v-if="message" class="mt-3 text-sm">{{ message }}</p>
    
  </div>

</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const email = ref("");
const password = ref("");
const loading = ref(false);
const message = ref("");

async function onSubmit(e){
  e.preventDefault();
  loading.value = true;
  try {
    if (!email.value || !password.value) throw new Error("Email and password are required");

    const response = await fetch(`${import.meta.env.BASE_URL}itb-mshop/v2/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: email.value,
        password: password.value
      }),
      credentials: 'include' // Important for cookies
    });

    if (response.ok) {
      const data = await response.json();
      
      // Store access token
      if (data.accessToken) {
        sessionStorage.setItem('accessToken', data.accessToken);
        
        // Decode JWT token to get user role
        try {
          const payload = JSON.parse(atob(data.accessToken.split('.')[1]));
          const userRole = payload.role;
          
          message.value = "Logged in successfully!";
          
          // Redirect based on user role
          if (userRole === 'SELLER') {
            setTimeout(() => router.push({ name: "sale-items-list-page" }), 1000);
          } else {
            setTimeout(() => router.push({ name: "sale-items-page" }), 1000);
          }
        } catch (e) {
          console.error('Error decoding token:', e);
          message.value = "Logged in successfully!";
          setTimeout(() => router.push({ name: "sale-items-page" }), 1000);
        }
      } else {
        message.value = "Logged in successfully!";
        setTimeout(() => router.push({ name: "sale-items-page" }), 1000);
      }
    } else {
      const errorData = await response.json();
      
      // Handle validation errors (errors object)
      if (errorData.errors && typeof errorData.errors === 'object') {
        // Extract error messages from errors object
        const errorMessages = Object.values(errorData.errors);
        message.value = errorMessages.join(', ') || "Validation failed";
      } 
      // Handle simple error message
      else if (errorData.message) {
        message.value = errorData.message;
      } 
      // Fallback
      else {
        message.value = "Invalid email or password";
      }
    }

  } catch (err) {
    message.value = err.message || "Login failed";
  } finally {
    loading.value = false;
  }
}
</script>
