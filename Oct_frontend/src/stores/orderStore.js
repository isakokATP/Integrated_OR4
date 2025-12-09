import { ref } from 'vue';

export const newOrdersCount = ref(0);

export const setNewOrdersCount = (count) => {
  newOrdersCount.value = count;
};
