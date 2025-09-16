<template>
  <div class="flex items-center gap-6">
    <!-- Filter Container - ทั้ง 3 filter + Clear All ในกรอบเดียวกัน -->
    <div class="flex-1 bg-white rounded-2xl shadow-lg border border-gray-100 p-4">
      <div class="flex items-center gap-4">
        <!-- Brand Filter -->
        <div class="flex-1 relative">
          <div
            class="itbms-brand-filter border-2 border-gray-200 rounded-xl px-4 py-3 flex flex-wrap gap-2 items-start min-h-[48px] bg-white hover:border-blue-300 transition-all duration-300 cursor-pointer shadow-sm hover:shadow-md"
            @click="toggleBrandDropdown"
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
            @click="togglePriceDropdown"
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
              <!-- Show predefined ranges only when custom is not selected -->
              <template v-if="!showCustomPriceInput">
                <div
                  v-for="range in priceRanges"
                  :key="range.label"
                  class="itbms-filter-item px-4 py-3 hover:bg-gradient-to-r hover:from-green-50 hover:to-green-100 cursor-pointer text-sm font-medium border-b border-gray-100 transition-all duration-200 hover:pl-6"
                  @click="selectPriceRange(range)"
                >
                  {{ range.label }}
                </div>
              </template>
              
              <!-- Custom Price Input Section - Show only when custom is selected -->
              <div v-if="showCustomPriceInput" class="p-4">
                <div class="text-sm font-medium text-gray-700 mb-3">Custom Price Range</div>
                <div class="grid grid-cols-2 gap-3 mb-3">
                  <div>
                    <label class="block text-xs text-gray-600 mb-1">Min (Baht)</label>
                    <input
                      v-model="customMinPrice"
                      type="number"
                      min="0"
                      placeholder="0"
                      class="w-full border border-gray-300 rounded-lg px-2 py-1 text-sm focus:border-green-400 focus:outline-none focus:ring-1 focus:ring-green-100"
                    />
                  </div>
                  <div>
                    <label class="block text-xs text-gray-600 mb-1">Max (Baht)</label>
                    <input
                      v-model="customMaxPrice"
                      type="number"
                      min="0"
                      placeholder="50000"
                      class="w-full border border-gray-300 rounded-lg px-2 py-1 text-sm focus:border-green-400 focus:outline-none focus:ring-1 focus:ring-green-100"
                    />
                  </div>
                </div>
                <div class="flex gap-2">
                  <button
                    @click="applyCustomPriceRange"
                    class="flex-1 bg-gradient-to-r from-green-500 to-green-600 text-white px-3 py-2 rounded-lg text-sm hover:from-green-600 hover:to-green-700 transition-all duration-200 font-medium"
                  >
                    Apply
                  </button>
                  <button
                    @click="cancelCustomPriceRange"
                    class="flex-1 bg-gradient-to-r from-gray-500 to-gray-600 text-white px-3 py-2 rounded-lg text-sm hover:from-gray-600 hover:to-gray-700 transition-all duration-200 font-medium"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          </div>


        </div>

        <!-- Storage Filter -->
        <div class="flex-1 relative">
          <div
            class="itbms-storage-filter border-2 border-gray-200 rounded-xl px-4 py-3 flex flex-wrap gap-2 items-start min-h-[48px] bg-white hover:border-blue-300 transition-all duration-300 cursor-pointer shadow-sm hover:shadow-md"
            @click="toggleStorageDropdown"
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

// Toggle functions for dropdowns
function toggleBrandDropdown() {
  showBrandDropdown.value = !showBrandDropdown.value;
  // Close other dropdowns when opening this one
  if (showBrandDropdown.value) {
    showPriceDropdown.value = false;
    showStorageDropdown.value = false;
  }
}

function togglePriceDropdown() {
  showPriceDropdown.value = !showPriceDropdown.value;
  // Close other dropdowns when opening this one
  if (showPriceDropdown.value) {
    showBrandDropdown.value = false;
    showStorageDropdown.value = false;
  }
}

function toggleStorageDropdown() {
  showStorageDropdown.value = !showStorageDropdown.value;
  // Close other dropdowns when opening this one
  if (showStorageDropdown.value) {
    showBrandDropdown.value = false;
    showPriceDropdown.value = false;
  }
}
const allBrands = ref([]);
const allStorageSizes = ref([]);
const customMinPrice = ref('');
const customMaxPrice = ref('');
const showCustomPriceInput = ref(false);

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

  // เช็ค predefined ranges
  const predefinedRange = priceRanges.find(range =>
    range.min === props.modelValue.priceMin && range.max === props.modelValue.priceMax
  );

  if (predefinedRange) {
    return predefinedRange;
  }

  // Custom range
  const min = props.modelValue.priceMin ?? 0;
  const max = props.modelValue.priceMax ?? min;
  let label = `${min} - ${max} Baht`;

  return {
    label: `Custom: ${label}`,
    min,
    max,
    isCustom: true
  };
});

// Check if custom price is selected
const isCustomPriceSelected = computed(() => {
  return selectedPriceRange.value && selectedPriceRange.value.isCustom;
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
    showCustomPriceInput.value = true;
    // Clear current price values when selecting custom
    const newValue = { ...props.modelValue, priceMin: null, priceMax: null };
    emit("update:modelValue", newValue);
    saveToSessionStorage(newValue);
  } else {
    showCustomPriceInput.value = false;
    const newValue = { ...props.modelValue, priceMin: range.min, priceMax: range.max };
    emit("update:modelValue", newValue);
    saveToSessionStorage(newValue);
  }
}

function applyCustomPriceRange() {
  const minPrice = customMinPrice.value ? parseInt(customMinPrice.value) : null;
  const maxPrice = customMaxPrice.value ? parseInt(customMaxPrice.value) : null;

  // ถ้าใส่แค่ค่าเดียว ให้เริ่มจาก 0
  const finalMin = minPrice !== null ? minPrice : 0;
  const finalMax = maxPrice !== null ? maxPrice : finalMin;

  const newValue = { ...props.modelValue, priceMin: finalMin, priceMax: finalMax };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);

  // ปิด custom input
  showCustomPriceInput.value = false;
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
  showCustomPriceInput.value = false;
  customMinPrice.value = '';
  customMaxPrice.value = '';
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

function saveToSessionStorage(value) {
  sessionStorage.setItem("filterSettings", JSON.stringify(value));
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
        
        // Sync custom price input with current values
        if (mergedSettings.priceMin !== null || mergedSettings.priceMax !== null) {
          customMinPrice.value = mergedSettings.priceMin ? mergedSettings.priceMin.toString() : '';
          customMaxPrice.value = mergedSettings.priceMax ? mergedSettings.priceMax.toString() : '';
          // Check if it's a custom range (not matching predefined ranges)
          const predefinedRange = priceRanges.find(range => 
            range.min === mergedSettings.priceMin && range.max === mergedSettings.priceMax
          );
          if (!predefinedRange) {
            showCustomPriceInput.value = true;
          }
        }
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
