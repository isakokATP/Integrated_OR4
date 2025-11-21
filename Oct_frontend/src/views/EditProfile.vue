<template>
  <div class="max-w-4xl mx-auto p-6">
    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-error">
      <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span>{{ error }}</span>
    </div>

    <!-- Edit Profile Form -->
    <div v-else-if="originalProfile" class="space-y-6">
      <!-- Header -->
      <div class="flex justify-between items-start">
        <div>
          <h1 class="text-3xl font-bold text-gray-800">Edit Profile</h1>
          <p class="text-gray-600 mt-1">Update your account information</p>
        </div>
        <div class="badge badge-lg" :class="originalProfile.userType === 'SELLER' ? 'badge-success' : 'badge-info'">
          {{ originalProfile.userType }}
        </div>
      </div>

      <!-- User Info Card -->
      <div class="card bg-white shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-4">Personal Information</h2>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <!-- Nickname -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Nickname *</span>
              </label>
              <input
                v-model="formData.nickName"
                type="text"
                :class="['input', hasError('nickName') ? 'input-error' : 'input-bordered']"
                placeholder="Enter nickname"
              />
              <label v-if="hasError('nickName')" class="label">
                <span class="label-text-alt text-red-500">{{ getError('nickName') }}</span>
              </label>
            </div>

            <!-- Email -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Email</span>
              </label>
              <input
                v-model="formData.email"
                type="email"
                disabled
                class="input input-bordered input-disabled"
              />
            </div>

            <!-- Full Name -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Full Name *</span>
              </label>
              <input
                v-model="formData.fullName"
                type="text"
                :class="['input', hasError('fullName') ? 'input-error' : 'input-bordered']"
                placeholder="Enter full name"
              />
              <label v-if="hasError('fullName')" class="label">
                <span class="label-text-alt text-red-500">{{ getError('fullName') }}</span>
              </label>
            </div>

            <!-- Password -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Password</span>
              </label>
              <input
                type="password"
                value="••••••••"
                disabled
                class="input input-bordered input-disabled"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Seller-specific Information (Read-only with masking) -->
      <div v-if="originalProfile.userType === 'SELLER'" class="card bg-gradient-to-br from-blue-50 to-purple-50 shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-4 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 mr-2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 013 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 00-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 01-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 003 15h-.75M15 10.5a3 3 0 11-6 0 3 3 0 016 0zm3 0h.008v.008H18V10.5zm-12 0h.008v.008H6V10.5z" />
            </svg>
            Seller Account Details (Read-only)
          </h2>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <!-- Phone Number (Masked) -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Phone Number</span>
              </label>
              <input
                :value="maskPhoneNumber(originalProfile.phoneNumber)"
                type="text"
                disabled
                class="input input-bordered input-disabled bg-white"
              />
            </div>

            <!-- Bank Account (Masked) -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Bank Account</span>
              </label>
              <input
                :value="maskBankAccount(originalProfile.bankAccount)"
                type="text"
                disabled
                class="input input-bordered input-disabled bg-white"
              />
            </div>

            <!-- ID Card Number (Masked) -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">National ID Number</span>
              </label>
              <input
                :value="maskIdCard(originalProfile.idCardNumber)"
                type="text"
                disabled
                class="input input-bordered input-disabled bg-white"
              />
            </div>

            <!-- Bank Name -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Bank Name</span>
              </label>
              <input
                :value="originalProfile.bankName"
                type="text"
                disabled
                class="input input-bordered input-disabled bg-white"
              />
            </div>

            <!-- ID Card Images -->
            <div class="form-control col-span-2">
              <label class="label">
                <span class="label-text font-semibold">National ID Photos</span>
              </label>
              <div class="flex gap-4">
                <div class="flex-1">
                  <label class="label-text text-sm">Front</label>
                  <div class="w-full p-4 border-2 border-dashed rounded-lg text-center bg-gray-50">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8 mx-auto text-gray-400 mb-2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="m2.25 15.75 5.159-5.159a2.25 2.25 0 0 1 3.182 0l5.159 5.159m-1.5-1.5 1.409-1.409a2.25 2.25 0 0 1 3.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 0 0 1.5-1.5V6a1.5 1.5 0 0 0-1.5-1.5H3.75A1.5 1.5 0 0 0 2.25 6v12.75a1.5 1.5 0 0 0 1.5 1.5Zm10.5-11.25h.008v.008h-.008V8.25Zm.375 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z" />
                    </svg>
                    <p class="text-gray-600">Provided</p>
                  </div>
                </div>
                <div class="flex-1">
                  <label class="label-text text-sm">Back</label>
                  <div class="w-full p-4 border-2 border-dashed rounded-lg text-center bg-gray-50">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-8 h-8 mx-auto text-gray-400 mb-2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="m2.25 15.75 5.159-5.159a2.25 2.25 0 0 1 3.182 0l5.159 5.159m-1.5-1.5 1.409-1.409a2.25 2.25 0 0 1 3.182 0l2.909 2.909m-18 3.75h16.5a1.5 1.5 0 0 0 1.5-1.5V6a1.5 1.5 0 0 0-1.5-1.5H3.75A1.5 1.5 0 0 0 2.25 6v12.75a1.5 1.5 0 0 0 1.5 1.5Zm10.5-11.25h.008v.008h-.008V8.25Zm.375 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z" />
                    </svg>
                    <p class="text-gray-600">Provided</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex justify-end gap-4">
        <button @click="cancel" class="btn btn-outline">
          Cancel
        </button>
        <button 
          @click="saveProfile" 
          :disabled="!canSave || saving"
          class="btn btn-primary"
        >
          <span v-if="saving" class="loading loading-spinner loading-sm"></span>
          {{ saving ? 'Saving...' : 'Save' }}
        </button>
      </div>

      <!-- Success/Error Message -->
      <div v-if="message" class="mt-4">
        <div :class="messageType === 'success' ? 'alert alert-success' : 'alert alert-error'">
          <span>{{ message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const loading = ref(true);
const saving = ref(false);
const error = ref('');
const message = ref('');
const messageType = ref('');
const originalProfile = ref(null);
const formData = ref({
  nickName: '',
  fullName: '',
  email: '',
  password: ''
});
const errors = ref({});

// Validation rules
const validateField = (field, value) => {
  errors.value[field] = '';
  
  if (field === 'nickName' && !value.trim()) {
    errors.value[field] = 'Nickname is required';
    return false;
  }
  if (field === 'fullName') {
    if (!value.trim()) {
      errors.value[field] = 'Full name is required';
      return false;
    }
    if (value.trim().length < 4 || value.trim().length > 40) {
      errors.value[field] = 'Full name must be between 4 and 40 characters';
      return false;
    }
  }
  if (field === 'email' && value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    errors.value[field] = 'Invalid email format';
    return false;
  }
  if (field === 'password' && value && value.length < 8) {
    errors.value[field] = 'Password must be at least 8 characters';
    return false;
  }
  
  return true;
};

const hasError = (field) => !!errors.value[field];
const getError = (field) => errors.value[field];

// Check if form has changes
const hasChanges = computed(() => {
  if (!originalProfile.value) return false;
  
  // For both BUYER and SELLER, only nickname and fullname are editable
  const originalNickName = originalProfile.value.nickName || originalProfile.value.nickname || '';
  const originalFullName = originalProfile.value.fullName || originalProfile.value.fullname || '';
  
  return formData.value.nickName !== originalNickName ||
         formData.value.fullName !== originalFullName;
});

// Check if form is valid
const isFormValid = computed(() => {
  // For both BUYER and SELLER, only nickname and fullname are editable
  return validateField('nickName', formData.value.nickName) &&
         validateField('fullName', formData.value.fullName);
});

// Can save only if has changes and form is valid
const canSave = computed(() => hasChanges.value && isFormValid.value);

// Mask phone number - show 2nd, 3rd, 4th digits from end
// Example: 0123456789 -> xxxxx678x
const maskPhoneNumber = (phone) => {
  if (!phone) return '';
  if (phone.length <= 4) return phone;
  // Show digits at positions -4, -3, -2 (2nd, 3rd, 4th from end)
  const visiblePart = phone.slice(-4, -1); // positions -4 to -2 (3 digits)
  const maskedLength = phone.length - 4; // mask everything except last 4
  return 'x'.repeat(maskedLength) + visiblePart + 'x'; // last digit is also masked
};

// Mask bank account - show 2nd, 3rd, 4th digits from end
// Example: 0123456789 -> xxxxx678x
const maskBankAccount = (account) => {
  if (!account) return '';
  if (account.length <= 4) return account;
  // Show digits at positions -4, -3, -2 (2nd, 3rd, 4th from end)
  const visiblePart = account.slice(-4, -1); // positions -4 to -2 (3 digits)
  const maskedLength = account.length - 4; // mask everything except last 4
  return 'x'.repeat(maskedLength) + visiblePart + 'x'; // last digit is also masked
};

// Mask ID card - show 2nd, 3rd, 4th digits from end
// Example: 0123456789 -> xxxxx678x
const maskIdCard = (idCard) => {
  if (!idCard) return '';
  if (idCard.length <= 4) return idCard;
  // Show digits at positions -4, -3, -2 (2nd, 3rd, 4th from end)
  const visiblePart = idCard.slice(-4, -1); // positions -4 to -2 (3 digits)
  const maskedLength = idCard.length - 4; // mask everything except last 4
  return 'x'.repeat(maskedLength) + visiblePart + 'x'; // last digit is also masked
};

// Fetch current profile
const fetchProfile = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    const userId = getCurrentUserId();
    if (!userId) return;

    const token = sessionStorage.getItem('accessToken');
    if (!token) {
      error.value = 'Not authenticated';
      router.push({ name: 'login-page' });
      return;
    }

    const response = await fetch(`${import.meta.env.BASE_URL}itb-mshop/v2/users/${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      if (response.status === 401) {
        error.value = 'Please log in again';
        router.push({ name: 'login-page' });
        return;
      }
      throw new Error(`Failed to fetch profile: ${response.status}`);
    }

    originalProfile.value = await response.json();
    
    // Initialize form data with current values
    formData.value = {
      nickName: originalProfile.value.nickName || originalProfile.value.nickname || '',
      fullName: originalProfile.value.fullName || originalProfile.value.fullname || '',
      email: originalProfile.value.email || '',
      password: ''
    };
  } catch (err) {
    error.value = err.message || 'Failed to load profile';
    console.error('Error fetching profile:', err);
  } finally {
    loading.value = false;
  }
};

// Get current user ID from token
const getCurrentUserId = () => {
  const token = sessionStorage.getItem('accessToken');
  if (!token) {
    router.push({ name: 'login-page' });
    return null;
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id;
  } catch (e) {
    console.error('Error decoding token:', e);
    router.push({ name: 'login-page' });
    return null;
  }
};

// Save profile
const saveProfile = async () => {
  if (!canSave.value || saving.value) return;
  
  saving.value = true;
  message.value = '';
  messageType.value = '';
  errors.value = {};
  
  try {
    // Validate all fields (only nickname and fullname are editable)
    validateField('nickName', formData.value.nickName);
    validateField('fullName', formData.value.fullName);
    
    if (!isFormValid.value) {
      message.value = 'Please fix validation errors';
      messageType.value = 'error';
      return;
    }

    const userId = getCurrentUserId();
    const token = sessionStorage.getItem('accessToken');

    // Only nickname and fullname can be updated
    const updateData = {
      nickName: formData.value.nickName.trim(),
      fullName: formData.value.fullName.trim()
    };

    const response = await fetch(`${import.meta.env.BASE_URL}itb-mshop/v2/users/${userId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updateData)
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to update profile');
    }

    message.value = 'Profile data is updated successfully.';
    messageType.value = 'success';

    // Redirect to profile page after 1 second
    setTimeout(() => {
      router.push({ name: 'profile-page' });
    }, 1000);

  } catch (err) {
    message.value = err.message || 'Failed to update profile';
    messageType.value = 'error';
    console.error('Error updating profile:', err);
  } finally {
    saving.value = false;
  }
};

const cancel = () => {
  router.push({ name: 'profile-page' });
};

onMounted(() => {
  fetchProfile();
});
</script>

<style scoped>
.card {
  border: 1px solid #e5e7eb;
}

.card-title {
  color: #1f2937;
}
</style>

