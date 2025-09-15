const STORAGE_KEY = 'jwtToken';

export function saveJwtToken(token){
  sessionStorage.setItem(STORAGE_KEY, token);
}

export function clearJwtToken(){
  sessionStorage.removeItem(STORAGE_KEY);
}

export function getAuthHeader(){
  // ใช้ JWT token
  const token = sessionStorage.getItem(STORAGE_KEY);
  if (token) {
    return { Authorization: `Bearer ${token}` };
  }
  
  return {};
}


