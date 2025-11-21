<template>
  <Header />
  <div class="max-w-4xl mx-auto p-6">
    <div class="flex items-center mb-6">
      <nav class="text-sm mb-4 flex items-center space-x-2">
        <router-link
          to="/sale-items"
          class="text-blue-600 hover:underline font-medium"
          >Home</router-link
        >
        <span class="mx-1">›</span>
        <span class="font-semibold">New Sale Item</span>
      </nav>
    </div>
         <form
       @submit.prevent="handleSave"
       class="grid grid-cols-1 md:grid-cols-2 gap-8"
     >
       <!-- Left: Picture -->
       <div class="mt-2">
         <div
           class="w-70 h-70 bg-gray-100 flex items-center justify-center text-2xl text-gray-400 mb-2 border border-gray-300 rounded"
         >
           <div v-if="selectedFiles.length === 0" class="text-center">
             <div class="text-2xl text-gray-400 mb-2">No Picture</div>
             <button
               type="button"
               @click="openFileInput"
               class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-colors"
             >
               Upload Pictures
             </button>
           </div>
           <div v-else class="w-full h-full relative">
             <img
               :src="getImagePreview(selectedFiles[0])"
               alt="Main preview"
               class="w-full h-full object-cover rounded"
             />
           </div>
         </div>
         
         <!-- Thumbnail previews -->
         <div class="flex gap-2 mb-4">
           <!-- New selected files -->
           <div
             v-for="(file, index) in selectedFiles.slice(0, 4)"
             :key="`new-${index}`"
             class="w-16 h-16 bg-gray-100 flex items-center justify-center text-gray-400 text-xs border border-gray-300 rounded relative overflow-hidden"
           >
             <img
               :src="getImagePreview(file)"
               alt="Thumbnail"
               class="w-full h-full object-cover"
             />
             <button
               @click="removeFile(index)"
               class="absolute top-0 right-0 bg-red-500 text-white text-xs px-1 rounded-bl"
             >
               ×
             </button>
           </div>
           
           <!-- Empty slots -->
           <div
             v-for="n in Math.max(0, 4 - selectedFiles.length)"
             :key="`empty-${n}`"
             class="w-16 h-16 bg-gray-100 flex items-center justify-center text-gray-400 text-xs border border-gray-300 rounded"
           >
             No Picture
           </div>
         </div>

         <!-- Upload button -->
         <div class="mb-4">
           <button
             type="button"
             @click="openFileInput"
             class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-colors"
           >
             Upload Pictures
           </button>
           <span class="text-sm text-gray-500 ml-2">
             (Max 4 pictures, 2MB each)
           </span>
         </div>

         <!-- Hidden file input -->
         <input
           ref="fileInput"
           type="file"
           multiple
           accept="image/*"
           @change="handleFileSelect"
           class="hidden"
         />

         <!-- File list with reorder controls -->
         <div v-if="selectedFiles.length > 0" class="mb-4">
           <h4 class="text-sm font-medium mb-2">Selected Files:</h4>
           <div class="space-y-2">
             <div
               v-for="(file, index) in selectedFiles"
               :key="`file-${index}-${file.name}`"
               class="flex items-center justify-between bg-gray-50 p-2 rounded"
             >
               <div class="flex items-center space-x-2">
                 <span class="text-sm font-medium">{{ index + 1 }}.</span>
                 <span class="text-sm">{{ file.name }}</span>
                 <span class="text-xs text-gray-500">({{ formatFileSize(file.size) }})</span>
               </div>
               <div class="flex items-center space-x-1">
                 <!-- Move up button -->
                 <button
                   v-if="index > 0"
                   type="button"
                   @click="moveFileUp(index)"
                   class="text-blue-500 hover:text-blue-700 p-1"
                   title="Move up"
                 >
                   ↑
                 </button>
                 <!-- Move down button -->
                 <button
                   v-if="index < selectedFiles.length - 1"
                   type="button"
                   @click="moveFileDown(index)"
                   class="text-blue-500 hover:text-blue-700 p-1"
                   title="Move down"
                 >
                   ↓
                 </button>
                 <!-- Remove button -->
                 <button
                   type="button"
                   @click="removeFile(index)"
                   class="text-red-500 hover:text-red-700 p-1"
                   title="Remove file"
                 >
                   ×
                 </button>
               </div>
             </div>
           </div>
           
           <!-- Warning for too many files -->
           <div v-if="selectedFiles.length > 4" class="mt-2 p-2 bg-yellow-100 border border-yellow-300 rounded">
             <p class="text-sm text-yellow-800">
               Maximum 4 pictures are allowed.
             </p>
           </div>
         </div>

         <!-- Error messages -->
         <div v-if="fileErrors.length > 0" class="mb-4">
           <div
             v-for="error in fileErrors"
             :key="error"
             class="text-red-500 text-sm mb-1"
           >
             {{ error }}
           </div>
         </div>
        
      </div>

      <!-- Right: Form -->
      <div>
        <div class="mb-3">
          <label class="block mb-1"
            >Brand<span class="text-red-500"> *</span></label
          >
          <select
            v-model="form.brandId"
            @blur="handleBlur('brandId')"
            @change="handleBlur('brandId')"
            class="itbms-brand w-full border rounded px-2 py-1"
          >
            <option value="">Select Brand</option>
            <option v-for="brand in brands" :key="brand.id" :value="brand.id">
              {{ brand.name }}
            </option>
          </select>
          <span
            v-if="fieldErrors.brandId"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.brandId }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Model<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.model"
            class="itbms-model w-full border rounded px-2 py-1"
            @blur="handleBlur('model')"
            @input="handleBlur('model')"
          />
          <span
            v-if="fieldErrors.model"
            class="itbms-message text-red-600 text-sm"
          >
            {{ fieldErrors.model }}
          </span>
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Price (Baht)<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.price"
            type="number"
            @blur="handleBlur('price')"
            @input="handleBlur('price')"
            step="1"
            class="itbms-price w-full border rounded px-2 py-1"
          />

          <span
            v-if="fieldErrors.price"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.price }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Description<span class="text-red-500"> *</span></label
          >
          <textarea
            v-model="form.description"
            v-trim
            @blur="handleBlur('description')"
            @input="handleBlur('description')"
            class="w-full border rounded px-2 py-1 itbms-description"
          ></textarea>
          <span
            v-if="fieldErrors.description"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.description }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Ram (GB)</label>
          <input
            v-model="form.ramGb"
            type="number"
            @blur="handleBlur('ramGb')"
            @input="handleBlur('ramGb')"
            step="1"
            class="w-full border rounded px-2 py-1 itbms-ramGb"
          />
          <span
            v-if="fieldErrors.ramGb"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.ramGb }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Screen Size (Inches)</label>
          <input
            v-model="form.screenSizeInch"
            type="number"
            step="0.01"
            @blur="handleBlur('screenSizeInch')"
            @input="handleBlur('screenSizeInch')"
            class="w-full border rounded px-2 py-1 itbms-screenSizeInch"
          />
          <span
            v-if="fieldErrors.screenSizeInch"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.screenSizeInch }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Storage (GB)</label>
          <input
            v-model="form.storageGb"
            type="number"
            @blur="handleBlur('storageGb')"
            @input="handleBlur('storageGb')"
            step="1"
            class="w-full border rounded px-2 py-1 itbms-storageGb"
          />
          <span
            v-if="fieldErrors.storageGb"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.storageGb }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1">Color</label>
          <input
            v-model="form.color"
            v-trim
            @blur="handleBlur('color')"
            @input="handleBlur('color')"
            class="w-full border rounded px-2 py-1 itbms-color"
          />
          <span
            v-if="fieldErrors.color"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.color }}</span
          >
        </div>

        <div class="mb-3">
          <label class="block mb-1"
            >Quantity<span class="text-red-500"> *</span></label
          >
          <input
            v-model="form.quantity"
            type="number"
            @blur="handleBlur('quantity')"
            @input="handleBlur('quantity')"
            class="w-full border rounded px-2 py-1 itbms-quantity"
          />
          <span
            v-if="fieldErrors.quantity"
            class="itbms-message text-red-500 text-sm"
            >{{ fieldErrors.quantity }}</span
          >
        </div>

        <div class="flex gap-4 mt-6">
          <button
            type="submit"
            class="itbms-save-button bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-500 transition-colors duration-300 disabled:bg-gray-400 disabled:cursor-not-allowed"
            :disabled="!isFormValid"
          >
            Save
          </button>

          <button
            type="button"
            class="border border-gray-400 px-4 py-2 rounded hover:bg-blue-200 transition-colors duration-300"
            @click="handleCancel"
          >
            Cancel
          </button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from "vue";
import { createSaleItem, fetchBrands } from "../services/saleItemService";
import Header from "../components/Header.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const brands = ref([]);

// File upload related refs
const fileInput = ref(null);
const selectedFiles = ref([]);
const fileErrors = ref([]);

const loadBrands = async () => {
  try {
    brands.value = await fetchBrands();
  } catch (error) {
    console.error("Error loading brands:", error);
  }
};
onMounted(loadBrands);

const form = ref({
  brandId: "",
  model: "",
  price: "",
  description: "",
  ramGb: "",
  screenSizeInch: "",
  storageGb: "",
  color: "",
  quantity: "",
});

const fieldErrors = reactive({});

const validateField = (field) => {
  let error = null;
  const value = form.value[field];

  switch (field) {
    case "brandId":
      if (!value) error = "Brand must be selected.";
      break;
    case "model":
      const trimmedModel = value ? value.trim() : "";
      if (!trimmedModel || trimmedModel.length > 60)
        error = "Model must be 1-60 characters long.";
      break;
    case "price":
      if (!/^[0-9]+$/.test(value) || parseInt(value) < 0)
        error = "Price must be non-negative integer.";
      break;
    case "description":
      const trimmedDescription = value ? value.trim() : "";
      if (!trimmedDescription || trimmedDescription.length > 16384)
        error = "Description must be 1-16,384 characters long.";
      break;
    case "ramGb":
      if (value !== "" && (!/^[0-9]+$/.test(value) || parseInt(value) <= 0))
        error = "RAM size must be positive integer or not specified.";
      break;
    case "screenSizeInch":
      if (value !== "") {
        const val = parseFloat(value);
        if (isNaN(val) || val <= 0 || !/^\d+(\.\d{1,2})?$/.test(value)) {
          error =
            "Screen size must be positive number with at most 2 decimal points or not specified.";
        }
      }
      break;
    case "storageGb":
      if (value !== "" && (!/^[0-9]+$/.test(value) || parseInt(value) <= 0))
        error = "Storage size must be positive integer or not specified.";
      break;
    case "color":
      if (value && value.length > 40)
        error = "Color must be 1-40 characters long or not specified.";
      break;
    case "quantity":
      if (!/^[0-9]+$/.test(value) || parseInt(value) < 0)
        error = "Quantity must be non-negative integer.";
      break;
  }

  fieldErrors[field] = error;
  return !error;
};

const validateAllFields = () => {
  let allValid = true;
  const fieldsToValidate = [
    "brandId",
    "model",
    "price",
    "description",
    "ramGb",
    "screenSizeInch",
    "storageGb",
    "color",
    "quantity",
  ];
  fieldsToValidate.forEach((field) => {
    if (!validateField(field)) {
      allValid = false;
    }
  });
  return allValid;
};

// File upload functions
function openFileInput() {
  fileInput.value.click();
}

function handleFileSelect(event) {
  const files = Array.from(event.target.files);
  fileErrors.value = [];
  
  // Calculate how many files we can actually add
  const currentTotal = selectedFiles.value.length;
  const maxAllowed = 4;
  const availableSlots = maxAllowed - currentTotal;
  
  // If no slots available, don't add any files
  if (availableSlots <= 0) {
    fileErrors.value.push("Maximum 4 pictures are allowed. Please remove some images first.");
    return;
  }
  
  // Limit files to available slots (take only the first N files)
  const filesToAdd = files.slice(0, availableSlots);
  const filesRejected = files.slice(availableSlots);
  
  // Validate each file to be added
  const validFiles = [];
  const invalidFiles = [];
  
  filesToAdd.forEach(file => {
    let isValid = true;
    let errorMessage = '';
    
    // Check file type
    if (!file.type.startsWith('image/')) {
      errorMessage = `${file.name} is not an image file.`;
      isValid = false;
    }
    
    // Check file size (2MB = 2 * 1024 * 1024 bytes)
    if (isValid && file.size > 2 * 1024 * 1024) {
      errorMessage = `${file.name} is larger than 2MB.`;
      isValid = false;
    }
    
    // Check for duplicate file names in new files
    if (isValid) {
      const existingFile = selectedFiles.value.find(f => f.name === file.name);
      if (existingFile) {
        errorMessage = `${file.name} already exists. Please choose a different file.`;
        isValid = false;
      }
    }
    
    // Categorize file
    if (isValid) {
      validFiles.push(file);
    } else {
      invalidFiles.push(file);
      fileErrors.value.push(errorMessage);
    }
  });
  
  // If there are validation errors, don't add files
  if (fileErrors.value.length > 0) {
    return;
  }
  
  // Add valid files (limited to available slots)
  selectedFiles.value.push(...validFiles);
  
  // Show message if some files were rejected due to limit
  if (filesRejected.length > 0) {
    const rejectedNames = filesRejected.map(f => f.name).join(', ');
    fileErrors.value.push(`Only ${availableSlots} files added. ${filesRejected.length} files rejected due to limit: ${rejectedNames}`);
  }
  
  // Clear the input
  event.target.value = '';
}

function removeFile(index) {
  selectedFiles.value.splice(index, 1);
  fileErrors.value = [];
}

function getImagePreview(file) {
  return URL.createObjectURL(file);
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function moveFileUp(index) {
  if (index > 0) {
    const files = [...selectedFiles.value];
    [files[index], files[index - 1]] = [files[index - 1], files[index]];
    selectedFiles.value = files;
  }
}

function moveFileDown(index) {
  if (index < selectedFiles.value.length - 1) {
    const files = [...selectedFiles.value];
    [files[index], files[index + 1]] = [files[index + 1], files[index]];
    selectedFiles.value = files;
  }
}

const isFormValid = computed(() => {
  const hasValidationErrors = Object.values(fieldErrors).some(
    (error) => error !== null && error !== ""
  );
  return (
    !hasValidationErrors &&
    form.value.brandId &&
    form.value.model.trim() &&
    form.value.price !== "" &&
    form.value.description.trim() &&
    form.value.quantity !== ""
  );
});

function handleCancel() {
  form.value = {
    brandId: "",
    model: "",
    price: "",
    description: "",
    ramGb: "",
    screenSizeInch: "",
    storageGb: "",
    color: "",
    quantity: "",
  };
  // Clear selected files
  selectedFiles.value = [];
  fileErrors.value = [];
  router.push({ name: "sale-items-list-page" });
}



async function handleSave() {
  if (!validateAllFields()) {
    return;
  }

  const brandObj = brands.value.find(
    (b) => b.id === parseInt(form.value.brandId)
  );
  const dataToSend = {
    model: form.value.model.trim(),
    brand: brandObj ? { id: brandObj.id, name: brandObj.name } : null,
    description: form.value.description.trim(),
    price: parseInt(form.value.price),
    ramGb: form.value.ramGb ? parseInt(form.value.ramGb) : null,
    screenSizeInch: form.value.screenSizeInch
      ? parseFloat(form.value.screenSizeInch)
      : null,
    quantity: parseInt(form.value.quantity),
    storageGb: form.value.storageGb ? parseInt(form.value.storageGb) : null,
    color: form.value.color.trim() || null,
  };

  try {
    // Send data to Backend (images are not supported by /v2/sellers/{id}/sale-items endpoint)
    // If images are needed, they should be uploaded separately or use /v2/sale-items endpoint
    const createdItem = await createSaleItem(dataToSend, selectedFiles.value);
    
    // Clear selected files after successful save
    selectedFiles.value = [];
    fileErrors.value = [];
    
    router.push({
      name: "sale-items-list-page",
      query: { message: "The sale item has been successfully added." },
    });
  } catch (err) {
    // Handle specific error cases
    let errorMessage = "เกิดข้อผิดพลาด: ";
    if (err.status === 400) {
      errorMessage += "Missing/Invalid request parameters";
    } else if (err.status === 401) {
      errorMessage += "Seller not found or invalid token";
    } else if (err.status === 403) {
      errorMessage += "User is not active, request seller id not matched with id in access token";
    } else if (err.status === 404) {
      errorMessage += "Brand not found";
    } else {
      errorMessage += err.message || "Unknown error";
    }
    alert(errorMessage);
  }
}

const handleBlur = (field) => {
  validateField(field);
};
</script>

<style scoped>
.text-sm {
  font-size: 0.875rem;
}
</style>
