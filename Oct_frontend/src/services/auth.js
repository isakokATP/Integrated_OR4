const STORAGE_KEY = 'basicAuth';

export function saveBasicAuth(email, password){
  const token = btoa(`${email}:${password}`);
  sessionStorage.setItem(STORAGE_KEY, token);
}

export function clearBasicAuth(){
  sessionStorage.removeItem(STORAGE_KEY);
}

export function getAuthHeader(){
  const token = sessionStorage.getItem(STORAGE_KEY);
  return token ? { Authorization: `Basic ${token}` } : {};
}


