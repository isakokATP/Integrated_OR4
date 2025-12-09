import { api } from "../api/client"; 

// Get auth token
const getAuthToken = () => {
  return sessionStorage.getItem("accessToken");
};

// --- Process Seller Groups ---
export function preprocessSellerGroups(sellerGroups) {
  return sellerGroups.map(group => {
    const hasInsufficient = group.items.some(
      item => item.quantity > item.stock
    );

    // assign status to every item
    const updatedItems = group.items.map(item => ({
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
    if (!token) {
      throw new Error("Not authenticated");
    }

    // This is the correct processed sellerGroups
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

    return response;
  } catch (error) {
    console.error("Error placing order:", error);
    throw error;
  }
}

// --- Get Buyer Orders ---
export async function getBuyerOrders(userId) {
  try {
    const token = getAuthToken();
    if (!token) {
      throw new Error("Not authenticated");
    }

    const response = await api.get(
      `/itb-mshop/v2/users/${userId}/orders`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    return response;
  } catch (error) {
    console.error("Error getting buyer orders:", error);
    throw error;
  }
}
