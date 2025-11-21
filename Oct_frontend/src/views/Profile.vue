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

    <!-- Profile Content -->
    <div v-else class="space-y-6">
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
                v-model="userProfile.fullName"
                type="text"
                class="input input-bordered"
                disabled
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Phone Number</span>
              </label>
              <input
                v-model="userProfile.phoneNumber"
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
                <span class="label-text font-semibold">Bank Account</span>
              </label>
              <input
                v-model="userProfile.bankAccount"
                type="text"
                class="input input-bordered bg-white"
                disabled
              />
            </div>

            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">ID Card Number</span>
              </label>
              <input
                v-model="userProfile.idCardNumber"
                type="text"
                class="input input-bordered bg-white"
                disabled
              />
            </div>

            <!-- National ID Photos -->
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

      <!-- Buyer Information -->
      <div v-else class="card bg-gradient-to-br from-green-50 to-teal-50 shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-4 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 mr-2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 10.5V6a3.75 3.75 0 10-7.5 0v4.5m11.356-1.993l1.263 12A1.125 1.125 0 0120.25 21.75H4.25a1.125 1.125 0 01-1.12-1.243l1.264-12A1.125 1.125 0 015.513 7.5h12.974c.576 0 1.059.435 1.119 1.007zM8.625 10.5a.375.375 0 11-.75 0 .375.375 0 01.75 0zm7.5 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
            </svg>
            Buyer Account
          </h2>
          <p class="text-gray-600">You are registered as a buyer. Browse and purchase items from sellers.</p>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex justify-end gap-4">
        <button @click="goHome" class="btn btn-outline">
          Back to Home
        </button>
        <button @click="editProfile" class="btn btn-primary">
          Edit Profile
        </button>
        <button @click="logout" class="btn btn-error">
          Logout
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
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

const goHome = () => {
  router.push({ name: 'home-page' });
};

const editProfile = () => {
  router.push({ name: 'edit-profile-page' });
};

const logout = async () => {
  // Redirect to signout route which will handle cleanup and redirect
  router.push({ name: 'signout-page' });
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

