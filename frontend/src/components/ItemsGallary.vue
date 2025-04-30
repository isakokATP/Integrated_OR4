<template>
  <div class="container">
    <div class="grid">
      <div v-for="item in items" :key="item.id" class="card">
        <img src="../../public/images/iphone.png" class="card-image" />
        <div class="card-info">
          <div>
            <h3>{{ item.model }}</h3>
            {{ item.description }}
          
          
            {{ item.ramGb }}/{{ item.storageGb }}GB
          </div>
          <div>
            Baht {{ item.price }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

// สร้างตัวแปร reactive เพื่อเก็บข้อมูล
const items = ref([]);

onMounted(() => {
  fetch("/api/sale-items/all") // ใช้ /api ถ้าคุณตั้ง proxy ไว้ใน vite.config.js
    .then((response) => {
      if (!response.ok) throw new Error("Failed to fetch items");
      return response.json();
      console.log(data);
    })
    .then((data) => {
      items.value = data; // อัปเดตค่าของ items
    })
    .catch((error) => {
      console.error("Error fetching items:", error);
    });
});
</script>

<style scoped>
.container {
  padding: 20px;
}
h3 {
  margin: 0%;
}
h2 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 20px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(250px, 1fr)); /* จัดวาง 3 คอลัมน์ */
  gap: 20px;
  margin-top: 20px;
}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
  transform: translateY(-5px); /* ยกการ์ดเมื่อ hover */
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2); /* เพิ่มเงาเมื่อ hover */
}

.card-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.card-info h3 {
  font-size: 1.2rem;
  color: #333;
}

.card-info p {
  font-size: 1rem;
  color: #555;
  margin: 5px 0;
}

.card-info div {
  margin-bottom: 20px;
}
</style>
