import { handleApiError } from "../api/client";

// Use BASE_URL from Vite config (will be "/or4/" in production, "/" in development)
// This ensures API calls use the correct base path
const URL = import.meta.env.BASE_URL || "";

export async function registerUser(form) {
  try {
    const fd = new FormData();
    fd.append("nickName", form.nickName);
    fd.append("email", form.email);
    fd.append("fullName", form.fullName);
    fd.append("password", form.password);
    fd.append("phoneNumber", form.phoneNumber || "");
    fd.append("bankAccount", form.bankAccount || "");
    fd.append("bankName", form.bankName || "");
    fd.append("idCardNumber", form.idCardNumber || "");
    fd.append("userType", form.userType);

    if (form.idCardImageFront) fd.append("idCardImageFront", form.idCardImageFront);
    if (form.idCardImageBack) fd.append("idCardImageBack", form.idCardImageBack);

    const res = await fetch(`${URL}/itb-mshop/v2/users/register`, {
      method: "POST",
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      },
      body: fd,
    });

    if (!res.ok) {
      const errorData = await res.json().catch(() => ({ message: 'Registration failed' }));
      const error = new Error(errorData.message || `Register failed (${res.status})`);
      error.status = res.status; // Attach status code
      throw error;
    }
    return await res.json();
  } catch (err) {
    if (err.status) {
      throw err; // Re-throw if it already has status
    }
    throw handleApiError(err);
  }
}

export async function verifyEmail(token) {
  try {
    const res = await fetch(`${URL}/itb-mshop/v2/auth/verify-email`, {
      method: "POST",
      headers: { 
        "Content-Type": "application/json",
        'X-Requested-With': 'XMLHttpRequest'
      },
      body: JSON.stringify({ token }),
    });

    const text = await res.text();
    if (!res.ok) throw new Error(text || `Verify failed (${res.status})`);
    return text;
  } catch (err) {
    throw handleApiError(err);
  }
}
