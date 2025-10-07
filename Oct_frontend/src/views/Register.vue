<template>
  <div class="max-w-2xl mx-auto p-6">
    <h1 class="text-3xl font-bold mb-6 text-center">Register</h1>
    
    <form @submit.prevent="onSubmit" class="space-y-6">
      <!-- Account Type Selection -->
      <div class="mb-6">
        <label class="block text-lg font-medium mb-3">Account Type</label>
        <div class="flex space-x-4">
          <label class="flex items-center">
            <input 
              v-model="form.userType" 
              type="radio" 
              value="BUYER" 
              class="mr-2"
            />
            Buyer
          </label>
          <label class="flex items-center">
            <input 
              v-model="form.userType" 
              type="radio" 
              value="SELLER" 
              class="mr-2"
            />
            Seller
          </label>
        </div>
      </div>

      <!-- Common Fields -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium mb-1">Nickname *</label>
          <input 
            v-model="form.nickName" 
            type="text" 
            class="w-full border p-2 rounded"
            @blur="trimField('nickName')"
            required
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium mb-1">Email *</label>
          <input 
            v-model="form.email" 
            type="email" 
            class="w-full border p-2 rounded"
            @blur="trimField('email')"
            required
          />
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm font-medium mb-1">Password *</label>
          <input 
            v-model="form.password" 
            type="password" 
            :class="[
              'w-full border p-2 rounded',
              form.password && !isPasswordValid ? 'border-red-500' : 'border-gray-300'
            ]"
            @blur="trimField('password')"
            required
          />
          <p class="text-xs mt-1" :class="form.password && !isPasswordValid ? 'text-red-600' : 'text-gray-600'">
            8-14 characters
          </p>
          <div v-if="form.password && !isPasswordValid" class="text-xs text-red-600 mt-1">
            <ul class="list-disc list-inside">
              <li v-if="form.password.length < 8">At least 8 characters</li>
              <li v-if="form.password.length > 14">Maximum 14 characters</li>
            </ul>
          </div>
        </div>
        
        <div>
          <label class="block text-sm font-medium mb-1">Full Name *</label>
          <input 
            v-model="form.fullName" 
            type="text" 
            :class="[
              'w-full border p-2 rounded',
              form.fullName && !isFullNameValid ? 'border-red-500' : 'border-gray-300'
            ]"
            @blur="trimField('fullName')"
            required
          />
          <p class="text-xs mt-1" :class="form.fullName && !isFullNameValid ? 'text-red-600' : 'text-gray-600'">
            4-40 characters
          </p>
          <div v-if="form.fullName && !isFullNameValid" class="text-xs text-red-600 mt-1">
            <ul class="list-disc list-inside">
              <li v-if="form.fullName.length < 4">At least 4 characters</li>
              <li v-if="form.fullName.length > 40">Maximum 40 characters</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Seller Only Fields -->
      <div v-if="form.userType === 'SELLER'" class="space-y-4 border-t pt-4">
        <h3 class="text-lg font-medium">Seller Information</h3>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">Mobile Number *</label>
            <input 
              v-model="form.phoneNumber" 
              type="tel" 
              class="w-full border p-2 rounded"
              @blur="trimField('phoneNumber')"
              :required="form.userType === 'SELLER'"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium mb-1">Bank Account Number *</label>
            <input 
              v-model="form.bankAccount" 
              type="text" 
              class="w-full border p-2 rounded"
              @blur="trimField('bankAccount')"
              :required="form.userType === 'SELLER'"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">Bank Name *</label>
          <input 
            v-model="form.bankName" 
            type="text" 
            class="w-full border p-2 rounded"
            @blur="trimField('bankName')"
            :required="form.userType === 'SELLER'"
          />
        </div>

        <div>
          <label class="block text-sm font-medium mb-1">National ID Number *</label>
          <input 
            v-model="form.idCardNumber" 
            type="text" 
            class="w-full border p-2 rounded"
            @blur="trimField('idCardNumber')"
            :required="form.userType === 'SELLER'"
          />
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">National ID Photo (Front) *</label>
            <input 
              @change="handleFileChange($event, 'front')"
              type="file" 
              accept="image/*"
              class="w-full border p-2 rounded"
              :required="form.userType === 'SELLER'"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium mb-1">National ID Photo (Back) *</label>
            <input 
              @change="handleFileChange($event, 'back')"
              type="file" 
              accept="image/*"
              class="w-full border p-2 rounded"
              :required="form.userType === 'SELLER'"
            />
          </div>
        </div>
      </div>

      <!-- Submit Button -->
      <div class="pt-4">
        <button 
          type="submit" 
          :disabled="!isFormValid || loading"
          class="px-6 py-2 bg-blue-600 text-white rounded disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Registering...' : 'Submit' }}
        </button>
      </div>
    </form>

    <!-- Cancel Button (outside form) -->
    <div class="mt-4">
      <button 
        type="button" 
        @click="onCancel"
        class="px-6 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors"
      >
        Cancel
      </button>
    </div>

    <!-- Success/Error Messages -->
    <div v-if="message" class="mt-4 p-3 rounded" :class="messageType === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { registerUser } from '../services/userService'

const router = useRouter()

// Form data
const form = ref({
  userType: 'BUYER',
  nickName: '',
  email: '',
  password: '',
  fullName: '',
  phoneNumber: '',
  bankAccount: '',
  bankName: '',
  idCardNumber: '',
  idCardImageFront: null,
  idCardImageBack: null
})

const loading = ref(false)
const message = ref('')
const messageType = ref('')

// Form validation
const isFormValid = computed(() => {
  const commonFields = form.value.nickName && form.value.email && isPasswordValid.value && isFullNameValid.value
  
  if (form.value.userType === 'BUYER') {
    return commonFields
  } else {
    return commonFields && 
           form.value.phoneNumber && 
           form.value.bankAccount && 
           form.value.bankName &&
           form.value.idCardNumber &&
           form.value.idCardImageFront &&
           form.value.idCardImageBack
  }
})

// Password validation - ตรงกับ Backend: 8-14 ตัวอักษร
const isPasswordValid = computed(() => {
  const password = form.value.password
  if (!password) return false
  
  return password.length >= 8 && password.length <= 14
})

// Special character check for template
const hasSpecialChar = computed(() => {
  const password = form.value.password
  if (!password) return false
  return /[!@#$%^&*(),.?":{}|<>]/.test(password)
})

// Full name validation
const isFullNameValid = computed(() => {
  const fullName = form.value.fullName
  return fullName && fullName.length >= 4 && fullName.length <= 40
})

// Handle file upload
function handleFileChange(event, type) {
  const file = event.target.files[0]
  if (type === 'front') {
    form.value.idCardImageFront = file
  } else {
    form.value.idCardImageBack = file
  }
}

// Trim function for lost focus
function trimField(fieldName) {
  if (form.value[fieldName]) {
    form.value[fieldName] = form.value[fieldName].trim()
  }
}

// Form submission
async function onSubmit() {
  if (!isFormValid.value) return
  
  loading.value = true
  message.value = ''
  
  try {
    await registerUser(form.value)
    message.value = 'The user account has been successfully registered.'
    messageType.value = 'success'
    
    // Redirect to gallery page after 2 seconds
    setTimeout(() => {
      router.push({ name: 'sale-items-page' })
    }, 2000)
    
  } catch (error) {
    let errorMessage = 'Registration failed'
    
    if (error.message) {
      if (error.message.includes('email')) {
        errorMessage = 'Email already exists. Please use a different email.'
      } else if (error.message.includes('idCardNumber')) {
        errorMessage = 'National ID number already exists. Please use a different ID number.'
      } else {
        errorMessage = error.message
      }
    }
    
    message.value = errorMessage
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

// Cancel registration
function onCancel() {
  router.push({ name: 'home-page' })
}
</script>
