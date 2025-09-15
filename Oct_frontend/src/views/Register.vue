<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { registerUser } from "../services/userService";

const router = useRouter();

const form = ref({
  nickName: "",
  email: "",
  fullName: "",
  password: "",
  phoneNumber: "",
  bankAccount: "",
  idCardNumber: "",
  userType: "BUYER",
  idCardImageFront: null,
  idCardImageBack: null,
});

const submitting = ref(false);
const message = ref("");
const errors = ref({});

function onFileFront(e) { form.value.idCardImageFront = e.target.files[0] || null; }
function onFileBack(e) { form.value.idCardImageBack = e.target.files[0] || null; }

function validate() {
  const e = {};
  const passRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
  const digits = /^\d*$/;

  if (!form.value.nickName) e.nickName = "Required";
  if (!form.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) e.email = "Invalid email";
  if (!form.value.fullName || form.value.fullName.length < 4 || form.value.fullName.length > 40) e.fullName = "4-40 characters";
  if (!form.value.password || !passRegex.test(form.value.password)) e.password = "Weak password";
  if (!form.value.phoneNumber || !digits.test(form.value.phoneNumber)) e.phoneNumber = "Digits only";
  if (!form.value.bankAccount || !digits.test(form.value.bankAccount)) e.bankAccount = "Digits only";
  if (!form.value.idCardNumber || !digits.test(form.value.idCardNumber)) e.idCardNumber = "Digits only";
  if (!form.value.userType || !["SELLER","BUYER","seller","buyer"].includes(form.value.userType)) e.userType = "Choose BUYER or SELLER";
  errors.value = e;
  return Object.keys(e).length === 0;
}

const canSubmit = computed(() => !submitting.value && validate());

async function onSubmit(e) {
  e.preventDefault();
  if (!validate()) return;
  try {
    submitting.value = true;
    const payload = { ...form.value, userType: form.value.userType.toUpperCase() };
    await registerUser(payload);
    message.value = "The user account has been successfully registered.";
    setTimeout(() => router.push({ name: "sale-items-page" }), 800);
  } catch (err) {
    message.value = err.message || "Register failed";
  } finally {
    submitting.value = false;
  }
}

function onCancel(){ router.push({ name: "sale-items-page" }); }
</script>

<template>
  <div class="max-w-3xl mx-auto p-6">
    <h1 class="text-2xl font-bold mb-4">Register</h1>

    <form @submit.prevent="onSubmit" class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium">Nickname</label>
        <input v-model="form.nickName" class="w-full border p-2 rounded" />
        <p v-if="errors.nickName" class="text-red-600 text-sm">{{ errors.nickName }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">Email</label>
        <input v-model="form.email" type="email" class="w-full border p-2 rounded" />
        <p v-if="errors.email" class="text-red-600 text-sm">{{ errors.email }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">Full name</label>
        <input v-model="form.fullName" class="w-full border p-2 rounded" />
        <p v-if="errors.fullName" class="text-red-600 text-sm">{{ errors.fullName }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">Password</label>
        <input v-model="form.password" type="password" class="w-full border p-2 rounded" />
        <p v-if="errors.password" class="text-red-600 text-sm">{{ errors.password }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">Phone number</label>
        <input v-model="form.phoneNumber" class="w-full border p-2 rounded" />
        <p v-if="errors.phoneNumber" class="text-red-600 text-sm">{{ errors.phoneNumber }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">Bank account</label>
        <input v-model="form.bankAccount" class="w-full border p-2 rounded" />
        <p v-if="errors.bankAccount" class="text-red-600 text-sm">{{ errors.bankAccount }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">National ID number</label>
        <input v-model="form.idCardNumber" class="w-full border p-2 rounded" />
        <p v-if="errors.idCardNumber" class="text-red-600 text-sm">{{ errors.idCardNumber }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">User type</label>
        <select v-model="form.userType" class="w-full border p-2 rounded">
          <option value="BUYER">Buyer</option>
          <option value="SELLER">Seller</option>
        </select>
        <p v-if="errors.userType" class="text-red-600 text-sm">{{ errors.userType }}</p>
      </div>
      <div>
        <label class="block text-sm font-medium">ID Card Front (optional)</label>
        <input type="file" accept="image/*" @change="onFileFront" class="w-full" />
      </div>
      <div>
        <label class="block text-sm font-medium">ID Card Back (optional)</label>
        <input type="file" accept="image/*" @change="onFileBack" class="w-full" />
      </div>

      <div class="md:col-span-2 flex gap-3 mt-2">
        <button :disabled="!canSubmit" class="px-4 py-2 rounded bg-blue-600 text-white disabled:opacity-50">Submit</button>
        <button type="button" @click="onCancel" class="px-4 py-2 rounded bg-gray-200">Cancel</button>
      </div>
    </form>

    <p v-if="message" class="mt-4 text-sm" :class="message.includes('successfully') ? 'text-green-700' : 'text-red-700'">{{ message }}</p>
  </div>
</template>


