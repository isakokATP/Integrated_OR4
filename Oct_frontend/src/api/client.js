// Use BASE_URL from Vite config (will be "/or4/" in production, "/" in development)
// This ensures API calls use the correct base path
export const apiUrl = import.meta.env.BASE_URL;

// --- Auth helpers ---
const ACCESS_TOKEN_KEY = "accessToken";

export function getStoredAccessToken() {
  return sessionStorage.getItem(ACCESS_TOKEN_KEY);
}

export function setStoredAccessToken(token) {
  if (!token) return;
  sessionStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export function clearStoredAccessToken() {
  sessionStorage.removeItem(ACCESS_TOKEN_KEY);
}

function redirectToLogin(message) {
  if (message) {
    // ใช้ alert แบบง่าย ๆ ให้ตรง requirement ข้อ error message
    alert(message);
  }
  // ใช้ path /login ซึ่งแม็ปกับหน้า login page
  window.location.href = `${import.meta.env.BASE_URL}login`;
}

// เรียก /v2/auth/refresh เพื่อขอ access token ใหม่ โดยใช้ HttpOnly refresh cookie
async function refreshAccessToken() {
  const refreshUrl = `${apiUrl}/itb-mshop/v2/auth/refresh`;

  try {
    const response = await fetch(refreshUrl, {
      method: "POST",
      credentials: "include", // ต้องส่ง cookie ไปด้วย
    });

    if (response.status === 200) {
      const data = await response.json();
      if (data.accessToken) {
        setStoredAccessToken(data.accessToken);
      }
      return { ok: true, status: 200, accessToken: data.accessToken };
    }

    // แปลง error body เป็นข้อความ (ถ้ามี)
    let errorMessage = "";
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || "";
    } catch (_) {
      // ignore parse error
    }

    switch (response.status) {
      case 400:
        // ไม่มี refresh token
        clearStoredAccessToken();
        redirectToLogin("Your session has expired. Please sign in again.");
        break;
      case 401:
        // refresh token invalid / expired
        clearStoredAccessToken();
        redirectToLogin("Your session is invalid or has expired. Please sign in again.");
        break;
      case 403:
        // user ยังไม่ activate
        clearStoredAccessToken();
        redirectToLogin(
          errorMessage || "Your account is not activated. Please check your email and sign in again."
        );
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
  const url = `${apiUrl}${endpoint}`;
  console.log("--- API REQUEST LOG ---");
  console.log("URL:", url);

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
    console.warn("Received 401/403. Trying to refresh access token...");

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

  console.error("--- API ERROR LOG ---");
  console.error("URL:", apiError.url);
  console.error("Status:", apiError.status);
  console.error("Message:", apiError.message);
  console.error("Full Error Object:", apiError);
  console.error("----------------------");

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
