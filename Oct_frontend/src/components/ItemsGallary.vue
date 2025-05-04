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
  <div class="container">
    <div class="grid">
      <div v-if="items.length === 0" class="no-data-message">No Sale item</div>
      <div v-for="item in items" :key="item.id" class="card">
        <div @click="goToSaleItem(item.id)">
          <img src="../../public/images/iphone.png" class="card-image" />
          <div class="card-info">
            <h3>{{ item.model }}</h3>
            <p>Brand: {{ item.brandName }}</p>
            <p>Color: {{ item.color }}</p>
            <p>Ram:{{ item.ramGb }}GB</p>
            <p>{{ item.storageGb }}GB</p>

            <div>
              <strong>{{ item.price }} BAHT</strong>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
