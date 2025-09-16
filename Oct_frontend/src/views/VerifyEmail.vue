<template>
  <div class="max-w-md mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Email Verification</h1>
    
    <div v-if="loading" class="text-center">
      <p>Verifying your email...</p>
    </div>
    
    <div v-else-if="message" class="p-4 rounded" :class="messageType === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
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
import { useRoute, useRouter } from 'vue-router'
import { verifyEmail } from '../services/userService'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const message = ref('')
const messageType = ref('')

onMounted(async () => {
  const token = route.query.token
  
  if (!token) {
    message.value = 'No verification token found.'
    messageType.value = 'error'
    return
  }
  
  loading.value = true
  
  try {
    const result = await verifyEmail(token)
    message.value = result || 'Your account has been successfully activated.'
    messageType.value = 'success'
  } catch (error) {
    message.value = error.message || 'Email verification failed'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
})
</script>
