import { handleApiError } from "../api/client";

const URL = import.meta.env.VITE_API_URL_PROD;

// API URL loaded from environment variables

async function fetchSaleItemsV2(
  page = 1,
  size = 10,
  sortType = "default",
  filters = {
    brands: [],
    priceMin: null,
    priceMax: null,
    storageSizes: [],
    searchKeyWord: null
  }
) {
  try {
    const params = new URLSearchParams();
    
    // Basic pagination - Backend expects 0-based indexing
    params.append('page', page - 1);
    params.append('size', size);
    
    // Sort parameters - Fixed to match Backend
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
    
    // Brand filter - Fixed parameter name to match Backend
    if (filters.brands && filters.brands.length > 0) {
      filters.brands.forEach(brand => {
        params.append('filterBrands', brand);
      });
    }
    
    // Price filter - Fixed parameter names to match Backend
    if (filters.priceMin !== null && filters.priceMin !== undefined) {
      params.append('filterPriceLower', filters.priceMin);
    }
    if (filters.priceMax !== null && filters.priceMax !== undefined) {
      params.append('filterPriceUpper', filters.priceMax);
    }
    
    // Storage filter - Fixed parameter name to match Backend
    if (filters.storageSizes && filters.storageSizes.length > 0) {
      filters.storageSizes.forEach(storage => {
        if (storage === 'not_specified') {
          // Handle not specified case - send -1 to backend (matches JPQL logic)
          params.append('filterStorages', -1);
        } else {
          params.append('filterStorages', storage);
        }
      });
    }

    // Search keyword - Added to match Backend
    if (filters.searchKeyWord && filters.searchKeyWord.trim() !== '') {
      params.append('searchKeyWord', filters.searchKeyWord.trim());
    }
    
    const url = `${URL}/itb-mshop/v2/sale-items?${params.toString()}`;
    
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
    // Use V2 endpoint for detailed view with images
    const response = await fetch(`${URL}/itb-mshop/v2/sale-items/${id}`, {
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

async function createSaleItem(saleItemData, images = null) {
  try {
    console.log("API URL:", URL);
    console.log("Full URL:", `${URL}/itb-mshop/v2/sale-items`);

    // Backend uses @ModelAttribute which requires FormData
    // Always use FormData regardless of whether images are provided
    const formData = new FormData();
    
    // Add sale item data
    Object.keys(saleItemData).forEach(key => {
      // Handle nested objects like brand
      if (key === 'brand' && saleItemData[key]) {
        formData.append('brand.id', saleItemData[key].id);
      } else {
        formData.append(key, saleItemData[key]);
      }
    });
    
    // Add images if provided
    if (images && images.length > 0) {
      images.forEach((image, index) => {
        formData.append('SaleItemImages', image);
      });
    }

    const response = await fetch(`${URL}/itb-mshop/v2/sale-items`, {
      method: "POST",
      body: formData, // Don't set Content-Type for FormData
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
    const response = await fetch(`${URL}/itb-mshop/v2/sale-items/${id}`, {
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

export const updateSaleItem = async (id, saleItemData, images = null) => {
  try {
    // Backend uses @ModelAttribute which requires FormData
    // Always use FormData regardless of whether images are provided
    const formData = new FormData();
    
    // Add sale item data
    Object.keys(saleItemData).forEach(key => {
      // Handle nested objects like brand
      if (key === 'brand' && saleItemData[key]) {
        formData.append('brand.id', saleItemData[key].id);
      } else {
        formData.append(key, saleItemData[key]);
      }
    });
    
    // Add images if provided
    if (images && images.length > 0) {
      images.forEach((image, index) => {
        formData.append('SaleItemImages', image);
      });
    }

    const response = await fetch(`${URL}/itb-mshop/v2/sale-items/${id}`, {
      method: "PUT",
      body: formData, // Don't set Content-Type for FormData
    });

    if (!response.ok) {
      throw new Error("Failed to update item");
    }
    return await response.json();
  } catch (error) {
    throw error;
  }
};

// Brand related functions - Updated to use correct endpoints
async function fetchBrands() {
  try {
    const response = await fetch(`${URL}/itb-mshop/v1/brands`, {
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
    const response = await fetch(`${URL}/itb-mshop/v2/sale-items?page=0&size=1000`, {
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
    const response = await fetch(`${URL}/itb-mshop/v1/brands/${id}`, {
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
    const response = await fetch(`${URL}/itb-mshop/v1/brands`, {
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
    const response = await fetch(`${URL}/itb-mshop/v1/brands/${id}`, {
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
    const response = await fetch(`${URL}/itb-mshop/v1/brands/${id}`, {
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

// Function to delete attachment
const deleteAttachment = async (saleItemId, imageViewOrder) => {
  try {
    const response = await fetch(`${URL}/itb-mshop/v2/sale-items/${saleItemId}/attachments/by-order?imageViewOrder=${imageViewOrder}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Failed to delete attachment");
    }
    return true;
  } catch (error) {
    console.error("Delete attachment error:", error);
    throw handleApiError(error);
  }
};

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
  deleteAttachment,
};
