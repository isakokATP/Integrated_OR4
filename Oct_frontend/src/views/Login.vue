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

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const email = ref("");
const password = ref("");
const loading = ref(false);
const message = ref("");

// ใช้ endpoint ตรวจสอบอีเมล/รหัสผ่านของ BE (ต้องมี /or4 เพราะ FE อยู่ใต้ base path)
const LOGIN_URL = '/or4/itb-mshop/v2/users/authentications'

async function onSubmit(e){
  e.preventDefault();
  loading.value = true;
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
      message.value = "Logged in successfully!";
      setTimeout(() => router.push({ name: "sale-items-page" }), 1000);
    } else if (response.status === 401) {
      message.value = "Invalid email or password";
    } else {
      message.value = `Login failed (${response.status})`;
    }

  } catch (err) {
    message.value = err.message || "Login failed";
  } finally {
    loading.value = false;
  }
}
</script>
