const isLocalhost =
  window.location.hostname === "localhost" ||
  window.location.hostname === "127.0.0.1" ||
  window.location.hostname === "::1";

export const apiUrl = isLocalhost
  ? import.meta.env.VITE_API_URL_LOCAL
  : import.meta.env.VITE_API_URL_PROD;

if (!isLocalhost && !window.location.origin.startsWith("https")) {
  throw new Error("Invalid production API URL");
}

async function apiCall(endpoint, options = {}) {
  const url = `${apiUrl}${endpoint}`;
  console.log("--- API REQUEST LOG ---");
  console.log("URL:", url);

  const defaultOptions = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const requestOptions = { ...defaultOptions, ...options };

  const response = await fetch(url, requestOptions);

  if (!response.ok) {
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
