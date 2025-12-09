<template>
  <table class="min-w-full border border-gray-200">
    <tbody>
      <tr
        v-for="order in orders"
        :key="order.orderId"
        :class="{
          'bg-yellow-100': order.status === 'Completed' && !order.isViewed,
          'hover:bg-gray-100': true
        }"
      >
        <td class="px-3 py-2 border-r">{{ order.orderId }}</td>
        <td class="px-3 py-2 border-r">{{ order.status }}</td>
        <td class="px-3 py-2 border-r">
          <span
            v-if="order.status === 'Completed' && !order.isViewed"
            class="bg-red-500 text-white text-xs px-1 rounded"
          >
            New
          </span>
        </td>
        <td class="px-3 py-2">
          <button
            @click="$emit('viewOrder', order)"
            class="bg-blue-500 text-white px-2 py-1 rounded"
          >
            View
          </button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script>
export default {
  props: {
    orders: {
      type: Array,
      required: true,
    },
  },
};
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 6px 10px;
  border: 1px solid #ccc;
}
</style>
