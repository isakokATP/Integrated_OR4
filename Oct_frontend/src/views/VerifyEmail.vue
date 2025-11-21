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
const VERIFY_URL = `${import.meta.env.BASE_URL}itb-mshop/v2/auth/verify-email`

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

    // อ่าน response เป็น text ก่อน เพื่อตรวจสอบว่าเป็น JSON หรือ string
    const responseText = await res.text()
    
    if (!res.ok) {
      // ถ้า error พยายาม parse เป็น JSON เพื่อดึง message
      try {
        const errorData = JSON.parse(responseText)
        throw new Error(errorData.message || errorData.error || responseText || `Verification failed (${res.status})`)
      } catch (parseError) {
        // ถ้า parse ไม่ได้ แสดง error message โดยตรง
        throw new Error(responseText || `Verification failed (${res.status})`)
      }
    }

    // ถ้า success พยายาม parse เป็น JSON
    try {
      const data = JSON.parse(responseText)
      message.value = `Hello ${data.fullName}, your email has been successfully verified!`
      messageType.value = 'success'
    } catch (parseError) {
      // ถ้า parse ไม่ได้ แต่ status ok ก็ถือว่าสำเร็จ
      message.value = responseText || 'Email verification successful!'
      messageType.value = 'success'
    }

  } catch (err) {
    console.error('Verification error:', err)
    message.value = err.message || 'Email verification failed. Please try again or contact support.'
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
