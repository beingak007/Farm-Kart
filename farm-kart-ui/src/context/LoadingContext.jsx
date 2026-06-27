import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { subscribeLoading } from '../api/loadingTracker';
import LoadingBar from '../components/LoadingBar';
import '../styles/loading.css';

const LoadingContext = createContext(null);

export function LoadingProvider({ children }) {
  const [apiLoading, setApiLoading] = useState(false);
  const [manualCount, setManualCount] = useState(0);
  const [booting, setBooting] = useState(true);
  const [message, setMessage] = useState('Loading application…');

  useEffect(() => subscribeLoading(setApiLoading), []);

  useEffect(() => {
    const timer = setTimeout(() => setBooting(false), 700);
    return () => clearTimeout(timer);
  }, []);

  const showLoading = useCallback((msg = 'Loading…') => {
    setMessage(msg);
    setManualCount((c) => c + 1);
  }, []);

  const hideLoading = useCallback(() => {
    setManualCount((c) => Math.max(0, c - 1));
  }, []);

  const withLoading = useCallback(
    async (promiseOrFn, msg = 'Loading…') => {
      showLoading(msg);
      try {
        return typeof promiseOrFn === 'function' ? await promiseOrFn() : await promiseOrFn;
      } finally {
        hideLoading();
      }
    },
    [showLoading, hideLoading]
  );

  const isLoading = booting || apiLoading || manualCount > 0;
  const displayMessage = booting ? 'Loading application…' : message;

  const value = useMemo(
    () => ({ isLoading, showLoading, hideLoading, withLoading }),
    [isLoading, showLoading, hideLoading, withLoading]
  );

  return (
    <LoadingContext.Provider value={value}>
      <LoadingBar active={isLoading} message={displayMessage} blocking={booting} />
      {children}
    </LoadingContext.Provider>
  );
}

export function useLoading() {
  const ctx = useContext(LoadingContext);
  if (!ctx) throw new Error('useLoading must be used within LoadingProvider');
  return ctx;
}
