import { PublicClientApplication } from '@azure/msal-browser';

const msClientId = import.meta.env.VITE_MS_CLIENT_ID || '';

export const msalConfigured = Boolean(msClientId);

export const msalInstance = new PublicClientApplication({
  auth: {
    clientId: msClientId || '00000000-0000-0000-0000-000000000000',
    authority: 'https://login.microsoftonline.com/common',
    redirectUri: typeof window !== 'undefined' ? window.location.origin : 'http://localhost:5173',
  },
  cache: {
    cacheLocation: 'sessionStorage',
  },
});

export async function signInWithMicrosoft() {
  if (!msalConfigured) {
    throw new Error('Microsoft sign-in is not configured. Set VITE_MS_CLIENT_ID in .env');
  }
  await msalInstance.initialize();
  const result = await msalInstance.loginPopup({
    scopes: ['User.Read', 'openid', 'profile', 'email'],
  });
  if (!result.accessToken) {
    throw new Error('Microsoft sign-in did not return an access token');
  }
  return result.accessToken;
}
