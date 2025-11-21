<template>
  <Header />
  <div class="max-w-6xl mx-auto p-6">
    <nav class="text-sm mb-4 flex items-center space-x-2">
      <router-link
        to="/sale-items"
        class="text-blue-600 hover:underline font-medium"
        >Home</router-link
      >
      <span class="mx-1">›</span>
      <span class="font-semibold">Shopping Cart</span>
    </nav>
    <h1 class="text-3xl font-bold mb-6">Shopping Cart</h1>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="loading loading-spinner loading-lg text-blue-600"></span>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-error mb-4">
      <span>{{ error }}</span>
    </div>

    <!-- Empty Cart -->
    <div v-else-if="!cartData || !cartData.sellerGroups || cartData.sellerGroups.length === 0" class="text-center py-12">
      <p class="text-xl text-gray-500">Your cart is empty</p>
      <router-link to="/sale-items" class="text-blue-600 hover:underline mt-4 inline-block">
        Continue Shopping
      </router-link>
    </div>

    <!-- Cart Content -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Shopping cart by seller -->
      <div class="lg:col-span-2 space-y-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-2xl font-semibold">Shopping cart by seller</h2>
          <div class="flex items-center gap-2">
            <input
              ref="selectAllCheckbox"
              type="checkbox"
              :checked="isSelectAllChecked"
              @change="toggleSelectAll"
              class="w-5 h-5 cursor-pointer"
            />
            <label class="font-medium cursor-pointer">Select All</label>
          </div>
        </div>
        
        <div v-for="sellerGroup in cartData.sellerGroups" :key="sellerGroup.sellerId" class="bg-white rounded-lg shadow-md p-6">
          <div class="flex items-center gap-2 mb-4">
            <input
              :ref="el => { if (el) sellerCheckboxes.value[sellerGroup.sellerId] = el }"
              type="checkbox"
              :checked="isSellerSelected(sellerGroup)"
              @change="toggleSellerSelection(sellerGroup)"
              class="w-5 h-5 cursor-pointer"
            />
            <h3 class="text-xl font-semibold">{{ sellerGroup.sellerNickname }}</h3>
          </div>
          
          <div class="space-y-4">
            <div
              v-for="item in sellerGroup.items"
              :key="item.cartItemId"
              class="border-b pb-4 last:border-b-0"
            >
              <div class="flex gap-4">
                <div class="flex items-start pt-1">
                  <input
                    type="checkbox"
                    :checked="selectedItems.has(item.cartItemId)"
                    @change="toggleItemSelection(item.cartItemId)"
                    class="w-5 h-5 cursor-pointer"
                  />
                </div>
                
                <!-- Product Image -->
                <div class="w-24 h-24 flex-shrink-0 border border-gray-300 rounded overflow-hidden bg-white">
                  <img
                    v-if="item.imageUrl"
                    :src="getImageUrl(item.imageUrl)"
                    :alt="`${item.brandName} ${item.model}`"
                    class="object-contain w-full h-full"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center text-gray-400 text-xs">
                    No Image
                  </div>
                </div>
                
                <div class="flex-1">
                  <h4 class="font-semibold text-lg">{{ item.brandName }} {{ item.model }}</h4>
                  <p class="text-gray-600 text-sm mt-1">
                    {{ item.ramGb ? `${item.ramGb}GB RAM` : '' }}
                    {{ item.storageGb ? ` / ${item.storageGb}GB Storage` : '' }}
                    {{ item.color ? ` / ${item.color}` : '' }}
                  </p>
                  <p class="text-lg font-semibold text-blue-600 mt-2">
                    ฿{{ item.price.toLocaleString() }}
                  </p>
                </div>
                
                <div class="flex items-center gap-3">
                  <div class="flex items-center gap-2">
                    <button
                      @click="decreaseQuantity(item.cartItemId, item.quantity)"
                      class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:border-blue-500 flex items-center justify-center"
                    >
                      -
                    </button>
                    <span class="w-12 text-center">{{ item.quantity }}</span>
                    <button
                      @click="increaseQuantity(item.cartItemId, item.quantity, item.availableStock)"
                      :disabled="item.quantity >= item.availableStock"
                      class="w-8 h-8 rounded-lg border-2 border-gray-300 hover:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
                    >
                      +
                    </button>
                  </div>
                  
                  <div class="text-right min-w-[100px]">
                    <p class="font-semibold text-lg">
                      ฿{{ (item.price * item.quantity).toLocaleString() }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Cart Summary -->
      <div class="lg:col-span-1">
        <div class="bg-white rounded-lg shadow-md p-6 sticky top-4">
          <h2 class="text-2xl font-semibold mb-4">Cart Summary</h2>
          
          <div class="space-y-4">
            <!-- Address Input -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Address:
              </label>
              <textarea
                v-model="shippingAddress"
                placeholder="Enter shipping address"
                rows="3"
                maxlength="500"
                class="w-full px-3 py-2 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100 resize-none"
              ></textarea>
              <p class="text-xs text-gray-500 mt-1">{{ shippingAddress.length }}/500</p>
            </div>
            
            <!-- Note Input -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Note:
              </label>
              <textarea
                v-model="note"
                placeholder="Enter note (optional)"
                rows="2"
                maxlength="500"
                class="w-full px-3 py-2 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100 resize-none"
              ></textarea>
              <p class="text-xs text-gray-500 mt-1">{{ note.length }}/500</p>
            </div>
            
            <div class="border-t pt-4 space-y-2">
              <div class="flex justify-between">
                <span class="text-gray-600">Total Items:</span>
                <span class="font-semibold">{{ selectedSummary.totalItems }}</span>
              </div>
              
              <div class="flex justify-between text-xl font-bold border-t pt-2">
                <span>Total Price:</span>
                <span class="text-blue-600">฿{{ selectedSummary.totalPrice.toLocaleString() }}</span>
              </div>
            </div>
            
            <button
              @click="handlePlaceOrder"
              :disabled="isPlacingOrder || selectedItems.size === 0 || !shippingAddress.trim()"
              class="w-full mt-6 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white px-6 py-3 rounded-lg font-medium shadow-md transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ isPlacingOrder ? 'Placing Order...' : 'Place Order' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Confirmation Dialog for Remove -->
    <div
      v-if="showRemoveConfirm"
      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-30 z-50"
    >
      <div class="bg-white p-6 rounded shadow-lg text-center max-w-md">
        <div class="mb-4">Do you want to remove the sale item from the cart?</div>
        <div class="flex justify-center gap-4">
          <button
            class="bg-red-700 hover:bg-red-500 text-white px-4 py-2 rounded"
            @click="confirmRemove"
          >
            Confirm
          </button>
          <button
            class="bg-gray-400 hover:bg-gray-500 text-white px-4 py-2 rounded"
            @click="cancelRemove"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import Header from '../components/Header.vue';
import { getCart, updateCartItemQuantity, removeCartItem } from '../services/cartService';
import { placeOrder } from '../services/orderService';
import { updateCartCount } from '../composables/useCartCount';

// Get image URL
const getImageUrl = (filename) => {
  if (!filename) return null;
  return `/or4/uploads/${encodeURIComponent(filename)}`;
};

const router = useRouter();
const loading = ref(false);
const error = ref('');
const cartData = ref(null);
const showRemoveConfirm = ref(false);
const itemToRemove = ref(null);
const selectedItems = ref(new Set()); // Store selected cartItemIds
const isPlacingOrder = ref(false);
const selectAllCheckbox = ref(null);
const sellerCheckboxes = ref({});
const shippingAddress = ref('');
const note = ref('');

const loadCart = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    cartData.value = await getCart();
    // Update cart count in header
    await updateCartCount();
  } catch (err) {
    if (err.status === 401) {
      router.push({ name: 'login-page' });
    } else {
      error.value = err.message || 'Failed to load cart';
    }
  } finally {
    loading.value = false;
  }
};

const increaseQuantity = async (cartItemId, currentQuantity, availableStock) => {
  if (currentQuantity >= availableStock) return;
  
  try {
    await updateCartItemQuantity(cartItemId, currentQuantity + 1);
    await loadCart(); // loadCart already calls updateCartCount
  } catch (err) {
    alert(err.message || 'Failed to update quantity');
  }
};

const decreaseQuantity = async (cartItemId, currentQuantity) => {
  if (currentQuantity <= 1) {
    // Show confirmation dialog
    itemToRemove.value = cartItemId;
    showRemoveConfirm.value = true;
    return;
  }
  
  try {
    await updateCartItemQuantity(cartItemId, currentQuantity - 1);
    await loadCart(); // loadCart already calls updateCartCount
  } catch (err) {
    alert(err.message || 'Failed to update quantity');
  }
};

const confirmRemove = async () => {
  if (!itemToRemove.value) return;
  
  try {
    await removeCartItem(itemToRemove.value);
    showRemoveConfirm.value = false;
    itemToRemove.value = null;
    await loadCart(); // loadCart already calls updateCartCount
  } catch (err) {
    alert(err.message || 'Failed to remove item');
    showRemoveConfirm.value = false;
    itemToRemove.value = null;
  }
};

const cancelRemove = () => {
  showRemoveConfirm.value = false;
  itemToRemove.value = null;
};

// Selection management
const isSelectAllChecked = computed(() => {
  if (!cartData.value || !cartData.value.sellerGroups) return false;
  const totalItems = cartData.value.sellerGroups.reduce((sum, group) => sum + group.items.length, 0);
  return totalItems > 0 && selectedItems.value.size === totalItems;
});

const isSelectAllIndeterminate = computed(() => {
  if (!cartData.value || !cartData.value.sellerGroups) return false;
  const totalItems = cartData.value.sellerGroups.reduce((sum, group) => sum + group.items.length, 0);
  return selectedItems.value.size > 0 && selectedItems.value.size < totalItems;
});

const toggleSelectAll = () => {
  if (!cartData.value || !cartData.value.sellerGroups) return;
  
  if (isSelectAllChecked.value) {
    // Deselect all
    selectedItems.value.clear();
  } else {
    // Select all
    cartData.value.sellerGroups.forEach(group => {
      group.items.forEach(item => {
        selectedItems.value.add(item.cartItemId);
      });
    });
  }
};

const isSellerSelected = (sellerGroup) => {
  return sellerGroup.items.every(item => selectedItems.value.has(item.cartItemId));
};

const isSellerIndeterminate = (sellerGroup) => {
  const selectedCount = sellerGroup.items.filter(item => selectedItems.value.has(item.cartItemId)).length;
  return selectedCount > 0 && selectedCount < sellerGroup.items.length;
};

const toggleSellerSelection = (sellerGroup) => {
  const allSelected = isSellerSelected(sellerGroup);
  
  if (allSelected) {
    // Deselect all items in this seller group
    sellerGroup.items.forEach(item => {
      selectedItems.value.delete(item.cartItemId);
    });
  } else {
    // Select all items in this seller group
    sellerGroup.items.forEach(item => {
      selectedItems.value.add(item.cartItemId);
    });
  }
};

const toggleItemSelection = (cartItemId) => {
  if (selectedItems.value.has(cartItemId)) {
    selectedItems.value.delete(cartItemId);
  } else {
    selectedItems.value.add(cartItemId);
  }
};

// Calculate selected items summary
const selectedSummary = computed(() => {
  if (!cartData.value || !cartData.value.sellerGroups) {
    return { totalItems: 0, totalPrice: 0 };
  }
  
  let totalItems = 0;
  let totalPrice = 0;
  
  cartData.value.sellerGroups.forEach(group => {
    group.items.forEach(item => {
      if (selectedItems.value.has(item.cartItemId)) {
        totalItems += item.quantity;
        totalPrice += item.price * item.quantity;
      }
    });
  });
  
  return { totalItems, totalPrice };
});

// Place order
const handlePlaceOrder = async () => {
  if (selectedItems.value.size === 0) {
    alert('Please select at least one item to place an order');
    return;
  }
  
  // Build seller groups with only selected items
  const sellerGroups = [];
  
  cartData.value.sellerGroups.forEach(group => {
    const selectedGroupItems = group.items
      .filter(item => selectedItems.value.has(item.cartItemId))
      .map(item => ({
        saleItemId: item.saleItemId,
        quantity: item.quantity
      }));
    
    if (selectedGroupItems.length > 0) {
      sellerGroups.push({
        sellerId: group.sellerId,
        items: selectedGroupItems
      });
    }
  });
  
  if (sellerGroups.length === 0) {
    alert('Please select at least one item to place an order');
    return;
  }
  
  // Validate address
  if (!shippingAddress.value.trim()) {
    alert('Please enter shipping address');
    return;
  }
  
  isPlacingOrder.value = true;
  try {
    await placeOrder(sellerGroups, shippingAddress.value.trim(), note.value.trim());
    // Reload cart after successful order (backend will remove ordered items)
    await loadCart(); // loadCart already calls updateCartCount
    selectedItems.value.clear();
    // Clear form
    shippingAddress.value = '';
    note.value = '';
    alert('Order placed successfully!');
  } catch (err) {
    alert(err.message || 'Failed to place order');
  } finally {
    isPlacingOrder.value = false;
  }
};

// Set indeterminate state for checkboxes
watch([isSelectAllIndeterminate, cartData], () => {
  nextTick(() => {
    if (selectAllCheckbox.value) {
      selectAllCheckbox.value.indeterminate = isSelectAllIndeterminate.value;
    }
  });
}, { immediate: true });

watch([cartData, selectedItems], () => {
  nextTick(() => {
    if (cartData.value && cartData.value.sellerGroups) {
      cartData.value.sellerGroups.forEach(group => {
        const checkbox = sellerCheckboxes.value[group.sellerId];
        if (checkbox) {
          checkbox.indeterminate = isSellerIndeterminate(group);
        }
      });
    }
  });
}, { deep: true });

onMounted(() => {
  loadCart();
});
</script>

