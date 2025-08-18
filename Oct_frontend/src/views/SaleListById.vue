<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { fetchSaleItemById, deleteSaleItem } from "../services/saleItemService";
import { useRoute, useRouter } from "vue-router";
import Header from "../components/Header.vue";
import Notification from "../components/Notification.vue";

const item = ref({});
const errorMsg = ref("");
const route = useRoute();
const id = route.params.id;
const router = useRouter();
const isLoading = ref(true);
const message = ref("");

// images from DB
const images = computed(() => (item.value?.saleItemImages ?? []));
const selectedIndex = ref(0);

const hasImages = computed(() => images.value.length > 0);

function getImageUrl(fileNameOrFilename) {
  const name = fileNameOrFilename ?? "";
  return `${import.meta.env.VITE_API_URL_PROD}/attachments/${encodeURIComponent(name)}`;
}

const mainImageUrl = computed(() => {
  if (!hasImages.value) return null;
  const first = images.value[selectedIndex.value] || images.value[0];
  return getImageUrl(first.fileName || first.filename);
});

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
  // Default selected index when images exist
  if (item.value?.saleItemImages?.length) {
    selectedIndex.value = 0;
  }
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
  // Replace browser confirm with custom dialog
  openDeleteDialog();
};

// เพิ่ม state สำหรับ dialog
const showConfirm = ref(false);
const itemToDelete = ref(null); // ใช้ item.value ที่โหลดมา

// Function เพื่อเปิด dialog
const openDeleteDialog = () => {
  // item.value ถูกโหลดมาแล้วใน onMounted
  itemToDelete.value = item.value; // กำหนด item ที่จะลบให้ dialog
  showConfirm.value = true; // แสดง dialog
};

// Function เมื่อกด Confirm
const confirmDelete = async () => {
  if (!itemToDelete.value) return; // ป้องกันถ้าไม่มี item ให้ลบ

  try {
    const response = await deleteSaleItem(itemToDelete.value.id);
    if (response.ok || response.status === 200) {
      // หากลบสำเร็จ ให้ไปที่หน้ารายการและแสดงข้อความ
      router.push({
        name: "sale-items-page",
        query: { message: "Item deleted successfully" },
      });
    } else {
      // หากลบไม่สำเร็จ (เช่น server error อื่นๆ)
      // สามารถจัดการ error message ตรงนี้ได้ ถ้าต้องการแสดงใน dialog
      throw new Error("Failed to delete item");
    }
  } catch (error) {
    console.error("Error deleting item:", error);
    // หากมี error จาก API (เช่น 404)
    router.push({
      name: "sale-items-page",
      query: { message: "Item has been deleted or not found" }, // หรือข้อความ error อื่นๆ
    });
  } finally {
    // ไม่ว่าจะสำเร็จหรือไม่ ให้ปิด dialog
    showConfirm.value = false;
    itemToDelete.value = null; // เคลียร์ข้อมูล itemToDelete
  }
};

// Function เมื่อกด Cancel
const cancelDelete = () => {
  showConfirm.value = false; // ปิด dialog
  itemToDelete.value = null; // เคลียร์ข้อมูล itemToDelete
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
            v-if="hasImages"
            :src="mainImageUrl"
            class="object-contain w-full h-full"
            alt="Main Product"
          />
          <div v-else class="text-gray-400">No Picture</div>
        </div>

        <!-- รูปรอง -->
        <div class="flex gap-2.5 mb-4">
          <div
            v-for="(img, idx) in images"
            :key="idx"
            class="w-16 h-16 border border-gray-300 rounded overflow-hidden bg-white cursor-pointer hover:ring-2 hover:ring-blue-400"
            @click="selectedIndex = idx"
          >
            <img
              :src="getImageUrl(img.fileName || img.filename)"
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
            class="itbms-edit-button w-24 bg-blue-900 text-white px-4 py-2 rounded hover:bg-blue-500"
          >
            Edit
          </button>
          <button
            @click="handleDelete"
            class="itbms-delete-button w-24 bg-red-700 text-white px-4 py-2 rounded hover:bg-red-500"
          >
            Delete
          </button>
        </div>
      </div>
    </div>

    <!-- Custom Confirmation Dialog -->
    <div
      v-if="showConfirm"
      class="itbms-message fixed inset-0 flex items-center justify-center bg-black bg-opacity-30 z-50"
    >
      <div class="bg-white p-6 rounded shadow-lg text-center">
        <div class="mb-4">Do you want to delete this sale item?</div>
        <div class="flex justify-center space-x-2">
          <button
            class="itbms-confirm-button btn bg-red-700 hover:bg-red-500 text-white px-4 py-2 rounded"
            @click="confirmDelete"
          >
            Confirm
          </button>
          <button
            class="itbms-cancel-button btn bg-gray-400 hover:bg-gray-500 text-white px-4 py-2 rounded"
            @click="cancelDelete"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ถ้าอยากใส่ style เพิ่มเติม สามารถใส่ที่นี่ */
</style>
