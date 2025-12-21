<template>
  <div v-if="visible" class="modal-overlay" @click.self="close">
    <div class="modal bg-white rounded-lg shadow-xl w-full max-w-2xl overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
        <h3 class="text-lg font-bold text-gray-800">Order Detail #{{ order.orderId }}</h3>
        <button @click="close" class="text-gray-400 hover:text-gray-600 transition-colors">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Content -->
      <div class="p-6 overflow-y-auto">
        <!-- Order Info -->
        <div class="grid grid-cols-2 gap-4 mb-6">
          <div>
            <p class="text-sm text-gray-500">Buyer</p>
            <p class="font-medium">{{ order.buyerNickname || 'Unknown' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Order Date</p>
            <p class="font-medium">{{ formatDate(order.orderTimestamp) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Status</p>
            <span class="px-2 py-1 rounded text-xs font-semibold" 
                  :class="{
                    'bg-green-100 text-green-800': order.orderStatus === 'COMPLETED',
                    'bg-red-100 text-red-800': order.orderStatus === 'CANCELLED',
                    'bg-yellow-100 text-yellow-800': order.orderStatus === 'PENDING'
                  }">
              {{ order.orderStatus }}
            </span>
          </div>
          <div>
            <p class="text-sm text-gray-500">Total Amount</p>
            <p class="font-bold text-blue-600">฿{{ formatCurrency(order.totalPrice) }}</p>
          </div>
        </div>

        <!-- Address & Note -->
        <div class="mb-6 bg-gray-50 p-4 rounded-lg">
          <div class="mb-2">
            <span class="text-xs font-bold text-gray-500 uppercase tracking-wide">Shipping Address</span>
            <p class="text-sm text-gray-800 mt-1">{{ order.shippingAddress || '-' }}</p>
          </div>
          <div v-if="order.note">
            <span class="text-xs font-bold text-gray-500 uppercase tracking-wide">Note</span>
            <p class="text-sm text-gray-800 mt-1">{{ order.note }}</p>
          </div>
        </div>

        <!-- Items List -->
        <div>
          <h4 class="font-bold text-gray-800 mb-3">Items ({{ order.items ? order.items.length : 0 }})</h4>
          <div class="space-y-3">
            <div v-for="item in order.items" :key="item.saleItemId" 
                 class="flex gap-4 p-3 border border-gray-100 rounded-lg hover:bg-gray-50 transition-colors">
              <!-- Item Image -->
              <div class="w-16 h-16 bg-white border border-gray-200 rounded flex-shrink-0 overflow-hidden">
                <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="w-full h-full object-cover" alt="Item">
                <div v-else class="w-full h-full flex items-center justify-center text-gray-300 text-xs">No Img</div>
              </div>
              
              <!-- Details -->
              <div class="flex-1">
                <p class="font-medium text-gray-900 text-sm line-clamp-1">{{ item.brandName }} {{ item.model }}</p>
                <p class="text-xs text-gray-500 mb-1">{{ item.color }} · {{ item.storageGb }}GB</p>
                <div class="flex justify-between items-end">
                   <p class="text-xs text-gray-600">Qty: {{ item.quantity }}</p>
                   <p class="text-sm font-semibold">฿{{ formatCurrency(item.quantity * item.unitPrice) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-gray-100 bg-gray-50 flex justify-end">
        <button @click="close" class="px-4 py-2 bg-white border border-gray-300 rounded text-gray-700 hover:bg-gray-100 font-medium text-sm transition-colors">
          Close
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    order: {
      type: Object,
      default: () => ({})
    },
    visible: Boolean,
  },
  emits: ["close"],
  setup(props, { emit }) {
    const close = () => {
      emit("close");
    };

    const formatCurrency = (value) => {
      return (value || 0).toLocaleString();
    };

    const formatDate = (dateString) => {
        if (!dateString) return "-";
        return new Date(dateString).toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    };

    const getImageUrl = (filename) => {
      return filename ? `/uploads/${encodeURIComponent(filename)}` : null;
    };

    return { close, formatCurrency, formatDate, getImageUrl };
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 1rem;
}

/* Modal animation (optional) */
.modal {
  animation: modal-in 0.2s ease-out;
}

@keyframes modal-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
