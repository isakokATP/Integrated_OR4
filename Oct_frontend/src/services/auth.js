const STORAGE_KEY = 'userCredentials';

export function saveUserCredentials(email, password){
  const credentials = { email, password };
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(credentials));
}

export function clearUserCredentials(){
  sessionStorage.removeItem(STORAGE_KEY);
}

export function getUserCredentials(){
  const credentials = sessionStorage.getItem(STORAGE_KEY);
  return credentials ? JSON.parse(credentials) : null;
}

export function getAuthHeader(){
  // ใช้ default Spring Security user สำหรับ API calls
  // Username: user, Password: eea9b939-ada6-4f2e-9beb-a85546a4f614
  const defaultToken = btoa('user:eea9b939-ada6-4f2e-9beb-a85546a4f614');
  return { Authorization: `Basic ${defaultToken}` };
}


