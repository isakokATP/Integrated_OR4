import { api } from "../api/client";

// Get auth token
const getAuthToken = () => {
  return localStorage.getItem("accessToken");
};

// --- Process Seller Groups ---
export function preprocessSellerGroups(sellerGroups) {
  return sellerGroups.map((group) => {
    const hasInsufficient = group.items.some(
      (item) => item.quantity > item.stock
    );

    const updatedItems = group.items.map((item) => ({
      ...item,
      status: hasInsufficient ? "Canceled" : "Confirmed",
    }));

    return {
      ...group,
      items: updatedItems,
    };
  });
}

// --- Place Order ---
export async function placeOrder(
  sellerGroups,
  shippingAddress = "",
  note = ""
) {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Not authenticated");

    const processedGroups = preprocessSellerGroups(sellerGroups);

    const payload = {
      sellerGroups: processedGroups,
      shippingAddress,
      note,
    };

    const response = await api.post("/itb-mshop/v2/orders", payload, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error) {
    console.error("Error placing order:", error);
    throw error;
  }
}

// --- Get Buyer Orders ---
export async function getBuyerOrders(userId) {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Not authenticated");

    const response = await api.get(`/itb-mshop/v2/users/${userId}/orders`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // api.get คืน body ที่ parse แล้วเลย (List<OrderSummaryDto>)
    return response;
  } catch (error) {
    console.error("Error getting buyer orders:", error);
    throw error;
  }
}

// --- Get Seller Orders ---
export async function getSellerOrders(sellerId) {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Not authenticated");

    const response = await api.get(`/itb-mshop/v2/sellers/${sellerId}/orders`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // api.get คืน body ที่ parse แล้วเลย (List<OrderResponseDto>)
    return response;
  } catch (err) {
    console.error("Error getting seller orders:", err);
    throw err;
  }
}

// --- Mark Order as Viewed ---
export async function markOrderViewed(orderId) {
  try {
    const token = getAuthToken();
    if (!token) throw new Error("Not authenticated");

    const response = await api.put(
      `/itb-mshop/v2/orders/${orderId}/view`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    // return updated order object
    return response.data;
  } catch (err) {
    console.error("Error marking order viewed:", err);
    throw err;
  }
}

// ... (skipping getOrderDetail as it wasn't requested to change, but keeping file structure)

// --- Classify Seller Orders ---
export function classifySellerOrders(orders) {
  const newOrders = orders.filter(
    (o) => !o.isViewed && o.orderStatus === "COMPLETED"
  );

  const canceledOrders = orders.filter((o) => o.orderStatus === "CANCELLED");

  const allOrders = orders
    .filter((o) => o.orderStatus === "COMPLETED")
    .sort((a, b) => b.orderId - a.orderId); // Sort by orderId desc (was orderNo)

  return { newOrders, canceledOrders, allOrders };
}

// --- Count New Orders ---
export function countNewOrders(orders) {
  return orders.filter((o) => !o.isViewed && o.orderStatus === "COMPLETED").length;
}

// --- Fetch and classify seller orders ---
export async function fetchAndClassifySellerOrders(sellerId) {
  try {
    const orders = await getSellerOrders(sellerId);
    return classifySellerOrders(orders);
  } catch (err) {
    console.error("Error fetching and classifying seller orders:", err);
    throw err;
  }
}

