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
          :maxlength="MAX_EMAIL_LENGTH"
        />
      </div>
      <div>
        <label class="block text-sm font-medium">Password</label>
        <input v-model="password" type="password" class="w-full border p-2 rounded" :maxlength="MAX_PASSWORD_LENGTH" />
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

// Spec constants
const MAX_EMAIL_LENGTH = 50;
const MAX_PASSWORD_LENGTH = 14;
// Simple email format check (HTML5-like)
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// Enable 'Sign In' only if email follows standard format and password is NOT empty
const isFormValid = computed(() => {
  const isEmailValid = EMAIL_REGEX.test(email.value) && email.value.length <= MAX_EMAIL_LENGTH;
  const isPasswordPresent = password.value.length > 0 && password.value.length <= MAX_PASSWORD_LENGTH;
  return Boolean(isEmailValid && isPasswordPresent);
});

// ใช้ endpoint ตรวจสอบอีเมล/รหัสผ่านของ BE (ต้องมี /or4 เพราะ FE อยู่ใต้ base path)
const LOGIN_URL = '/or4/itb-mshop/v2/auth/login'

async function onSubmit(e){
  e.preventDefault();
  loading.value = true;
  message.value = "";
  messageType.value = "";
  
  try {
    const response = await fetch(`${LOGIN_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      },
      // Do not trim per spec; send raw values
      body: JSON.stringify({ email: email.value, password: password.value })
    });

    if (response.ok) {
      const data = await response.json();
      const accessToken = data.accessToken;
      const refreshToken = data.refreshToken;

      // Decode JWT (no verify) to get nickname claim
      const payloadPart = accessToken.split('.')[1];
      const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64 + '='.repeat((4 - (base64.length % 4)) % 4);
      const payloadJson = JSON.parse(atob(padded));
      const nickname = payloadJson.nickname || 'User';

      // Store tokens and nickname for later pages to use
      sessionStorage.setItem('accessToken', accessToken);
      sessionStorage.setItem('refreshToken', refreshToken);
      sessionStorage.setItem('nickname', nickname);

      // Redirect to sale items page; page can read nickname from storage
      router.push({ name: "sale-items-page" });
    } else if (response.status === 401) {
      message.value = "Email or Password is incorrect.";
      messageType.value = "error";
    } else if (response.status === 403) {
      message.value = "You need to activate your account before signing in.";
      messageType.value = "error";
    } else {
      message.value = `There is a problem. Please try again later.`;
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
