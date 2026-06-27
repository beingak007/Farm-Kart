import { trackLoading } from './loadingTracker';

const API_BASE = '/farm-kart/api/v1';

async function request(path, options = {}) {
  trackLoading(true);
  try {
    const response = await fetch(`${API_BASE}${path}`, options);
    const json = await response.json();
    if (!response.ok || !json.success) {
      throw new Error(json.message || 'Request failed');
    }
    return json.data;
  } finally {
    trackLoading(false);
  }
}

async function fetchWithLoading(url, options = {}) {
  trackLoading(true);
  try {
    const response = await fetch(url, options);
    const json = await response.json();
    if (!response.ok || !json.success) {
      throw new Error(json.message || 'Request failed');
    }
    return json.data;
  } finally {
    trackLoading(false);
  }
}

export async function login(username, password) {
  return request('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
}

export async function forgotPassword(username) {
  return request('/auth/forgot-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username }),
  });
}

export async function resetPassword(username, otp, newPassword) {
  return request('/auth/reset-password', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, otp, newPassword }),
  });
}

export async function oauthLogin(provider, { idToken, accessToken }) {
  return request('/auth/oauth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ provider, idToken, accessToken }),
  });
}

export async function verifyOtp(mobile, otp) {
  return request('/auth/verify-otp', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ mobile, otp }),
  });
}

export async function sendOtp(mobile) {
  return request('/auth/send-otp', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ mobile }),
  });
}

export async function uploadSheet(userId, file, token) {
  const formData = new FormData();
  formData.append('file', file);

  return fetchWithLoading(`${API_BASE}/sheets/upload`, {
    method: 'POST',
    headers: {
      'X-User-Id': String(userId),
      Authorization: token ? `Bearer ${token}` : '',
    },
    body: formData,
  });
}

export async function listSheets(userId, token) {
  return fetchWithLoading(`${API_BASE}/sheets`, {
    headers: {
      'X-User-Id': String(userId),
      Authorization: token ? `Bearer ${token}` : '',
    },
  });
}

export async function getSheetStatus(uploadId, token) {
  return fetchWithLoading(`${API_BASE}/sheets/${uploadId}/status`, {
    headers: { Authorization: token ? `Bearer ${token}` : '' },
  });
}

/**
 * Upload an image or video to S3 via the backend.
 * @param {string} contextId  - e.g. product ID
 * @param {string} mediaType  - PRODUCT_IMAGE | PRODUCT_VIDEO | KYC_DOCUMENT
 */
export async function uploadMedia(userId, contextId, mediaType, file, token) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('contextId', String(contextId));
  formData.append('mediaType', mediaType);

  return fetchWithLoading(`${API_BASE}/media/upload`, {
    method: 'POST',
    headers: {
      'X-User-Id': String(userId),
      Authorization: token ? `Bearer ${token}` : '',
    },
    body: formData,
  });
}
