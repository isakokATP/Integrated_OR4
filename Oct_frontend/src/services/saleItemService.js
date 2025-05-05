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

export { fetchSaleItems, fetchSaleItemById };
