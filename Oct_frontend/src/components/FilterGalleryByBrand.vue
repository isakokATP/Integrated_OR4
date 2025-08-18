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
    <div class="flex items-center gap-4">
      <label class="text-sm font-medium text-gray-700 min-w-[80px]">Price Range:</label>
      <div class="flex items-center gap-2">
        <input
          v-model.number="modelValue.priceMin"
          type="number"
          placeholder="Min"
          class="w-24 px-3 py-2 border border-gray-300 rounded-md text-sm"
          min="0"
          @input="updatePriceFilter"
        />
        <span class="text-gray-500">-</span>
        <input
          v-model.number="modelValue.priceMax"
          type="number"
          placeholder="Max"
          class="w-24 px-3 py-2 border border-gray-300 rounded-md text-sm"
          min="0"
          @input="updatePriceFilter"
        />
        <button
          @click="clearPriceFilter"
          class="bg-gray-200 px-3 py-2 hover:bg-gray-300 text-sm border border-gray-300 rounded-md font-semibold"
        >
          Clear
        </button>
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
        <div v-else class="text-gray-500 text-sm">Filter by storage size(s)</div>
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
const showStorageDropdown = ref(false);
const allBrands = ref([]);
const allStorageSizes = ref([]);

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

function updatePriceFilter() {
  emit("update:modelValue", props.modelValue);
  saveToSessionStorage(props.modelValue);
}

function clearPriceFilter() {
  const newValue = { ...props.modelValue, priceMin: null, priceMax: null };
  emit("update:modelValue", newValue);
  saveToSessionStorage(newValue);
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
    storageSizes: []
  };
  emit("update:modelValue", newValue);
  sessionStorage.removeItem("filterSettings");
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
