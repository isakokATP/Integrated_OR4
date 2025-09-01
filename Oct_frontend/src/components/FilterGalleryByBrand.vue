<template>
  <div class="flex flex-col gap-4">
    <!-- Brand Filter -->
    <div class="flex items-start gap-0.5">
      <div
        class="itbms-brand-filter w-102.5 border border-gray-300 rounded-md px-4 py-2 flex flex-wrap gap-2 items-start min-h-[40px]"
        @click="showBrandDropdown = !showBrandDropdown"
      >
        <template v-if="modelValue.brands.length">
          <div
            v-for="brand in modelValue.brands"
            :key="brand"
            class="itbms-filter-item flex items-center bg-blue-100 text-blue-800 px-3 py-1 rounded-md text-sm"
          >
            {{ brand }}
            <button
              @click="removeBrand(brand)"
              class="itbms-filter-item-clear ml-2 text-blue-600 hover:text-blue-900 font-bold"
              aria-label="Remove brand"
            >
              &times;
            </button>
          </div>
        </template>
        <div v-else class="text-gray-500 text-sm">Filter by brand(s)</div>
      </div>

      <div class="flex flex-col ml-1">
        <div class="flex gap-1.5">
          <button
            @click="showBrandDropdown = !showBrandDropdown"
            class="itbms-brand-filter-button bg-gray-200 px-3 py-2 hover:bg-blue-200 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
          >
            <span class="inline-block mr-1">Choose</span>
          </button>
          <button
            @click="clearBrands"
            class="itbms-brand-filter-clear bg-gray-200 px-3 py-2 hover:bg-gray-300 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
          >
            Clear
          </button>
        </div>

        <div v-if="showBrandDropdown" class="relative mt-2">
          <div
            class="absolute right-0 z-10 bg-white border border-gray-300 rounded shadow-md max-h-60 overflow-auto w-48"
          >
            <div
              v-for="brand in availableBrands"
              :key="brand"
              class="itbms-filter-item px-4 py-2 hover:bg-blue-100 cursor-pointer text-sm"
              @click="addBrand(brand)"
            >
              {{ brand }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Price Range Filter -->
    <div class="flex items-start gap-0.5">
      <div
        class="itbms-price-filter w-102.5 border border-gray-300 rounded-md px-4 py-2 flex flex-wrap gap-2 items-start min-h-[40px]"
        @click="showPriceDropdown = !showPriceDropdown"
      >
        <template v-if="selectedPriceRange">
          <div class="itbms-filter-item flex items-center bg-blue-100 text-blue-800 px-3 py-1 rounded-md text-sm">
            {{ selectedPriceRange.label }}
            <button
              @click="clearPriceFilter"
              class="itbms-filter-item-clear ml-2 text-blue-600 hover:text-blue-900 font-bold"
              aria-label="Clear price filter"
            >
              &times;
            </button>
          </div>
        </template>
        <div v-else class="text-gray-500 text-sm">Filter by price range</div>
      </div>

      <div class="flex flex-col ml-1">
        <div class="flex gap-1.5">
          <button
            @click="showPriceDropdown = !showPriceDropdown"
            class="itbms-price-filter-button bg-gray-200 px-3 py-2 hover:bg-blue-200 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
          >
            <span class="inline-block mr-1">Choose</span>
          </button>
          <button
            @click="clearPriceFilter"
            class="itbms-price-filter-clear bg-gray-200 px-3 py-2 hover:bg-gray-300 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
          >
            Clear
          </button>
        </div>

                 <div v-if="showPriceDropdown" class="relative mt-2">
           <div
             class="absolute right-0 z-10 bg-white border border-gray-300 rounded shadow-md max-h-60 overflow-auto w-48"
           >
             <div
               v-for="range in priceRanges"
               :key="range.label"
               class="itbms-filter-item px-4 py-2 hover:bg-blue-100 cursor-pointer text-sm"
               @click="selectPriceRange(range)"
             >
               {{ range.label }}
             </div>
           </div>
         </div>

         <!-- Custom Price Range Input -->
         <div v-if="showCustomPriceInput" class="relative mt-2 custom-price-input">
           <div
             class="absolute right-0 z-10 bg-white border border-gray-300 rounded shadow-md p-4 w-64"
           >
             <div class="mb-3">
               <label class="block text-sm font-medium mb-1">Min Price (Baht)</label>
               <input
                 v-model="customMinPrice"
                 type="number"
                 min="0"
                 placeholder="0"
                 class="w-full border border-gray-300 rounded px-2 py-1 text-sm"
               />
             </div>
             <div class="mb-3">
               <label class="block text-sm font-medium mb-1">Max Price (Baht)</label>
               <input
                 v-model="customMaxPrice"
                 type="number"
                 min="0"
                 placeholder="50000"
                 class="w-full border border-gray-300 rounded px-2 py-1 text-sm"
               />
             </div>
             <div class="flex gap-2">
               <button
                 @click="applyCustomPriceRange"
                 class="bg-blue-500 text-white px-3 py-1 rounded text-sm hover:bg-blue-600"
               >
                 Apply
               </button>
               <button
                 @click="cancelCustomPriceRange"
                 class="bg-gray-300 text-gray-700 px-3 py-1 rounded text-sm hover:bg-gray-400"
               >
                 Cancel
               </button>
             </div>
           </div>
         </div>
      </div>
    </div>

    <!-- Storage Size Filter -->
    <div class="flex items-start gap-0.5">
      <div
        class="itbms-storage-filter w-102.5 border border-gray-300 rounded-md px-4 py-2 flex flex-wrap gap-2 items-start min-h-[40px]"
        @click="showStorageDropdown = !showStorageDropdown"
      >
        <template v-if="modelValue.storageSizes.length">
          <div
            v-for="storage in modelValue.storageSizes"
            :key="storage"
            class="itbms-filter-item flex items-center bg-green-100 text-green-800 px-3 py-1 rounded-md text-sm"
          >
            {{ formatStorageSize(storage) }}
            <button
              @click="removeStorage(storage)"
              class="itbms-filter-item-clear ml-2 text-green-600 hover:text-green-900 font-bold"
              aria-label="Remove storage size"
            >
              &times;
            </button>
          </div>
        </template>
        <div v-else class="text-gray-500 text-sm">Filter by storage size</div>
      </div>

      <div class="flex flex-col ml-1">
        <div class="flex gap-1.5">
          <button
            @click="showStorageDropdown = !showStorageDropdown"
            class="itbms-storage-filter-button bg-gray-200 px-3 py-2 hover:bg-green-200 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
          >
            <span class="inline-block mr-1">Choose</span>
          </button>
          <button
            @click="clearStorageSizes"
            class="itbms-storage-filter-clear bg-gray-200 px-3 py-2 hover:bg-gray-300 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
          >
            Clear
          </button>
        </div>

        <div v-if="showStorageDropdown" class="relative mt-2">
          <div
            class="absolute right-0 z-10 bg-white border border-gray-300 rounded shadow-md max-h-60 overflow-auto w-48"
          >
            <div
              v-for="storage in availableStorageSizes"
              :key="storage"
              class="itbms-filter-item px-4 py-2 hover:bg-green-100 cursor-pointer text-sm"
              @click="addStorage(storage)"
            >
              {{ formatStorageSize(storage) }}
            </div>
            <div
              v-if="!modelValue.storageSizes.includes('not_specified')"
              class="itbms-filter-item px-4 py-2 hover:bg-green-100 cursor-pointer text-sm border-t border-gray-200"
              @click="addStorage('not_specified')"
            >
              Not specified
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Clear All Filters Button -->
    <div class="flex justify-end">
      <button
        @click="clearAllFilters"
        class="bg-red-500 text-white px-4 py-2 hover:bg-red-600 text-sm rounded-md font-semibold"
      >
        Clear All Filters
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineEmits, defineProps } from "vue";
import { fetchBrands, fetchStorageSizes } from "../services/saleItemService";

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({
      brands: [],
      priceMin: null,
      priceMax: null,
      storageSizes: []
    })
  }
});

const emit = defineEmits(["update:modelValue"]);

const showBrandDropdown = ref(false);
const showPriceDropdown = ref(false);
const showStorageDropdown = ref(false);
const showCustomPriceInput = ref(false);
const allBrands = ref([]);
const allStorageSizes = ref([]);
const customMinPrice = ref('');
const customMaxPrice = ref('');
const searchKeyword = ref('');

const priceRanges = [
  { label: "0 - 5,000 Baht", min: 0, max: 5000 },
  { label: "5,001-10,000 Baht", min: 5001, max: 10000 },
  { label: "10,001-20,000 Baht", min: 10001, max: 20000 },
  { label: "20,001-30,000 Baht", min: 20001, max: 30000 },
  { label: "30,001-40,000 Baht", min: 30001, max: 40000 },
  { label: "40,001-50,000 Baht", min: 40001, max: 50000 },
  { label: "Custom Range", min: null, max: null, isCustom: true }
];

const selectedPriceRange = computed(() => {
  if (props.modelValue.priceMin === null && props.modelValue.priceMax === null) {
    return null;
  }
  
  // Check if it matches any predefined range
  const predefinedRange = priceRanges.find(range => 
    range.min === props.modelValue.priceMin && range.max === props.modelValue.priceMax
  );
  
  if (predefinedRange) {
    return predefinedRange;
  }
  
  // If no predefined range matches, it's a custom range
  const minDisplay = props.modelValue.priceMin !== null ? props.modelValue.priceMin : '0';
  const maxDisplay = props.modelValue.priceMax !== null ? props.modelValue.priceMax : '∞';
  
  // ถ้ามีแค่ min price ให้แสดงเป็น min - min
  if (props.modelValue.priceMin !== null && props.modelValue.priceMax === null) {
    return {
      label: `Custom: ${minDisplay} - ${minDisplay} Baht`,
      min: props.modelValue.priceMin,
      max: props.modelValue.priceMin,
      isCustom: true
    };
  }
  
  return {
    label: `Custom: ${minDisplay} - ${maxDisplay} Baht`,
    min: props.modelValue.priceMin,
    max: props.modelValue.priceMax,
    isCustom: true
  };
});

const availableBrands = computed(() =>
  allBrands.value
    .filter((b) => !props.modelValue.brands.includes(b))
    .sort((a, b) => a.localeCompare(b))
);

const availableStorageSizes = computed(() =>
  allStorageSizes.value
    .filter((s) => !props.modelValue.storageSizes.includes(s))
    .sort((a, b) => a - b)
);

function addBrand(brand) {
  const newSelectedBrands = [...props.modelValue.brands, brand];
  const newValue = { ...props.modelValue, brands: newSelectedBrands };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function removeBrand(brand) {
  const newSelectedBrands = props.modelValue.brands.filter((b) => b !== brand);
  const newValue = { ...props.modelValue, brands: newSelectedBrands };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function clearBrands() {
  const newValue = { ...props.modelValue, brands: [] };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function selectPriceRange(range) {
  if (range.isCustom) {
    showPriceDropdown.value = false;
    showCustomPriceInput.value = true;
    return;
  }
  
  const newValue = { 
    ...props.modelValue, 
    priceMin: range.min, 
    priceMax: range.max 
  };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
  showPriceDropdown.value = false;
}

function applyCustomPriceRange() {
  const min = customMinPrice.value ? parseInt(customMinPrice.value) : null;
  const max = customMaxPrice.value ? parseInt(customMaxPrice.value) : null;
  
  // Validation
  if (min !== null && max !== null && min > max) {
    alert('Min price cannot be greater than max price');
    return;
  }
  
  if (min !== null && min < 0) {
    alert('Min price cannot be negative');
    return;
  }
  
  if (max !== null && max < 0) {
    alert('Max price cannot be negative');
    return;
  }
  
  const newValue = { 
    ...props.modelValue, 
    priceMin: min, 
    priceMax: max 
  };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
  showCustomPriceInput.value = false;
  customMinPrice.value = '';
  customMaxPrice.value = '';
}

function cancelCustomPriceRange() {
  showCustomPriceInput.value = false;
  customMinPrice.value = '';
  customMaxPrice.value = '';
}

function clearPriceFilter() {
  const newValue = { ...props.modelValue, priceMin: null, priceMax: null };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
  showPriceDropdown.value = false;
}

function addStorage(storage) {
  const newSelectedStorageSizes = [...props.modelValue.storageSizes, storage];
  const newValue = { ...props.modelValue, storageSizes: newSelectedStorageSizes };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function removeStorage(storage) {
  const newSelectedStorageSizes = props.modelValue.storageSizes.filter((s) => s !== storage);
  const newValue = { ...props.modelValue, storageSizes: newSelectedStorageSizes };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function clearStorageSizes() {
  const newValue = { ...props.modelValue, storageSizes: [] };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function formatStorageSize(storage) {
  if (storage === 'not_specified') {
    return 'Not specified';
  }
  if (storage >= 1024) {
    return `${storage / 1024} TB`;
  }
  return `${storage} GB`;
}

function clearAllFilters() {
  const newValue = {
    brands: [],
    priceMin: null,
    priceMax: null,
    storageSizes: [],
    searchKeyWord: null
  };
  searchKeyword.value = '';
  emit("update:modelValue", newValue);
  sessionStorage.removeItem("filterSettings");
}

function updateSearchKeyword() {
  const newValue = { ...props.modelValue, searchKeyWord: searchKeyword.value };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function clearSearchKeyword() {
  searchKeyword.value = '';
  const newValue = { ...props.modelValue, searchKeyWord: null };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

function saveToSessionStorage(value) {
  sessionStorage.setItem("filterSettings", JSON.stringify(value));
}

onMounted(async () => {
  // Load brands from the API
  const result = await fetchBrands();
  allBrands.value = result.map((b) => b.name);

  // Load storage sizes from the API
  try {
    const storageResult = await fetchStorageSizes();
    allStorageSizes.value = storageResult;
  } catch (error) {
    console.error("Failed to load storage sizes:", error);
    // Fallback to hardcoded values if API fails
    allStorageSizes.value = [32, 64, 128, 256, 512, 1024, 2048];
  }

  // Load filter settings from session storage
  const savedFilterSettings = sessionStorage.getItem("filterSettings");
  if (savedFilterSettings) {
    try {
      const parsedSettings = JSON.parse(savedFilterSettings);
      if (parsedSettings && typeof parsedSettings === 'object') {
        
                         // Add click outside handler for dropdowns
        document.addEventListener('click', (event) => {
          const target = event.target;
          if (!target.closest('.itbms-brand-filter') && !target.closest('.itbms-price-filter') && !target.closest('.itbms-storage-filter')) {
            showBrandDropdown.value = false;
            showPriceDropdown.value = false;
            showStorageDropdown.value = false;
            // ไม่ปิด custom price input เมื่อคลิกที่ input fields
            if (!target.closest('.custom-price-input')) {
              showCustomPriceInput.value = false;
            }
          }
        });
        // Ensure all required properties exist
        const defaultSettings = {
          brands: [],
          priceMin: null,
          priceMax: null,
          storageSizes: []
        };
        const mergedSettings = { ...defaultSettings, ...parsedSettings };
        emit("update:modelValue", mergedSettings);
      } else {
        console.error("Invalid data in session storage for filterSettings.");
        sessionStorage.removeItem("filterSettings");
      }
    } catch (e) {
      console.error("Failed to parse filterSettings from session storage:", e);
      sessionStorage.removeItem("filterSettings");
    }
  }
});
</script>
