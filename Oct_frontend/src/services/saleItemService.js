import { api, handleApiError } from "../api/client";
import { ENDPOINTS } from "../api/endpoints";

const URL = import.meta.env.VITE_API_URL_PROD;

console.log(URL);
async function fetchSaleItems() {
  try {
    const response = await fetch(`${URL}/itb-mshop/v1/sale-items`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Fetch error:", error);
  }
}

async function fetchSaleItemById(id) {
  try {
    const response = await fetch(`${URL}/itb-mshop/v1/sale-items/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return { ...data, httpStatus: response.status };
  } catch (error) {
    return { status: "not_found" };
  }
}

async function createSaleItem(saleItemData) {
  try {
    console.log("API URL:", URL);
    console.log("Full URL:", `${URL}/itb-mshop/v1/sale-items/add`);
    console.log(
      "Sending data to server:",
      JSON.stringify(saleItemData, null, 2)
    );

    const response = await fetch(`${URL}/itb-mshop/v1/sale-items/add`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify(saleItemData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      console.error("Server error:", errorData);
      throw new Error(
        `HTTP error! status: ${response.status}, message: ${
          errorData.message || "Unknown error"
        }`
      );
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Create sale item error:", error);
    throw handleApiError(error);
  }
}

export const deleteSaleItem = async (id) => {
  try {
    const response = await fetch(`${URL}/itb-mshop/v1/sale-items/${id}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Failed to delete item");
    }

    return await response.json();
  } catch (error) {
    throw error;
  }
};

export const updateSaleItem = async (id, saleItemData) => {
  try {
    const response = await fetch(`${URL}/itb-mshop/v1/sale-items/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(saleItemData),
    });

    if (!response.ok) {
      throw new Error("Failed to update item");
    }

    return await response.json();
  } catch (error) {
    throw error;
  }
};

export { fetchSaleItems, fetchSaleItemById, createSaleItem };
