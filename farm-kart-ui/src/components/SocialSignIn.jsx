import { useRef } from 'react';
import { GoogleLogin } from '@react-oauth/google';
import { oauthLogin } from '../api/client';
import { signInWithMicrosoft, msalConfigured } from '../auth/msal';

function GoogleIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
      <path fill="#EA4335" d="M12 10.2v3.6h5.1c-.2 1.2-1.6 3.5-5.1 3.5-3.1 0-5.6-2.5-5.6-5.6S8.9 6.1 12 6.1c1.8 0 3 .8 3.7 1.5l2.5-2.4C16.8 3.7 14.6 2.7 12 2.7 6.9 2.7 2.7 6.9 2.7 12S6.9 21.3 12 21.3c6.9 0 8.6-4.8 8.6-7.2 0-.5 0-.9-.1-1.2H12z" />
      <path fill="#34A853" d="M3.9 14.5l3 2.2c.8 1.5 2.4 2.5 4.2 2.5 2.5 0 4.1-1.9 4.1-4.1h-4.1V10h7.1c.1.5.1 1 .1 1.6 0 5.5-3.7 9.4-9.2 9.4-5.3 0-9.6-4.3-9.6-9.6S6.7 2.7 12 2.7c2.7 0 4.5 1.1 5.5 2l2.5-2.4C16.8 1.2 14.5 0 12 0 5.4 0 0 5.4 0 12s5.4 12 12 12c6.8 0 11.3-4.8 11.3-11.5 0-.8-.1-1.4-.2-2H12v4.5z" />
      <path fill="#FBBC05" d="M3.9 14.5 1.8 16.5A11.9 11.9 0 0 1 0 12c0-1.9.5-3.7 1.3-5.3l3.6 2.8c.3 1.5 1.2 2.8 2.5 3.7z" />
      <path fill="#4285F4" d="M12 23.3c3.2 0 5.9-1 7.9-2.8l-3.7-3c-1 .7-2.3 1.1-4.2 1.1-3.2 0-5.9-2.1-6.9-5.1L1.3 16.5A12 12 0 0 0 12 23.3z" />
    </svg>
  );
}

function MicrosoftIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
      <rect x="2" y="2" width="9" height="9" fill="#F25022" />
      <rect x="13" y="2" width="9" height="9" fill="#7FBA00" />
      <rect x="2" y="13" width="9" height="9" fill="#00A4EF" />
      <rect x="13" y="13" width="9" height="9" fill="#FFB900" />
    </svg>
  );
}

function MobileIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <rect x="6" y="2" width="12" height="20" rx="2" />
      <line x1="10" y1="18" x2="14" y2="18" />
    </svg>
  );
}

export default function SocialSignIn({
  googleConfigured,
  loading,
  onLoading,
  onSuccess,
  onError,
  onMobileClick,
  onPasswordClick,
}) {
  const googleWrapRef = useRef(null);

  const handleGoogleSuccess = async (credentialResponse) => {
    onLoading(true);
    onError('');
    try {
      const data = await oauthLogin('GOOGLE', { idToken: credentialResponse.credential });
      onSuccess(data);
    } catch (err) {
      onError(err.message);
    } finally {
      onLoading(false);
    }
  };

  const handleMicrosoft = async () => {
    onLoading(true);
    onError('');
    try {
      const accessToken = await signInWithMicrosoft();
      const data = await oauthLogin('MICROSOFT', { accessToken });
      onSuccess(data);
    } catch (err) {
      onError(err.message);
    } finally {
      onLoading(false);
    }
  };

  const triggerGoogle = () => {
    if (!googleConfigured) {
      onError('Google sign-in is not configured. Set VITE_GOOGLE_CLIENT_ID in .env');
      return;
    }
    const googleBtn = googleWrapRef.current?.querySelector('div[role="button"]');
    googleBtn?.click();
  };

  return (
    <div className="social-signin">
      <div className="social-signin-hidden" ref={googleWrapRef} aria-hidden="true">
        {googleConfigured && (
          <GoogleLogin
            onSuccess={handleGoogleSuccess}
            onError={() => onError('Google sign-in was cancelled or failed')}
            useOneTap={false}
            theme="outline"
            size="large"
            width="360"
          />
        )}
      </div>

      <button
        type="button"
        className="btn-social btn-google"
        onClick={triggerGoogle}
        disabled={loading}
      >
        <GoogleIcon />
        Continue with Google
      </button>

      <button
        type="button"
        className="btn-social btn-microsoft"
        onClick={handleMicrosoft}
        disabled={loading || !msalConfigured}
        title={!msalConfigured ? 'Set VITE_MS_CLIENT_ID in .env' : undefined}
      >
        <MicrosoftIcon />
        Continue with Microsoft
      </button>

      <div className="social-divider">
        <span>or</span>
      </div>

      <button
        type="button"
        className="btn-social btn-mobile"
        onClick={onMobileClick}
        disabled={loading}
      >
        <MobileIcon />
        Continue with Mobile Number
      </button>

      <p className="login-existing">
        Already have an account?{' '}
        <button type="button" className="link-btn" onClick={onPasswordClick} disabled={loading}>
          Sign in with username &amp; password
        </button>
      </p>
    </div>
  );
}
