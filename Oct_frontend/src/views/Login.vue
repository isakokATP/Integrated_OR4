<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { saveUserCredentials } from "../services/auth";

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
    
    // ตรวจสอบว่า user มีอยู่ในระบบหรือไม่
    // ใช้ API ที่มีอยู่แล้วเพื่อตรวจสอบ user
    const response = await fetch(`http://localhost:8080/itb-mshop/v2/users?email=${email.value}`, {
      method: 'GET',
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      },
      credentials: 'omit'
    });
    
    if (response.ok) {
      const userData = await response.json();
      
      // ตรวจสอบว่า user active หรือไม่
      if (userData.active) {
        // เก็บ user credentials ไว้ใน session (สำหรับการแสดงผล)
        saveUserCredentials(email.value, password.value);
        message.value = "Logged in successfully!";
        
        // Redirect ไปหน้า gallery
        setTimeout(() => {
          router.push({ name: "sale-items-page" });
        }, 1000);
      } else {
        message.value = "Account is not active. Please verify your email first.";
      }
    } else {
      message.value = "Invalid email or password";
    }
    
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


