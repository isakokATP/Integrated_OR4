import { handleApiError } from "../api/client";

// ใช้ path proxy ของ nginx แทน port 8080
const URL = "ip24or4.sit.kmutt.ac.th/itb-mshop";

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

    const res = await fetch(`${URL}/v2/users/register`, {
      method: "POST",
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      },
      body: fd,
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || `Register failed (${res.status})`);
    }
    return await res.json();
  } catch (err) {
    throw handleApiError(err);
  }
}

export async function verifyEmail(token) {
  try {
    const res = await fetch(`${URL}/v2/auth/verify-email`, {
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
