export default function LoadingBar({ active, message = 'Loading…', blocking = false }) {
  return (
    <>
      <div
        className={`app-loading-bar${active ? ' app-loading-bar--active' : ''}`}
        role="progressbar"
        aria-hidden={!active}
        aria-label={message}
      >
        <span className="app-loading-bar__track">
          <span className="app-loading-bar__fill" />
        </span>
      </div>

      {blocking && active && (
        <div className="app-loading-overlay" aria-live="polite" aria-busy="true">
          <div className="app-loading-overlay__content">
            <div className="app-loading-spinner" />
            <p>{message}</p>
          </div>
        </div>
      )}
    </>
  );
}
