<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { verifyEmail } from "../services/userService";

const route = useRoute();
const router = useRouter();
const status = ref("pending"); // pending | success | error
const message = ref("Verifying...");

onMounted(async () => {
  const token = route.query.token;
  if (!token) {
    status.value = "error";
    message.value = "Missing token.";
    return;
  }
  try {
    const text = await verifyEmail(token);
    status.value = "success";
    message.value = text || "Your account has been successfully activated.";
  } catch (err) {
    status.value = "error";
    message.value = err.message || "Verification failed.";
  }
});

function goLogin(){ router.push({ name: "login-page" }); }
</script>

<template>
  <div class="max-w-xl mx-auto p-6 text-center">
    <h1 class="text-2xl font-bold mb-4">Email Verification</h1>
    <p :class="status==='success' ? 'text-green-700' : status==='error' ? 'text-red-700' : 'text-gray-700'">
      {{ message }}
    </p>
    <div class="mt-6">
      <button @click="goLogin" class="px-4 py-2 rounded bg-blue-600 text-white">Go to Login</button>
    </div>
  </div>
  
</template>


