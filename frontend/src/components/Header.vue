<template>
  <header class="header">
    <div class="top-bar">
      <div class="auth-buttons">
        <button class="btn">Sign In</button>
        <button class="btn">Register</button>
      </div>
    </div>

    <div class="main-bar">
      <div class="logo">ITB MShop</div>

      <div class="search-box">
        <input type="text" placeholder="ค้นหาสินค้า..." />
        <button class="search-btn">ค้นหา</button>
      </div>

      <div class="icons">
        <span class="icon">👤</span>
        <span class="icon">🛒</span>
      </div>
    </div>
  </header>
</template>

<script setup>
// ไม่จำเป็นต้องมี logic ในตอนนี้
</script>

<style scoped>
.header {
  width: 100%;
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  font-family: sans-serif;
}

.top-bar {
  display: flex;
  justify-content: flex-start;
  padding: 8px 20px;
  background-color: #b32727;
  border-bottom: 1px solid #ddd;
}

.auth-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  width: 100%;
  
}

.btn {
  padding: 6px 12px;
  font-size: 0.9rem;
  border: none;
  color: white;
  background-color: #b32727;
  text-decoration: none;
  cursor: pointer;
}

.main-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.search-box {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 0 20px;
}

.search-box input {
  width: 300px;
  padding: 8px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-btn {
  padding: 8px 16px;
  font-size: 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #0056b3;
}

.icons {
  display: flex;
  gap: 20px;
  font-size: 1.5rem;
  color: #444;
  align-items: center;
}
</style>
