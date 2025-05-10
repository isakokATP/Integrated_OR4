import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../views/HomeView.vue";
import SaleItemPage from "../views/SaleList.vue";
import SaleItemById from "../views/SaleListById.vue";
import NewSaleItem from "../views/NewSaleItem.vue";
import EditSaleItem from "../views/EditSaleItem.vue";

const routes = [
  {
    path: "/",
    name: "home-page",
    component: HomePage,
  },
  {
    path: "/sale-items",
    name: "sale-items-page",
    component: SaleItemPage,
  },
  {
    path: "/sale-items/add",
    name: "new-sale-item-page",
    component: NewSaleItem,
  },
  {
    path: "/sale-items/:id",
    name: "sale-items-page-byId",
    component: SaleItemById,
  },
  {
    path: "/sale-items/:id/edit",
    name: "edit-sale-item",
    component: EditSaleItem,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
