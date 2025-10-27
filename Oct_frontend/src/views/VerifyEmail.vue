<template>
  <div class="max-w-md mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Email Verification</h1>

    <!-- Loading -->
    <div v-if="loading" class="text-center text-gray-500">
      <p>Verifying your email...</p>
    </div>

    <!-- Message -->
    <div v-else-if="message" :class="messageType === 'success' ? 'bg-green-100 text-green-800 p-4 rounded' : 'bg-red-100 text-red-800 p-4 rounded'">
      <p>{{ message }}</p>
      <div v-if="messageType === 'success'" class="mt-4">
        <router-link 
          to="/login" 
          class="inline-block px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Go to Login
        </router-link>
      </div>
    </div>

    <!-- No token -->
    <div v-else class="text-center">
      <p>No verification token found.</p>
      <router-link 
        to="/register" 
        class="inline-block mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        Register Again
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const loading = ref(false)
const message = ref('')
const messageType = ref('')

// API ผ่าน nginx proxy (ลดปัญหา CORS)
const VERIFY_URL = '/itb-mshop/v2/auth/verify-email'

async function verifyToken(token) {
  try {
    // ใช้ POST method ตาม backend endpoint
    const res = await fetch(`${VERIFY_URL}?token=${encodeURIComponent(token)}`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest' 
      }
    })

    if (!res.ok) {
      const text = await res.text()
      throw new Error(text || 'Verification failed')
    }

    const data = await res.json()
    message.value = `Hello ${data.fullName}, your email has been successfully verified!`
    messageType.value = 'success'

  } catch (err) {
    message.value = err.message || 'Email verification failed'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const token = route.query.token
  if (!token) {
    message.value = 'No verification token found.'
    messageType.value = 'error'
    return
  }
  loading.value = true
  verifyToken(token)
})
</script>
