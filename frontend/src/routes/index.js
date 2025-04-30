import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../views/HomeView.vue";
import SaleItemPage from "../views/SaleList.vue";

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
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
