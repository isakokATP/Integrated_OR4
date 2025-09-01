import { handleApiError } from "../api/client";
import { ENDPOINTS } from "../api/endpoints";

// Get API URL from environment variables with fallback
const getApiUrl = () => {
  // Check if we're in development or production
  const isLocalhost = window.location.hostname === "localhost" || 
                     window.location.hostname === "127.0.0.1" || 
                     window.location.hostname === "::1";
  
  if (isLocalhost) {
    return import.meta.env.VITE_API_URL_DEV || "http://localhost:8080";
  } else {
    // For production (university server), use relative path with /or4
    return "/or4";
  }
};

const URL = getApiUrl();

// API URL loaded from environment variables

async function fetchSaleItemsV2(
  page = 1,
  size = 10,
  sortType = "default",
  filters = {
    brands: [],
    priceMin: null,
    priceMax: null,
    storageSizes: []
  }
) {
  try {
    const params = new URLSearchParams();
    
    // Basic pagination
    params.append('page', page - 1);
    params.append('size', size);
    
    // Sort parameters
    if (sortType === "asc") {
      params.append('sortField', 'brand.name');
      params.append('sortDirection', 'asc');
    } else if (sortType === "desc") {
      params.append('sortField', 'brand.name');
      params.append('sortDirection', 'desc');
    } else {
      // Default sort
      params.append('sortField', 'id');
      params.append('sortDirection', 'asc');
    }
    
    // Brand filter
    if (filters.brands && filters.brands.length > 0) {
      filters.brands.forEach(brand => {
        params.append('filterBrands', brand);
      });
    }
    
    // Price filter - ใช้ชื่อ parameter ที่ Backend รองรับ
    if (filters.priceMin !== null && filters.priceMin !== undefined) {
      params.append('filterPriceLower', filters.priceMin);
    }
    if (filters.priceMax !== null && filters.priceMax !== undefined) {
      params.append('filterPriceUpper', filters.priceMax);
    }
    
    // Storage filter - ใช้ชื่อ parameter ที่ Backend รองรับ
    if (filters.storageSizes && filters.storageSizes.length > 0) {
      filters.storageSizes.forEach(storage => {
        if (storage === 'not_specified') {
          // Handle not specified case - send null to backend
          params.append('storageSize', 'null');
        } else {
          params.append('storageSize', storage);
        }
      });
    }
    
    const url = `${URL}${ENDPOINTS.SALE_ITEMS.V2_ALL}?${params.toString()}`;
    
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error("Failed to fetch sale items");
    }
    return await response.json();
  } catch (error) {
    console.error("Error fetching sale items:", error);
    throw error;
  }
}

async function fetchSaleItemById(id) {
  try {
    // Prefer v2 (has saleItemImages); if not available in BE, v1 still works for core fields
    const response = await fetch(`${URL}${ENDPOINTS.SALE_ITEMS.BY_ID(id)}`, {
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
    console.log("Full URL:", `${URL}${ENDPOINTS.SALE_ITEMS.CREATE}`);

    const response = await fetch(`${URL}${ENDPOINTS.SALE_ITEMS.CREATE}`, {
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
    const response = await fetch(`${URL}${ENDPOINTS.SALE_ITEMS.DELETE(id)}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Failed to delete item");
    }

    return {
      status: response.status,
      ok: response.ok,
    };
  } catch (error) {
    throw error;
  }
};

export const updateSaleItem = async (id, saleItemData) => {
  try {
    const response = await fetch(`${URL}${ENDPOINTS.SALE_ITEMS.UPDATE(id)}`, {
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

// Brand related functions
async function fetchBrands() {
  try {
    const response = await fetch(`${URL}${ENDPOINTS.BRANDS.ALL}`, {
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
    console.error("Fetch brands error:", error);
    throw handleApiError(error);
  }
}

async function fetchStorageSizes() {
  try {
    // ดึงข้อมูล sale items ทั้งหมดจาก V2 API เพื่อเอา storage sizes ที่มีอยู่จริง
    const response = await fetch(`${URL}${ENDPOINTS.SALE_ITEMS.V2_ALL}?page=0&size=1000`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    const saleItems = result.content || result; // V2 API returns paginated result
    
    // ดึง storage sizes ที่ไม่ซ้ำกันและไม่เป็น null
    const storageSizes = [...new Set(
      saleItems
        .map(item => item.storageGb)
        .filter(storage => storage !== null && storage !== undefined)
    )].sort((a, b) => a - b);
    
    return storageSizes;
  } catch (error) {
    console.error("Fetch storage sizes error:", error);
    // Fallback to hardcoded values if API fails
    return [32, 64, 128, 256, 512, 1024, 2048];
  }
}

async function fetchBrandById(id) {
  try {
    const response = await fetch(`${URL}${ENDPOINTS.BRANDS.BY_ID(id)}`, {
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

async function createBrand(brandData) {
  try {
    const response = await fetch(`${URL}${ENDPOINTS.BRANDS.CREATE}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(brandData),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Create brand error:", error);
    throw handleApiError(error);
  }
}

async function updateBrand(id, brandData) {
  try {
    const response = await fetch(`${URL}${ENDPOINTS.BRANDS.UPDATE(id)}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(brandData),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Update brand error:", error);
    throw handleApiError(error);
  }
}

async function deleteBrand(id) {
  try {
    const response = await fetch(`${URL}${ENDPOINTS.BRANDS.DELETE(id)}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return true;
  } catch (error) {
    console.error("Delete brand error:", error);
    throw handleApiError(error);
  }
}

async function uploadAttachment(formData) {
  try {
    const response = await fetch(`${URL}/api/attachments/upload`, {
      method: "POST",
      body: formData, // Don't set Content-Type header for FormData
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Upload attachment error:", error);
    throw handleApiError(error);
  }
}

export {
  fetchSaleItemsV2,
  fetchSaleItemById,
  createSaleItem,
  fetchBrands,
  fetchStorageSizes,
  fetchBrandById,
  createBrand,
  updateBrand,
  deleteBrand,
  uploadAttachment,
};
