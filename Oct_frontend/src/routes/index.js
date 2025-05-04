import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../views/HomeView.vue";
import SaleItemPage from "../views/SaleList.vue";
import SaleItemById from "../views/SaleListById.vue"

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
    path: "/sale-items/:id",
    name: "sale-items-page-byId",
    component: SaleItemById,
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
