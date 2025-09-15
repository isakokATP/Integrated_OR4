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
    // Stub: replace with real BE login when available
    await new Promise(r => setTimeout(r, 400));
    message.value = "Logged in (stub).";
    router.push({ name: "sale-items-page" });
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


