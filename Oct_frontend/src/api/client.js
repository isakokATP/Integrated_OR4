// Use BASE_URL from Vite config (will be "/or4/" in production, "/" in development)
// This ensures API calls use the correct base path
export const apiUrl = import.meta.env.BASE_URL;

// --- Auth helpers ---
const ACCESS_TOKEN_KEY = "accessToken";

export function getStoredAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function setStoredAccessToken(token) {
  if (!token) return;
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export function clearStoredAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}


function redirectToLogin(message) {
  // Store current userId to check if the same user logs in again
  const currentToken = getStoredAccessToken();
  if (currentToken) {
    try {
      const payload = JSON.parse(atob(currentToken.split(".")[1]));
      if (payload.id) {
        localStorage.setItem("redirectUserId", payload.id);
      }
    } catch (e) {
      // Ignore decoding errors
    }
  }

  clearStoredAccessToken(); // Safety: always clear token before redirecting to login

  // Store current path to redirect back after login (if not already on login page)
  const currentPath = window.location.pathname + window.location.search;
  if (!currentPath.includes("login")) {
    localStorage.setItem("redirectPath", currentPath);
  }

  // No alert as requested
  window.location.href = `${import.meta.env.BASE_URL}login`;
}

// เรียก /v2/auth/refresh เพื่อขอ access token ใหม่ โดยใช้ HttpOnly refresh cookie
async function refreshAccessToken() {
  const refreshUrl = `${apiUrl}itb-mshop/v2/auth/refresh`;

  try {
    const response = await fetch(refreshUrl, {
      method: "POST",
      credentials: "include", // ต้องส่ง cookie ไปด้วย
    });

    switch (response.status) {
      case 200:
        const data = await response.json();
        if (data.accessToken) {
          setStoredAccessToken(data.accessToken);
          return { ok: true, accessToken: data.accessToken };
        }
        break;
      case 400:
        // ไม่มี refresh token
        redirectToLogin(null);
        break;
      case 401:
        // refresh token invalid / expired
        redirectToLogin(null);
        break;
      case 403:
        // user ยังไม่ activate
        // Fixed: errorMessage was not defined, using default message or generic
        redirectToLogin(null);
        break;
      default:
        // กรณีอื่น ๆ
        alert("There is a problem. Please try again later.");
        break;
    }

    return { ok: false, status: response.status };
  } catch (err) {
    console.error("Error refreshing access token:", err);
    alert("There is a problem. Please try again later.");
    return { ok: false, status: 0 };
  }
}

// if (!isLocalhost && !window.location.origin.startsWith("https")) {
//   throw new Error("Invalid production API URL");
// }

// Helper to inject auth header
function getAuthHeaders(options = {}) {
  const headers = new Headers(options.headers || {});
  const token = getStoredAccessToken();
  if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  return headers;
}

async function apiCall(endpoint, options = {}, isRetry = false) {
  // Fix: Prevent double slashes if apiUrl ends with / and endpoint starts with /
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint.slice(1) : endpoint;
  const url = `${apiUrl}${cleanEndpoint}`;


  const defaultOptions = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const requestOptions = { ...defaultOptions, ...options };

  // Inject Auth header
  const authHeaders = getAuthHeaders(requestOptions);
  requestOptions.headers = authHeaders;

  const response = await fetch(url, requestOptions);

  // ถ้า token หมดอายุ / invalid ให้ลอง refresh ตาม requirement
  if (!response.ok && (response.status === 401 || response.status === 403) && !isRetry) {


    const refreshResult = await refreshAccessToken();
    if (refreshResult.ok && refreshResult.accessToken) {

      // อัปเดต Authorization header ใน request เดิม (ถ้ามี)
      const newHeaders = new Headers(requestOptions.headers || {});
      newHeaders.set("Authorization", `Bearer ${refreshResult.accessToken}`);

      const retryOptions = {
        ...requestOptions,
        headers: newHeaders,
      };

      const retryResponse = await fetch(url, retryOptions);
      if (!retryResponse.ok) {
        // ถ้า retry แล้วยัง error ก็ปล่อยให้ไป handle ด้านล่าง
        return handleErrorResponse(retryResponse);
      }

      return parseResponse(retryResponse);
    }

    // ถ้า refresh ไม่ผ่าน redirectToLogin จะถูกเรียกใน refreshAccessToken แล้ว
    // และให้ throw error ต่อไป
  }

  if (!response.ok) {
    return handleErrorResponse(response);
  }

  return parseResponse(response);
}

async function handleErrorResponse(response) {
  let errorMessage = `HTTP Error: ${response.status} ${response.statusText}`;
  try {
    const clonedResponse = response.clone();
    const errorData = await clonedResponse.json();
    errorMessage = errorData.message || errorData.error || errorMessage;
  } catch (_) {
    // Ignore parsing error
  }

  const apiError = {
    isApiError: true,
    status: response.status,
    message: errorMessage,
    url: response.url,
    response: response,
  };



  throw apiError;
}

async function parseResponse(response) {
  const contentType = response.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    return await response.json();
  }

  return response;
}

export const api = {
  get: (endpoint, options = {}) =>
    apiCall(endpoint, { ...options, method: "GET" }),
  post: (endpoint, data, options = {}) =>
    apiCall(endpoint, {
      ...options,
      method: "POST",
      body: JSON.stringify(data),
    }),
  put: (endpoint, data, options = {}) =>
    apiCall(endpoint, {
      ...options,
      method: "PUT",
      body: JSON.stringify(data),
    }),
  delete: (endpoint, options = {}) =>
    apiCall(endpoint, { ...options, method: "DELETE" }),
  request: (endpoint, options = {}) =>
    apiCall(endpoint, options),
};

export function handleApiError(error, defaultMessage = "not connect server") {
  switch (error.status) {
    case 400:
      return "400";
    case 401:
      return "401";
    case 403:
      return "403";
    case 404:
      return "404";
    case 500:
      return "500";
    default:
      if (error.isNetworkError) {
        return "not connect server";
      }
      return error.message || defaultMessage;
  }
}
