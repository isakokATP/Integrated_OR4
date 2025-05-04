<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItems } from "../services/saleItemService";
import { useRouter } from "vue-router";

const items = ref([]);

onMounted(async () => {
  items.value = await fetchSaleItems();
  console.log(items.value);
});

const router = useRouter();

function goToSaleItem(id) {
  router.push({ name: "sale-items-page-byId", params: { id } });
}
</script>

<template>
  <div class="container mx-auto px-4">
    <div v-if="items.length === 0" class="flex justify-center w-full">
      No Sale item
    </div>
    <div v-else class="grid grid-cols-5 gap-4">
      <div v-for="item in items" :key="item.id" class="card">
        <div @click="goToSaleItem(item.id)">
          <img src="../../public/images/iphone.png" class="card-image" />
          <div class="card-info">
            <div>
              <strong>{{ item.brandName }}</strong>
              <p>{{ item.model }}</p>
              <p>{{ item.ramGb }}/{{ item.storageGb }}GB</p>
            </div>
            <div class="mt-4">
              <strong>BAHT {{ item.price }}</strong>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
