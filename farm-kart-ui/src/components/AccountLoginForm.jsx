import { useState } from 'react';
import { login, forgotPassword, resetPassword } from '../api/client';
import {
  loadRememberedUsername,
  loadRememberEnabled,
  saveRememberUsername,
} from '../auth/rememberUsername';

export default function AccountLoginForm({
  mode,
  loading,
  onLoading,
  onSuccess,
  onError,
  onForgotClick,
  onBack,
  formFieldsRef,
}) {
  const [username, setUsername] = useState(loadRememberedUsername);
  const [password, setPassword] = useState('');
  const [remember, setRemember] = useState(loadRememberEnabled);
  const [resetSent, setResetSent] = useState(false);
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    onLoading(true);
    onError('');
    setSuccessMsg('');
    try {
      const data = await login(username.trim(), password);
      saveRememberUsername(username.trim(), remember);
      onSuccess(data);
    } catch (err) {
      onError(err.message);
    } finally {
      onLoading(false);
    }
  };

  const handleForgotSubmit = async (e) => {
    e.preventDefault();
    onLoading(true);
    onError('');
    setSuccessMsg('');
    try {
      const result = await forgotPassword(username.trim());
      setResetSent(true);
      setSuccessMsg(result.message || 'Reset code sent if account exists.');
    } catch (err) {
      onError(err.message);
    } finally {
      onLoading(false);
    }
  };

  const handleResetSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      onError('Passwords do not match');
      return;
    }
    onLoading(true);
    onError('');
    setSuccessMsg('');
    try {
      const result = await resetPassword(username.trim(), otp, newPassword);
      setSuccessMsg(result.message || 'Password updated.');
      setTimeout(() => onBack('password'), 1200);
    } catch (err) {
      onError(err.message);
    } finally {
      onLoading(false);
    }
  };

  if (mode === 'forgot') {
    return (
      <form className="login-form" onSubmit={resetSent ? handleResetSubmit : handleForgotSubmit}>
        <button type="button" className="mobile-back" onClick={() => onBack('password')}>
          ← Back to sign in
        </button>

        {successMsg && !resetSent && <div className="success-banner">{successMsg}</div>}

        <div className="form-group" ref={(el) => (formFieldsRef.current[0] = el)}>
          <label htmlFor="reset-username">Username or email</label>
          <input
            id="reset-username"
            type="text"
            placeholder="farmer@example.com"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            disabled={resetSent}
            autoComplete="username"
          />
        </div>

        {resetSent && (
          <>
            <div className="form-group" ref={(el) => (formFieldsRef.current[1] = el)}>
              <label htmlFor="reset-otp">Reset code</label>
              <input
                id="reset-otp"
                type="text"
                inputMode="numeric"
                placeholder="6-digit code"
                maxLength={6}
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                required
              />
              <p className="login-hint">Check backend logs for reset code in dev mode</p>
            </div>
            <div className="form-group" ref={(el) => (formFieldsRef.current[2] = el)}>
              <label htmlFor="new-password">New password</label>
              <input
                id="new-password"
                type="password"
                placeholder="At least 6 characters"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
                minLength={6}
                autoComplete="new-password"
              />
            </div>
            <div className="form-group" ref={(el) => (formFieldsRef.current[3] = el)}>
              <label htmlFor="confirm-password">Confirm password</label>
              <input
                id="confirm-password"
                type="password"
                placeholder="Re-enter password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                minLength={6}
                autoComplete="new-password"
              />
            </div>
          </>
        )}

        {successMsg && resetSent && <div className="success-banner">{successMsg}</div>}

        <div ref={(el) => (formFieldsRef.current[resetSent ? 4 : 1] = el)}>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading
              ? 'Please wait…'
              : resetSent
                ? 'Update password'
                : 'Send reset code'}
          </button>
        </div>
      </form>
    );
  }

  return (
    <form className="login-form" onSubmit={handleLogin}>
      <button type="button" className="mobile-back" onClick={() => onBack('social')}>
        ← Back to sign-in options
      </button>

      <div className="form-group" ref={(el) => (formFieldsRef.current[0] = el)}>
        <label htmlFor="username">Username or email</label>
        <input
          id="username"
          type="text"
          placeholder="farmer@example.com"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          autoComplete="username"
        />
      </div>

      <div className="form-group" ref={(el) => (formFieldsRef.current[1] = el)}>
        <label htmlFor="password">Password</label>
        <input
          id="password"
          type="password"
          placeholder="••••••••"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          autoComplete="current-password"
        />
      </div>

      <div className="login-form-row" ref={(el) => (formFieldsRef.current[2] = el)}>
        <label className="remember-label">
          <input
            type="checkbox"
            checked={remember}
            onChange={(e) => setRemember(e.target.checked)}
          />
          Remember username
        </label>
        <button type="button" className="link-btn" onClick={onForgotClick}>
          Forgot password?
        </button>
      </div>

      <div ref={(el) => (formFieldsRef.current[3] = el)}>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Signing in…' : 'Sign in'}
        </button>
      </div>
    </form>
  );
}
