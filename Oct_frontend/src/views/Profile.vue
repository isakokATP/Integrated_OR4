<template>
  <Header />
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

    <!-- Profile Content -->
    <div v-else class="space-y-6">
      <!-- Navigation Bar -->
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link
          to="/sale-items"
          class="text-blue-600 hover:underline font-medium"
        >Home</router-link>
        <span class="mx-1">›</span>
        <span class="font-semibold">My Profile</span>
      </nav>

      <!-- Header -->
      <div class="flex justify-between items-start">
        <div>
          <h1 class="text-3xl font-bold text-gray-800">My Profile</h1>
          <p class="text-gray-600 mt-1">Manage your account information</p>
        </div>
        <div v-if="userProfile" class="badge badge-lg"
          :class="userProfile.userType === 'SELLER' ? 'badge-success' : 'badge-info'">
          {{ userProfile.userType }}
        </div>
      </div>

      <!-- User Info Card -->
      <div class="card bg-white shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-4">Personal Information</h2>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Nickname</span>
              </label>
              <input
                :value="userProfile.nickName || userProfile.nickname || ''"
                type="text"
                class="input input-bordered"
                disabled
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Email</span>
              </label>
              <input
                v-model="userProfile.email"
                type="email"
                class="input input-bordered"
                disabled
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Full Name</span>
              </label>
              <input
                :value="userProfile.fullName || userProfile.fullname || ''"
                type="text"
                class="input input-bordered"
                disabled
              />
            </div>

            <!-- Mobile - Only show for SELLER -->
            <div v-if="userProfile.userType === 'SELLER'" class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Mobile</span>
              </label>
              <input
                :value="maskMobile(userProfile.phoneNumber)"
                type="text"
                class="input input-bordered"
                disabled
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Seller-specific Information -->
      <div v-if="userProfile.userType === 'SELLER'" class="card bg-gradient-to-br from-blue-50 to-purple-50 shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-4 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 mr-2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 013 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 00-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 01-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 003 15h-.75M15 10.5a3 3 0 11-6 0 3 3 0 016 0zm3 0h.008v.008H18V10.5zm-12 0h.008v.008H6V10.5z" />
            </svg>
            Seller Account Details
          </h2>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Bank Name</span>
              </label>
              <input
                v-model="userProfile.bankName"
                type="text"
                class="input input-bordered bg-white"
                disabled
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Bank Account No</span>
              </label>
              <input
                :value="maskBankAccount(userProfile.bankAccount)"
                type="text"
                class="input input-bordered bg-white"
                disabled
              />
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex justify-end gap-4">
        <button @click="editProfile" class="btn btn-primary">
          Edit Profile
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Header from '../components/Header.vue';
import { handleApiError } from '../api/client';

const router = useRouter();
const loading = ref(false);
const error = ref('');
const userProfile = ref(null);

// Get user ID from sessionStorage (set during login)
const getCurrentUserId = () => {
  // Check if user is logged in
  const token = sessionStorage.getItem('accessToken');
  if (!token) {
    router.push({ name: 'login-page' });
    return null;
  }

  // Decode JWT to get user ID (simplified - use jwt-decode library in production)
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id;
  } catch (e) {
    console.error('Error decoding token:', e);
    router.push({ name: 'login-page' });
    return null;
  }
};

// Fetch user profile
const fetchUserProfile = async () => {
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

    const data = await response.json();
    console.log('Profile data received:', data);
    userProfile.value = data;
  } catch (err) {
    error.value = handleApiError(err);
    console.error('Error fetching profile:', err);
  } finally {
    loading.value = false;
  }
};

// Mask mobile - show 3 digits from end (2nd, 3rd, 4th from end) and last digit as x
// Example: 0123456789 -> xxxxxx789x
const maskMobile = (mobile) => {
  if (!mobile) return '';
  if (mobile.length <= 4) return mobile;
  // Show digits at positions -4, -3, -2 (2nd, 3rd, 4th from end)
  const visiblePart = mobile.slice(-4, -1); // positions -4 to -2 (3 digits)
  const maskedLength = mobile.length - 4; // mask everything except last 4
  return 'x'.repeat(maskedLength) + visiblePart + 'x'; // last digit is also masked
};

// Mask bank account - show 3 digits from end (2nd, 3rd, 4th from end) and last digit as x
// Example: 0123456789 -> xxxxxx789x
const maskBankAccount = (account) => {
  if (!account) return '';
  if (account.length <= 4) return account;
  // Show digits at positions -4, -3, -2 (2nd, 3rd, 4th from end)
  const visiblePart = account.slice(-4, -1); // positions -4 to -2 (3 digits)
  const maskedLength = account.length - 4; // mask everything except last 4
  return 'x'.repeat(maskedLength) + visiblePart + 'x'; // last digit is also masked
};

const editProfile = () => {
  router.push({ name: 'edit-profile-page' });
};

onMounted(() => {
  fetchUserProfile();
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

