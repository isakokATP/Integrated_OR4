<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItems } from "../services/saleItemService";
import { useRouter, useRoute } from "vue-router";

const items = ref([]);
const loading = ref(true);
const route = useRoute();
const msg = route.query.msg;
const source = route.query.source;
const showMsg = ref(msg === "success");

onMounted(async () => {
  items.value = Array.isArray(await fetchSaleItems())
    ? await fetchSaleItems()
    : [];
  loading.value = false;
  if (showMsg.value) {
    setTimeout(() => (showMsg.value = false), 3000);
    router.replace({ query: {} });
  }
});

const router = useRouter();

function goToSaleItem(id) {
  router.push({ name: "sale-items-page-byId", params: { id } });
}
function goToAddSaleItem() {
  router.push({ name: "new-sale-item-page" });
}
</script>

<template>
  <div class="max-w-6xl mx-auto mt-8">
    <div
      v-if="msg === 'success'"
      class="itbms-message bg-green-50 border border-green-300 text-green-800 px-6 py-3 mb-4 rounded"
    >
      <strong>success</strong>
      <div v-if="source === 'new'">
        The sale item has been successfully added.
      </div>
      <div v-else-if="source === 'edit'">The sale item has been updated.</div>
      <div v-else-if="source === 'delete'">The sale item has been deleted.</div>
    </div>
    <div
      v-if="msg === 'notfound'"
      class="itbms-message bg-red-50 border border-red-300 text-red-800 px-6 py-3 mb-4 rounded"
    >
      The requested sale item does not exist.
    </div>
    <button
      class="itbms-sale-item-add bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors duration-200 mb-6"
      @click="goToAddSaleItem"
    >
      Add Sale Item
    </button>
    <div v-if="loading" class="text-center text-gray-400">Loading...</div>
    <div v-else>
      <div
        v-if="Array.isArray(items) && items.length === 0"
        class="flex justify-center w-full"
      >
        no sale item
      </div>

      <div
        v-else-if="Array.isArray(items) && items.length > 0"
        class="grid grid-cols-5 gap-8"
      >
        <itbms-row v-for="item in items" :key="item.id" class="card itbms-row">
          <div @click="goToSaleItem(item.id)">
            <img
              src="../assets/iphone.png"
              alt="Product"
              class="card-image w-48 h-56 object-contain mx-auto"
            />
            <div class="card-info text-lg p-4">
              <strong class="itbms-brand">{{ item.brandName }}</strong>
              <p class="itbms-model">{{ item.model }}</p>
              <span class="itbms-ramGb">{{ item.ramGb ?? "-" }}</span
              >/
              <span class="itbms-storageGb">{{ item.storageGb ?? "-" }}</span>
              <span class="itbms-storageGb-unit">GB</span>
              <div class="itbms-price">
                <strong>Baht {{ item.price.toLocaleString() }}</strong>
              </div>
            </div>
          </div>
        </itbms-row>
      </div>
    </div>
    <div v-if="showMsg" class="itbms-msg ...">...</div>
  </div>
</template>
