import { API_BASE_URL } from './client.js';

export async function loginUser(email, password) {
  try {
    const response = await fetch(`${API_BASE_URL}/itb-mshop/v2/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      },
      body: JSON.stringify({ email, password }),
      credentials: 'omit'
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.message || 'Login failed');
    }

    return data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
}
