<script setup>
import { ref, onMounted, watch } from "vue";
import { fetchSaleItemById, deleteSaleItem } from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";
import Notification from "@/components/Notification.vue";

const item = ref({});
const errorMsg = ref("");
const route = useRoute();
const id = route.params.id;
const router = useRouter();
const isLoading = ref(true);
const message = ref("");

// Watch for route changes to get message from query params
watch(
  () => route.query.message,
  (newMessage) => {
    if (newMessage) {
      message.value = newMessage;
      // Clear message after 3 seconds
      setTimeout(() => {
        message.value = "";
        // Remove message from URL without refreshing
        router.replace({ query: {} });
      }, 3000);
    }
  },
  { immediate: true }
);

onMounted(async () => {
  item.value = await fetchSaleItemById(id);
  isLoading.value = false;
  if (item.value.status === "not_found") {
    errorMsg.value = "The requested sale item does not exist.";
    alert("The requested sale item does not exist.");
    router.push({ name: "sale-items-page" });
  }
});

const handleEdit = () => {
  router.push({ name: "edit-sale-item-page", params: { id: id } });
};

const handleDelete = async () => {
  if (confirm("Are you sure you want to delete this item?")) {
    try {
      const response = await deleteSaleItem(id);
      if (response.ok || response.status === 200) {
        router.push({
          name: "sale-items-page",
          query: { message: "Item deleted successfully" },
        });
      } else {
        throw new Error("Failed to delete item");
      }
    } catch (error) {
      console.error("Error:", error);
      router.push({
        name: "sale-items-page",
        query: { message: "Item has been deleted" },
      });
    }
  }
};
</script>

<template>
  <Header />
  <Notification :message="message" />
  <div v-if="isLoading"></div>
  <div v-else class="max-w-4xl mx-auto mt-8">
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items"
        class="text-blue-600 hover:underline font-medium"
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

    <div
      v-if="errorMsg"
      class="itbms-message text-center text-red-600 text-lg my-8"
    >
      {{ errorMsg }}
    </div>

    <div v-else class="itbms-row flex flex-col md:flex-row gap-8">
      <!-- Left: Picture -->
      <div class="mt-2">
        <!-- รูปหลัก -->
        <div
          class="w-72 h-72 flex items-center justify-center mb-2.5 border border-gray-300 rounded overflow-hidden bg-white"
        >
          <img
            src="../assets/iphone.png"
            class="object-contain w-full h-full"
            alt="Main Product"
          />
        </div>

        <!-- รูปรอง -->
        <div class="flex gap-2.5 mb-4">
          <div
            v-for="n in 4"
            :key="n"
            class="w-16 h-16 border border-gray-300 rounded overflow-hidden bg-white cursor-pointer hover:ring-2 hover:ring-blue-400"
          >
            <img
              src="../assets/iphone.png"
              class="object-contain w-full h-full"
              alt="Thumbnail"
            />
          </div>
        </div>
      </div>

      <!-- รายละเอียด -->
      <div class="flex-1 space-y-2 text-base">
        <div class="itbms-brand">Brand : {{ item.brandName }}</div>
        <div class="itbms-model">Model : {{ item.model }}</div>
        <div class="itbms-price">
          Price : {{ item.price?.toLocaleString() }} Baht
        </div>
        <div class="itbms-description">
          Description : {{ item.description }}
        </div>
        <div>
          <span class="itbms-ramGb">RAM : {{ item.ramGb ?? "-" }}</span>
          <span class="itbms-ramGb-unit"> GB</span>
        </div>
        <div>
          <span class="itbms-screenSizeInch">
            Screen Size : {{ item.screenSizeInch ?? "-" }}
            <span class="itbms-screenSizeInch-unit"> Inches</span>
          </span>
        </div>
        <div>
          <span class="itbms-storageGb"
            >Storage : {{ item.storageGb ?? "-" }}</span
          >
          <span class="itbms-storageGb-unit"> GB</span>
        </div>
        <div class="itbms-color">Color : {{ item.color ?? "-" }}</div>
        <div class="itbms-quantity">
          Available quantity : {{ item.quantity }} units
        </div>

        <div class="flex gap-4 mt-6">
          <button
            @click="handleEdit"
            class="w-24 bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-500"
          >
            Edit
          </button>
          <button
            @click="handleDelete"
            class="w-24 bg-red-700 text-white px-4 py-2 rounded hover:bg-red-500"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ถ้าอยากใส่ style เพิ่มเติม สามารถใส่ที่นี่ */
</style>
