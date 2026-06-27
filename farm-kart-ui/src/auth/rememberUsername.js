const REMEMBER_USERNAME_KEY = 'farmkart_remember_username';
const REMEMBER_ENABLED_KEY = 'farmkart_remember_enabled';

export function loadRememberedUsername() {
  if (localStorage.getItem(REMEMBER_ENABLED_KEY) === 'true') {
    return localStorage.getItem(REMEMBER_USERNAME_KEY) || '';
  }
  return '';
}

export function loadRememberEnabled() {
  return localStorage.getItem(REMEMBER_ENABLED_KEY) === 'true';
}

export function saveRememberUsername(username, remember) {
  if (remember && username) {
    localStorage.setItem(REMEMBER_USERNAME_KEY, username);
    localStorage.setItem(REMEMBER_ENABLED_KEY, 'true');
  } else {
    localStorage.removeItem(REMEMBER_USERNAME_KEY);
    localStorage.setItem(REMEMBER_ENABLED_KEY, 'false');
  }
}
