<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItemById } from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";

const item = ref({});
const errorMsg = ref("");
const route = useRoute();
const id = route.params.id;
const router = useRouter();

onMounted(async () => {
  item.value = await fetchSaleItemById(id);
  if (item.value.status === "not_found") {
    errorMsg.value = "The requested sale item does not exist.";
    // แสดงข้อความก่อน redirect (เช่น 1 วินาที)
    setTimeout(() => {
      router.push({ name: "sale-items-page" });
    }, 2000);
  }
});
</script>

<template>
  <Header />
  <div class="max-w-4xl mx-auto mt-8">
    <!-- Breadcrumb -->
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link to="/" class="text-blue-600 hover:underline font-medium"
        >Home</router-link
      >
      <span class="mx-1">›</span>
      <span class="font-semibold">
        {{ item.brandName }} {{ item.model
        }}{{ item.ramGb ? " " + item.ramGb : ""
        }}{{ item.storageGb ? "/" + item.storageGb : ""
        }}{{ item.storageGb ? "GB" : ""
        }}{{ item.color ? " " + item.color : "" }}
      </span>
    </nav>
    <div
      v-if="errorMsg"
      class="itbms-message text-center text-red-600 text-lg my-8"
    >
      {{ errorMsg }}
    </div>
    <div v-else class="itbms-row flex flex-col md:flex-row gap-8">
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
        <div class="itbms-brand">Brand : {{ item.brandName }}</div>
        <div class="itbms-model">Model : {{ item.model }}</div>
        <div class="itbms-price">
          Price : {{ item.price?.toLocaleString() }}
        </div>
        <div class="itbms-description">
          Description : {{ item.description }}
        </div>
        <div>
          <span class="itbms-ramGb">RAM : {{ item.ramGb ?? "-" }}</span>
          <span class="itbms-ramGb-unit">GB</span>
        </div>
        <div>
          <span class="itbms-screenSizeInch">
            Screen Size : {{ item.screenSizeInch ?? "-" }}
          </span>
          <span class="itbms-screenSizeInch-unit">Inches</span>
        </div>
        <div>
          <span class="itbms-storageGb"
            >Storage : {{ item.storageGb ?? "-" }}</span
          >
          <span class="itbms-storageGb-unit">GB</span>
        </div>
        <div class="itbms-color">Color : {{ item.color ?? "-" }}</div>
        <div class="itbms-quantity">
          Available quantity : {{ item.quantity }}
        </div>
      </div>
    </div>
    <!-- แถบรูปเล็ก (thumbnail) -->
    <div v-if="!errorMsg" class="flex gap-4 mt-6 ml-2">
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
