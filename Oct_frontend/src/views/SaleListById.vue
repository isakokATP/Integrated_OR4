<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItemById, fetchSaleItems } from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";

const item = ref({});
const route = useRoute();
const id = route.params.id;
const router = useRouter();

onMounted(async () => {
  item.value = await fetchSaleItemById(id);
  if (item.value.status === "not_found") {
    router.push({ name: "sale-items-page" });
  }
});
</script>

<template>
  <Header />
  <div class="max-w-4xl mx-auto mt-8">
    <div class="flex flex-col md:flex-row gap-8">
      <!-- รูปใหญ่ -->
      <div class="flex-shrink-0">
        <img
          src="../../public/images/iphone.png"
          class="w-72 h-80 object-contain rounded"
          alt="Product"
        />
      </div>
      <!-- รายละเอียด -->
      <div class="flex-1 space-y-2 text-base">
        <div>
          <span class="font-semibold">Brand :</span>
          <span class="ml-2">{{ item.brandName }}</span>
        </div>
        <div>
          <span class="font-semibold">Model :</span>
          <span class="ml-2">{{ item.model }}</span>
        </div>
        <div>
          <span class="font-semibold">Price :</span>
          <span class="ml-2 text-lg"
            >{{ item.price }} Baht</span
          >
        </div>
        <div>
          <span class="font-semibold">Description:</span>
          <div class="ml-2">{{ item.description }}</div>
        </div>
        <div>
          <span class="font-semibold">Ram :</span>
          <span class="ml-2">{{ item.ramGb }} GB</span>
        </div>
        <div>
          <span class="font-semibold">Screen Size :</span>
          <span class="ml-2">{{ item.screensizeInch }} inch</span>
        </div>
        <div>
          <span class="font-semibold">Storage :</span>
          <span class="ml-2">{{ item.storageGb }} GB</span>
        </div>
        <div>
          <span class="font-semibold">Color :</span>
          <span class="ml-2">{{ item.color }}</span>
        </div>
        <div>
          <span class="font-semibold">Available quantity :</span>
          <span class="ml-2">{{ item.quantity }} units</span>
        </div>
      </div>
    </div>
    <!-- แถบรูปเล็ก (thumbnail) -->
    <div class="flex gap-4 mt-6 ml-2">
      <!-- 
      <img
        v-for="(img, idx) in images"
        :key="idx"
        :src="img"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        @click="mainImage = img"
        :alt="'thumbnail-' + idx"
      />
      -->
      <img
        src="../../public/images/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
      <img
        src="../../public/images/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
      <img
        src="../../public/images/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
      <img
        src="../../public/images/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
    </div>
  </div>
</template>

<style scoped></style>
