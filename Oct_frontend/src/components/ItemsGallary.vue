<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItems } from "../services/saleItemService";
import { useRouter } from "vue-router";
import Header from "@/components/Header.vue";

const items = ref([]);
const loading = ref(true);

onMounted(async () => {
  items.value = Array.isArray(await fetchSaleItems())
    ? await fetchSaleItems()
    : [];
  loading.value = false;
});

const router = useRouter();

function goToSaleItem(id) {
  router.push({ name: "sale-items-page-byId", params: { id } });
}
</script>

<template>
  <div class="max-w-4xl mx-auto mt-8">
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
        class="grid grid-cols-5 gap-4"
      >
        <itbms-row v-for="item in items" :key="item.id" class="card itbms-row">
          <div @click="goToSaleItem(item.id)">
            <img src="../../public/images/iphone.png" class="card-image" />
            <div class="card-info">
              <strong class="itbms-brand">{{ item.brandName }}</strong>
              <p class="itbms-model">{{ item.model }}</p>
              <span class="itbms-ramGb">{{ item.ramGb ?? "-" }}</span>/
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
  </div>
</template>



