<script setup>
import { useRouter } from "vue-router";

const props = defineProps({
  items: Array,
  loading: Boolean,
});

const router = useRouter();

function goToSaleItem(id) {
  router.push({ name: "sale-items-page-byId", params: { id } });
}
</script>

<template>
  <div class="max-w-6xl mx-auto mt-8">
    <div v-if="loading" class="text-center text-gray-400">Loading...</div>

    <div v-else>
      <div
        v-if="Array.isArray(items) && items.length === 0"
        class="flex justify-center w-full"
      >
        No sale item
      </div>

      <div
        v-else-if="Array.isArray(items) && items.length > 0"
        class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6"
      >
        <div
          v-for="item in items"
          :key="item.id"
          @click="goToSaleItem(item.id)"
          class="itbms-row bg-white rounded-lg overflow-hidden cursor-pointer transition duration-300 custom-shadow"
        >
          <img
            src="../assets/iphone.png"
            alt="Product"
            class="w-full h-56 object-cover"
          />
          <div class="p-4 text-center" style="background-color: #e3f2fd">
            <strong class="itbms-brand lock text-lg">{{
              item.brandName
            }}</strong>
            <p class="itbms-model text-gray-600">{{ item.model }}</p>
            <span class="itbms-ramGb text-sm text-gray-500">
              {{ item.ramGb ?? "-" }}
            </span>
            <span class="itbms-storageGb text-sm text-gray-500">
              / {{ item.storageGb ?? "-" }}
            </span>
            <span class="itbms-storageGb-unit text-sm text-gray-500"> GB </span>
            <div class="itbms-price text-blue-800 font-bold mt-2">
              Baht {{ item.price.toLocaleString() }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.custom-shadow {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.11);
  transition: box-shadow 0.3s ease;
}
.custom-shadow:hover {
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.15);
}
</style>
