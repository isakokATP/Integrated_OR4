<script setup>
import { ref, onMounted} from "vue";
import { fetchSaleItemById, fetchSaleItems } from '@/services/saleItemService';
import { useRoute, useRouter } from "vue-router";

const item = ref({});
const route = useRoute();
const id = route.params.id;
const router = useRouter();

onMounted(async() => {
    item.value = await fetchSaleItemById(id);
    if(item.value.status === "not_found"){
        router.push({name: "sale-items-page" });
    }
});
</script>

<template>
    <div class="grid">
        <p>Model:{{ item.model }}</p>
        <p>Brand:{{ item.brandName }}</p>
        <p>Description:{{ item.description }}</p>
        <p>price:{{ item.price }} BAHT</p>
        <p>Ram:{{ item.ramGb }} Gb</p>
        <p>Screen:{{ item.screensizeInch }}Inch</p>
        <p>quantity:{{ item.quantity }}</p>
        <p>storage:{{ item.storageGb }}Gb</p>
        <p>color:{{ item.color }}</p>
    </div>
</template>

<style scoped>

</style>