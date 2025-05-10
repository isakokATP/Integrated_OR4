<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItemById, deleteSaleItem } from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";

const item = ref({});
const errorMsg = ref("");
const route = useRoute();
const id = route.params.id;
const router = useRouter();
const isLoading = ref(true);

onMounted(async () => {
  item.value = await fetchSaleItemById(id);
  isLoading.value = false;
  if (item.value.status === "not_found") {
    errorMsg.value = "The requested sale item does not exist.";
    // แสดง alert ให้ user กด OK ก่อน redirect
    alert("The requested sale item does not exist.");
    router.push({ name: "sale-items-page" });
  }
});

// เพิ่มฟังก์ชัน handleEdit
const handleEdit = () => {
  router.push({ name: "edit-sale-item", params: { id: id } });
};

// เพิ่มฟังก์ชัน handleDelete
const handleDelete = async () => {
  if (confirm("Are you sure you want to delete this item?")) {
    try {
      const response = await deleteSaleItem(id);
      if (response.ok || response.status === 200) {
        alert("Item deleted successfully");
        router.push({ name: "sale-items-page" }); // Use push instead of back for more reliability
      } else {
        throw new Error("Failed to delete item");
      }
    } catch (error) {
      alert("Item was deleted, but there was an issue with the response. Redirecting...");
      console.error("Error:", error);
      router.push({ name: "sale-items-page" });
    }
  }
};
</script>

<template>
  <Header />
  <div v-if="isLoading"></div>
  <div v-else class="max-w-4xl mx-auto mt-8">
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
          src="../assets/iphone.png"
          class="w-72 h-80 object-contain rounded"
          alt="Product"
        />
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

        <!-- เพิ่มปุ่ม Edit และ Delete -->
        <div class="flex gap-4 mt-6">
          <button
            @click="handleEdit"
            class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Edit
          </button>
          <button
            @click="handleDelete"
            class="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
    <!-- แถบรูปเล็ก (thumbnail) -->
    <div v-if="!errorMsg" class="flex gap-4 mt-6 ml-2">
      <img
        src="../assets/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
      <img
        src="../assets/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
      <img
        src="../assets/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
      <img
        src="../assets/iphone.png"
        class="w-16 h-16 object-contain border rounded cursor-pointer hover:ring-2 hover:ring-blue-400"
        alt="Product"
      />
    </div>
  </div>
</template>

<style scoped></style>
