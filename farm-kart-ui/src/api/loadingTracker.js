let activeCount = 0;
const listeners = new Set();

function notify() {
  listeners.forEach((fn) => fn(activeCount > 0));
}

export function subscribeLoading(listener) {
  listeners.add(listener);
  listener(activeCount > 0);
  return () => listeners.delete(listener);
}

export function trackLoading(start) {
  activeCount = Math.max(0, activeCount + (start ? 1 : -1));
  notify();
}

export function resetLoading() {
  activeCount = 0;
  notify();
}
