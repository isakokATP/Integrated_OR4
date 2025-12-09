<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal">
      <h3>Order Detail</h3>
      <p><strong>Order No:</strong> {{ order.orderNo }}</p>
      <p><strong>Item:</strong> {{ order.itemName }}</p>
      <p><strong>Quantity:</strong> {{ order.quantity }}</p>
      <p><strong>Status:</strong> {{ order.status }}</p>
      <button @click="close">Close</button>
    </div>
  </div>
</template>

<script>
import { ref, watch } from "vue";

export default {
  props: {
    order: Object,
    visible: Boolean,
  },
  emits: ["close"],
  setup(props, { emit }) {
    const close = () => {
      emit("close");
    };

    return { close };
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background: white;
  padding: 20px;
  border-radius: 8px;
  min-width: 300px;
}
</style>
