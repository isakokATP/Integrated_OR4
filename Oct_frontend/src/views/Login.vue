<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { saveJwtToken } from "../services/auth";
import { loginUser } from "../services/authService";

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
    
    // ใช้ JWT login API
    const response = await loginUser(email.value, password.value);
    
    // เก็บ JWT token ไว้ใน session
    saveJwtToken(response.token);
    message.value = "Logged in successfully!";
    
    // Redirect ไปหน้า gallery
    setTimeout(() => {
      router.push({ name: "sale-items-page" });
    }, 1000);
    
  } catch (err) {
    message.value = err.message || "Login failed";
  } finally {
    loading.value = false;
  }
}
</script>

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
    </form>
    <p v-if="message" class="mt-3 text-sm">{{ message }}</p>
  </div>
</template>


