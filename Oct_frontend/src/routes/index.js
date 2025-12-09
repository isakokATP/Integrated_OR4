import { createRouter, createWebHistory } from "vue-router";
import HomePage from "../views/HomeView.vue";
import SaleItemPage from "../views/SaleList.vue";
import SaleItemById from "../views/SaleListById.vue";
import NewSaleItem from "../views/NewSaleItem.vue";
import EditSaleItem from "../views/EditSaleItem.vue";
import SaleItemList from "../views/SaleItemsList.vue";
import BrandsList from "../views/BrandsList.vue";
import NewBrand from "../views/NewBrand.vue";
import EditBrand from "../views/EditBrand.vue";
import Register from "../views/Register.vue";
import Login from "../views/Login.vue";
import VerifyEmail from "../views/VerifyEmail.vue";
import Profile from "../views/Profile.vue";
import EditProfile from "../views/EditProfile.vue";
import SignOut from "../views/SignOut.vue";
import Cart from "../views/Cart.vue";
import YourOrders from "../views/YourOrders.vue";
import SaleOrders from "../views/SaleOrders.vue";

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
    name: "edit-sale-item-page",
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
  {
    path: "/brands/:id",
    name: "edit-brand-page",
    component: EditBrand,
  },
  {
    path: "/register",
    name: "register-page",
    component: Register,
  },
  {
    path: "/login",
    name: "login-page",
    component: Login,
  },
  {
    path: "/verify-email",
    name: "verify-email-page",
    component: VerifyEmail,
  },
  {
    path: "/profile",
    name: "profile-page",
    component: Profile,
  },
  {
    path: "/profile/edit",
    name: "edit-profile-page",
    component: EditProfile,
  },
  {
    path: "/signout",
    name: "signout-page",
    component: SignOut,
  },
  {
    path: "/cart",
    name: "cart-page",
    component: Cart,
  },
  {
    path: "/your-orders",
    name: "your-orders-page",
    component: YourOrders,
  },
  {
    path: "/sale-orders",
    name: "sale-orders-page",
    component: SaleOrders,
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Navigation guard to protect routes based on user role
router.beforeEach((to, from, next) => {
  // Routes that don't require authentication
  const publicRoutes = [
    "login-page",
    "register-page",
    "verify-email-page",
    "home-page",
    "sale-items-page",
    "sale-items-page-byId",
  ];

  if (publicRoutes.includes(to.name)) {
    next();
    return;
  }

  // Get token and user role
  const token = sessionStorage.getItem("accessToken");

  if (!token) {
    // Not logged in, redirect to login
    next({ name: "login-page" });
    return;
  }

  try {
    // Decode token to get user role
    const payload = JSON.parse(atob(token.split(".")[1]));
    const userRole = payload.role || payload.userType;

    // Seller-only routes
    const sellerOnlyRoutes = [
      "sale-items-list-page",
      "new-sale-item-page",
      "edit-sale-item-page",
      "new-brand-page",
      "edit-brand-page",
    ];

    // Routes accessible to both buyers and sellers (buying features)
    const buyingRoutes = ["cart-page", "your-orders-page"];

    // Check if route requires seller access
    if (sellerOnlyRoutes.includes(to.name)) {
      if (userRole !== "SELLER") {
        // Buyer trying to access seller page, redirect to sale items gallery
        next({ name: "sale-items-page" });
        return;
      }
    }

    // Buying routes (cart, orders) are accessible to both BUYER and SELLER
    // No restriction needed - both can buy and view their orders

    // Allow access
    next();
  } catch (e) {
    console.error("Error decoding token:", e);
    // Invalid token, redirect to login
    next({ name: "login-page" });
    return;
  }
});

export default router;
