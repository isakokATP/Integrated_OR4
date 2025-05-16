import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../views/HomeView.vue";
import SaleItemPage from "../views/SaleList.vue";
import SaleItemById from "../views/SaleListById.vue";
import NewSaleItem from "../views/NewSaleItem.vue";
import EditSaleItem from "../views/EditSaleItem.vue";
import SaleItemList from "../views/SaleItemsList.vue";
import BrandsList from "../views/BrandsList.vue";
import NewBrand from "../views/NewBrand.vue";
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
    path: "/sale-items/:id",
    name: "edit-sale-item",
    component: EditSaleItem,
  },
  {
    path: "/sale-items/list",
    name: "sale-items-list-page",
    component: SaleItemList,
  },
  {
    path: "/brands",
    name: "brands-list-page",
    component: BrandsList,
  },
  {
    path: "/brands/add",
    name: "new-brand-page",
    component: NewBrand,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
