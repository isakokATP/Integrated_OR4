<template>
  <div class="max-w-md mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Sign In</h1>
    <form @submit.prevent="onSubmit" class="space-y-4">
      <div>
        <label class="block text-sm font-medium">Email</label>
        <input 
          v-model="email" 
          type="email" 
          placeholder="itbkk.somchai@ad.sit.kmutt.ac.th"
          class="w-full border p-2 rounded" 
        />
      </div>
      <div>
        <label class="block text-sm font-medium">Password</label>
        <input v-model="password" type="password" class="w-full border p-2 rounded" />
      </div>
      <button 
        :disabled="!isFormValid || loading" 
        class="px-4 py-2 rounded bg-blue-600 text-white disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {{ loading ? 'Signing In...' : 'Sign In' }}
      </button>
    </form>
    <p v-if="message" class="mt-3 text-sm" :class="messageType === 'error' ? 'text-red-600' : 'text-green-600'">
      {{ message }}
    </p>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const email = ref("");
const password = ref("");
const loading = ref(false);
const message = ref("");
const messageType = ref("");

// Check if form is valid (both email and password are filled)
const isFormValid = computed(() => {
  return email.value.trim() && password.value.trim();
});

// ใช้ endpoint ตรวจสอบอีเมล/รหัสผ่านของ BE (ต้องมี /or4 เพราะ FE อยู่ใต้ base path)
const LOGIN_URL = '/or4/itb-mshop/v2/users/authentications'

async function onSubmit(e){
  e.preventDefault();
  loading.value = true;
  message.value = "";
  messageType.value = "";
  
  try {
    if (!email.value || !password.value) throw new Error("Email and password are required");

    const response = await fetch(`${LOGIN_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      },
      body: JSON.stringify({ email: email.value, password: password.value })
    });

    if (response.ok) {
      // ต้องเรียก API เพื่อดึงข้อมูล user และแสดงชื่อเล่น
      const userResponse = await fetch(`/or4/itb-mshop/v2/users?email=${encodeURIComponent(email.value)}`, {
        method: 'GET',
        headers: {
          'X-Requested-With': 'XMLHttpRequest'
        }
      });
      
      if (userResponse.ok) {
        const userData = await userResponse.json();
        message.value = `Welcome ${userData.nickName || 'User'}!`;
        messageType.value = "success";
        setTimeout(() => router.push({ name: "sale-items-page" }), 1000);
      } else {
        message.value = "Logged in successfully!";
        messageType.value = "success";
        setTimeout(() => router.push({ name: "sale-items-page" }), 1000);
      }
    } else if (response.status === 401) {
      message.value = "Email or Password is incorrect.";
      messageType.value = "error";
    } else if (response.status === 403) {
      message.value = "You need to activate your account before signing in.";
      messageType.value = "error";
    } else {
      message.value = `Login failed (${response.status})`;
      messageType.value = "error";
    }

  } catch (err) {
    message.value = err.message || "Login failed";
    messageType.value = "error";
  } finally {
    loading.value = false;
  }
}
</script>
