<template>
  <div class="flex items-start gap-0.5">
    <!-- กล่องแสดงแบรนด์ -->
    <div
      class="itbms-brand-filter w-102.5 border border-gray-300 rounded-md px-4 py-2 flex flex-wrap gap-2 items-start min-h-[40px]"
      @click="showDropdown = !showDropdown"
    >
      <template v-if="modelValue.length">
        <div
          v-for="brand in modelValue"
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

    <!-- ปุ่ม Choose และ Clear All -->
    <div class="flex flex-col ml-1">
      <div class="flex gap-1.5">
        <button
          @click="showDropdown = !showDropdown"
          class="itbms-brand-filter-button bg-gray-200 px-3 py-2 hover:bg-blue-200 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
        >
          <span class="inline-block mr-1">Choose</span>
        </button>
        <button
          @click="clearAll"
          class="itbms-brand-filter-clear bg-gray-200 px-3 py-2 hover:bg-gray-300 h-[40px] text-sm flex items-center border border-gray-300 rounded-md font-semibold"
        >
          Clear
        </button>
      </div>

      <div v-if="showDropdown" class="relative mt-2">
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
</template>

<script setup>
import { ref, computed, onMounted, defineEmits, defineProps } from "vue";
import { fetchBrands } from "@/services/saleItemService";

const props = defineProps({
  modelValue: Array,
});
const emit = defineEmits(["update:modelValue"]);

const showDropdown = ref(false);
const allBrands = ref([]);

const availableBrands = computed(() =>
  allBrands.value
    .filter((b) => !props.modelValue.includes(b))
    .sort((a, b) => a.localeCompare(b))
);

function addBrand(brand) {
  const newSelectedBrands = [...props.modelValue, brand];
  emit("update:modelValue", newSelectedBrands);
  // Save to session storage
  sessionStorage.setItem("selectedBrands", JSON.stringify(newSelectedBrands));
}

function removeBrand(brand) {
  const newSelectedBrands = props.modelValue.filter((b) => b !== brand);
  emit("update:modelValue", newSelectedBrands);
  // Save to session storage
  sessionStorage.setItem("selectedBrands", JSON.stringify(newSelectedBrands));
}

function clearAll() {
  emit("update:modelValue", []);
  // Clear from session storage
  sessionStorage.removeItem("selectedBrands");
}

onMounted(async () => {
  // Load brands from the API
  const result = await fetchBrands();
  allBrands.value = result.map((b) => b.name);

  // Load selected brands from session storage
  const savedSelectedBrands = sessionStorage.getItem("selectedBrands");
  if (savedSelectedBrands) {
    try {
      const parsedBrands = JSON.parse(savedSelectedBrands);
      // Check if parsed data is an array before emitting
      if (Array.isArray(parsedBrands)) {
        emit("update:modelValue", parsedBrands);
      } else {
        console.error("Invalid data in session storage for selectedBrands.");
        // Optionally clear invalid data
        sessionStorage.removeItem("selectedBrands");
      }
    } catch (e) {
      console.error("Failed to parse selectedBrands from session storage:", e);
      // Clear invalid data
      sessionStorage.removeItem("selectedBrands");
    }
  }
});
</script>
