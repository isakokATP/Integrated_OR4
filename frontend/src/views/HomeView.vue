<template>
  <div class="itb-mshop-container">
    <h1 class="itb-mshop-header">ITB MShop</h1>
    <p class="itb-mshop-description">
      ยินดีต้อนรับสู่ ITB MShop ที่นี่คุณสามารถเลือกซื้อสินค้าหลายประเภท
    </p>
    <button @click="goToSaleItem" class="sale-item-button">
      ไปที่สินค้าราคาพิเศษ
    </button>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

function goToSaleItem() {
  router.push({ name: "sale-items-page" });
}
</script>

<style scoped>
.itb-mshop-container {
  justify-content: center;
  align-items: center;
  padding: 20px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.itb-mshop-header {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.itb-mshop-description {
  font-size: 1rem;
  color: #666;
  margin-bottom: 20px;
}

.sale-item-button {
  padding: 10px 20px;
  font-size: 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.sale-item-button:hover {
  background-color: #0056b3;
}
</style>
