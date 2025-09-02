<template>
  <div class="flex items-center gap-6">
    <!-- Filter Container - ทั้ง 3 filter + Clear All ในกรอบเดียวกัน -->
    <div class="flex-1 bg-white rounded-2xl shadow-lg border border-gray-100 p-4">
      <div class="flex items-center gap-4">
        <!-- Brand Filter -->
        <div class="flex-1 relative">
          <div
            class="itbms-brand-filter border-2 border-gray-200 rounded-xl px-4 py-3 flex flex-wrap gap-2 items-start min-h-[48px] bg-white hover:border-blue-300 transition-all duration-300 cursor-pointer shadow-sm hover:shadow-md"
            @click="showBrandDropdown = !showBrandDropdown"
          >
            <template v-if="modelValue.brands.length">
              <div
                v-for="brand in modelValue.brands"
                :key="brand"
                class="itbms-filter-item flex items-center bg-gradient-to-r from-blue-500 to-blue-600 text-white px-3 py-2 rounded-lg text-sm font-medium shadow-sm hover:shadow-md transition-all duration-200"
              >
                {{ brand }}
                <button
                  @click.stop="removeBrand(brand)"
                  class="itbms-filter-item-clear ml-2 text-white hover:text-red-200 font-bold hover:bg-white hover:bg-opacity-20 rounded-full w-5 h-5 flex items-center justify-center transition-all duration-200"
                  aria-label="Remove brand"
                >
                  &times;
                </button>
              </div>
            </template>
            <div v-else class="text-gray-500 text-sm font-medium">Brand</div>
          </div>
          
          <!-- Brand Dropdown -->
          <div v-if="showBrandDropdown" class="absolute top-full left-0 right-0 mt-2 z-20">
            <div class="bg-white border-2 border-gray-200 rounded-xl shadow-xl max-h-60 overflow-auto backdrop-blur-sm">
              <div
                v-for="brand in availableBrands"
                :key="brand"
                class="itbms-filter-item px-4 py-3 hover:bg-gradient-to-r hover:from-blue-50 hover:to-blue-100 cursor-pointer text-sm font-medium border-b border-gray-100 last:border-b-0 transition-all duration-200 hover:pl-6"
                @click="addBrand(brand)"
              >
                {{ brand }}
              </div>
            </div>
          </div>
        </div>

        <!-- Price Filter -->
        <div class="flex-1 relative">
          <div
            class="itbms-price-filter border-2 border-gray-200 rounded-xl px-4 py-3 flex flex-wrap gap-2 items-start min-h-[48px] bg-white hover:border-blue-300 transition-all duration-300 cursor-pointer shadow-sm hover:shadow-md"
            @click="showPriceDropdown = !showPriceDropdown"
          >
            <template v-if="selectedPriceRange">
              <div class="itbms-filter-item flex items-center bg-gradient-to-r from-green-500 to-green-600 text-white px-3 py-2 rounded-lg text-sm font-medium shadow-sm hover:shadow-md transition-all duration-200">
                {{ selectedPriceRange.label }}
                <button
                  @click.stop="clearPriceFilter"
                  class="itbms-filter-item-clear ml-2 text-white hover:text-red-200 font-bold hover:bg-white hover:bg-opacity-20 rounded-full w-5 h-5 flex items-center justify-center transition-all duration-200"
                  aria-label="Clear price filter"
                >
                  &times;
                </button>
              </div>
            </template>
            <div v-else class="text-gray-500 text-sm font-medium">Price</div>
          </div>
          
          <!-- Price Dropdown -->
          <div v-if="showPriceDropdown" class="absolute top-full left-0 right-0 mt-2 z-20">
            <div class="bg-white border-2 border-gray-200 rounded-xl shadow-xl max-h-60 overflow-auto backdrop-blur-sm">
              <div
                v-for="range in priceRanges"
                :key="range.label"
                class="itbms-filter-item px-4 py-3 hover:bg-gradient-to-r hover:from-green-50 hover:to-green-100 cursor-pointer text-sm font-medium border-b border-gray-100 last:border-b-0 transition-all duration-200 hover:pl-6"
                @click="selectPriceRange(range)"
              >
                {{ range.label }}
              </div>
            </div>
          </div>

          <!-- Custom Price Range Input -->
          <div v-if="showCustomPriceInput" class="absolute top-full left-0 right-0 mt-2 z-20">
            <div class="bg-white border-2 border-gray-200 rounded-xl shadow-xl p-4 w-64 backdrop-blur-sm">
              <div class="mb-3">
                <label class="block text-sm font-medium mb-1">Min Price (Baht)</label>
                <input
                  v-model="customMinPrice"
                  type="number"
                  min="0"
                  placeholder="0"
                  class="w-full border-2 border-gray-200 rounded-lg px-3 py-2 text-sm focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100 transition-all duration-200"
                />
              </div>
              <div class="mb-3">
                <label class="block text-sm font-medium mb-1">Max Price (Baht)</label>
                <input
                  v-model="customMaxPrice"
                  type="number"
                  min="0"
                  placeholder="50000"
                  class="w-full border-2 border-gray-200 rounded-lg px-3 py-2 text-sm focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100 transition-all duration-200"
                />
              </div>
              <div class="flex gap-2">
                <button
                  @click="applyCustomPriceRange"
                  class="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:from-blue-600 hover:to-blue-700 transition-all duration-200 font-medium"
                >
                  Apply
                </button>
                <button
                  @click="cancelCustomPriceRange"
                  class="bg-gradient-to-r from-gray-500 to-gray-600 text-white px-4 py-2 rounded-lg text-sm hover:from-gray-600 hover:to-gray-700 transition-all duration-200 font-medium"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Storage Filter -->
        <div class="flex-1 relative">
          <div
            class="itbms-storage-filter border-2 border-gray-200 rounded-xl px-4 py-3 flex flex-wrap gap-2 items-start min-h-[48px] bg-white hover:border-blue-300 transition-all duration-300 cursor-pointer shadow-sm hover:shadow-md"
            @click="showStorageDropdown = !showStorageDropdown"
          >
            <template v-if="modelValue.storageSizes.length">
              <div
                v-for="storage in modelValue.storageSizes"
                :key="storage"
                class="itbms-filter-item flex items-center bg-gradient-to-r from-purple-500 to-purple-600 text-white px-3 py-2 rounded-lg text-sm font-medium shadow-sm hover:shadow-md transition-all duration-200"
              >
                {{ formatStorageSize(storage) }}
                <button
                  @click.stop="removeStorage(storage)"
                  class="itbms-filter-item-clear ml-2 text-white hover:text-red-200 font-bold hover:bg-white hover:bg-opacity-20 rounded-full w-5 h-5 flex items-center justify-center transition-all duration-200"
                  aria-label="Remove storage size"
                >
                  &times;
                </button>
              </div>
            </template>
            <div v-else class="text-gray-500 text-sm font-medium">Storage</div>
          </div>
          
          <!-- Storage Dropdown -->
          <div v-if="showStorageDropdown" class="absolute top-full left-0 right-0 mt-2 z-20">
            <div class="bg-white border-2 border-gray-200 rounded-xl shadow-xl max-h-60 overflow-auto backdrop-blur-sm">
              <div
                v-for="storage in availableStorageSizes"
                :key="storage"
                class="itbms-filter-item px-4 py-3 hover:bg-gradient-to-r hover:from-purple-50 hover:to-purple-100 cursor-pointer text-sm font-medium border-b border-gray-100 last:border-b-0 transition-all duration-200 hover:pl-6"
                @click="addStorage(storage)"
              >
                {{ formatStorageSize(storage) }}
              </div>
              <div
                v-if="!modelValue.storageSizes.includes('not_specified')"
                class="itbms-filter-item px-4 py-3 hover:bg-gradient-to-r hover:from-purple-50 hover:to-purple-100 cursor-pointer text-sm font-medium border-t border-gray-200 transition-all duration-200 hover:pl-6"
                @click="addStorage('not_specified')"
              >
                Not specified
              </div>
            </div>
          </div>
        </div>

        <!-- Clear All Filters Button (ไม่กระทบ Search) -->
        <button
          @click="clearAllFilters"
          class="bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white px-4 py-3 text-sm rounded-xl font-semibold shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 flex items-center gap-2 whitespace-nowrap"
          title="Clear all filters (keeps search)"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-4 h-4">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
          Clear All Filters
        </button>
      </div>
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
      storageSizes: [],
      searchKeyWord: null
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
  let label;
  
  if (props.modelValue.priceMin === props.modelValue.priceMax && props.modelValue.priceMin !== null) {
    // Exact price: 2000-2000 -> "2000 Baht"
    label = `${props.modelValue.priceMin} Baht`;
  } else if (props.modelValue.priceMin !== null && props.modelValue.priceMax !== null) {
    // Range: min-max
    label = `${props.modelValue.priceMin} - ${props.modelValue.priceMax} Baht`;
  } else if (props.modelValue.priceMin !== null) {
    // Only min: min-∞
    label = `${props.modelValue.priceMin}+ Baht`;
  } else if (props.modelValue.priceMax !== null) {
    // Only max: 0-max
    label = `0 - ${props.modelValue.priceMax} Baht`;
  } else {
    label = 'Custom Price Range';
  }
  
  return {
    label: `Custom: ${label}`,
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
  
  // If only min is provided, set max to min (exact price)
  // If only max is provided, set min to 0
  let finalMin = min;
  let finalMax = max;
  
  if (min !== null && max === null) {
    finalMax = min; // Exact price: 2000 -> 2000-2000
  } else if (min === null && max !== null) {
    finalMin = 0; // Up to max: max -> 0-max
  }
  
  const newValue = { 
    ...props.modelValue, 
    priceMin: finalMin, 
    priceMax: finalMax 
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
  // Clear all filters but keep search keyword
  const newValue = {
    brands: [],
    priceMin: null,
    priceMax: null,
    storageSizes: [],
    searchKeyWord: props.modelValue.searchKeyWord // Keep search keyword
  };
  emit("update:modelValue", newValue);
  sessionStorage.removeItem("filterSettings");
}

// Custom Price Range Functions
function selectPriceRange(range) {
  if (range.isCustom) {
    showCustomPriceInput.value = true;
    showPriceDropdown.value = false;
    customMinPrice.value = '';
    customMaxPrice.value = '';
  } else {
    const newValue = { ...props.modelValue, priceMin: range.min, priceMax: range.max };
    emit("update:modelValue", newValue);
    saveToSessionStorage(newValue);
    showPriceDropdown.value = false;
  }
}

function applyCustomPriceRange() {
  const min = customMinPrice.value ? parseInt(customMinPrice.value) : null;
  const max = customMaxPrice.value ? parseInt(customMaxPrice.value) : null;
  
  if (min !== null || max !== null) {
    const newValue = { ...props.modelValue, priceMin: min, priceMax: max };
    emit("update:modelValue", newValue);
    saveToSessionStorage(newValue);
  }
  
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
}

function saveToSessionStorage(value) {
  sessionStorage.setItem("filterSettings", JSON.stringify(value));
}

// Brand Functions
function addBrand(brand) {
  const newSelectedBrands = [...props.modelValue.brands, brand];
  const newValue = { ...props.modelValue, brands: newSelectedBrands };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
  showBrandDropdown.value = false;
}

function removeBrand(brand) {
  const newSelectedBrands = props.modelValue.brands.filter((b) => b !== brand);
  const newValue = { ...props.modelValue, brands: newSelectedBrands };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
}

// Storage Functions
function addStorage(storage) {
  const newSelectedStorageSizes = [...props.modelValue.storageSizes, storage];
  const newValue = { ...props.modelValue, storageSizes: newSelectedStorageSizes };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
  showStorageDropdown.value = false;
}

function removeStorage(storage) {
  const newSelectedStorageSizes = props.modelValue.storageSizes.filter((s) => s !== storage);
  const newValue = { ...props.modelValue, storageSizes: newSelectedStorageSizes };
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
             showCustomPriceInput.value = false;
           }
         });
        // Ensure all required properties exist
        const defaultSettings = {
          brands: [],
          priceMin: null,
          priceMax: null,
          storageSizes: [],
          searchKeyWord: null
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
