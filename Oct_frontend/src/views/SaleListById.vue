<script setup>
import { ref, onMounted } from "vue";
import { fetchSaleItemById, deleteSaleItem } from "@/services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "@/components/Header.vue";
import status from "daisyui/components/status";

const item = ref({});
const errorMsg = ref("");
const route = useRoute();
const id = route.params.id;
const router = useRouter();
const isLoading = ref(true);
const msg = route.query.msg;
const source = route.query.source;
const showMsg = ref(msg === "success");
const showDeleteDialog = ref(false);

onMounted(async () => {
  item.value = await fetchSaleItemById(id);
  isLoading.value = false;
  if (item.value.status === "not_found") {
    errorMsg.value = "The requested sale item does not exist.";
    // แสดง alert ให้ user กด OK ก่อน redirect
    alert("The requested sale item does not exist.");
    setTimeout(() => {
      router.push({ name: "sale-items-page" });
    }, 3000);
  }
  if (showMsg.value) {
    setTimeout(() => {
      showMsg.value = false;
      router.replace({ query: {} });
    }, 3000);
  }
});

// เพิ่มฟังก์ชัน handleEdit
const handleEdit = () => {
  router.push({ name: "edit-sale-item", params: { id: id } });
};

// เพิ่มฟังก์ชัน handleDelete
const handleDelete = async () => {
  showDeleteDialog.value = true;
};

const confirmDelete = async () => {
  try {
    const response = await deleteSaleItem(id);
    if (response.ok || response.status === 204) {
      router.push({
        name: "sale-items-page",
        query: { msg: "success", source: "delete" },
      });
    } else {
      throw new Error("Failed to delete item");
    }
  } catch (error) {
    console.log("error:", error.status);
    if (error.status === 404) {
      errorMsg.value = "The requested sale item does not exist.";
      router.push({ name: "sale-items-page", query: { msg: "notfound" } });
    } else {
      router.push({ name: "sale-items-page" });
    }
  }
};

const cancelDelete = () => {
  showDeleteDialog.value = false;
};
</script>

<template>
  <Header />
  <div v-if="isLoading"></div>
  <div v-else class="max-w-4xl mx-auto mt-8">
    <!-- Delete Confirmation Dialog -->
    <div
      v-if="showDeleteDialog"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center"
    >
      <div class="bg-white p-6 rounded-lg shadow-lg">
        <div class="itbms-message mb-4">
          Do you want to delete this sale item?
        </div>
        <div class="flex justify-end gap-4">
          <button
            @click="cancelDelete"
            class="itbms-cancel-button px-4 py-2 border border-gray-400 rounded hover:bg-gray-100"
          >
            Cancel
          </button>
          <button
            @click="confirmDelete"
            class="itbms-confirm-button px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>

    <!-- Breadcrumb -->
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items"
        class="itbms-home-button text-blue-600 hover:underline font-medium"
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
      v-if="showMsg"
      class="itbms-message bg-green-50 border border-green-300 text-green-800 px-6 py-3 mb-4 rounded"
    >
      <strong>success</strong>
      <div v-if="source === 'new'">
        The sale item has been successfully added.
      </div>
      <div v-else>The sale item has been updated.</div>
    </div>
    <div
      v-if="errorMsg"
      class="itbms-message bg-red-50 border border-red-300 text-red-800 px-6 py-3 mb-4 rounded"
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
            class="itbms-edit-button bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Edit
          </button>
          <button
            @click="handleDelete"
            class="itbms-delete-button bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
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
