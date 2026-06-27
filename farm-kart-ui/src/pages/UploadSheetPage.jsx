import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import gsap from 'gsap';
import { listSheets, uploadSheet, uploadMedia } from '../api/client';
import { useAuth } from '../context/AuthContext';
import '../styles/upload.css';

const SHEET_ACCEPT = '.csv,.xlsx,.xls';
const MEDIA_ACCEPT = 'image/jpeg,image/png,image/webp,image/gif,video/mp4,video/quicktime,video/webm';

const STATUS_META = {
  QUEUED:     { label: 'Queued',     color: '#3b82f6', bg: '#eff6ff' },
  PROCESSING: { label: 'Processing', color: '#d97706', bg: '#fffbeb' },
  PROCESSED:  { label: 'Processed',  color: '#16a34a', bg: '#f0fdf4' },
  FAILED:     { label: 'Failed',     color: '#dc2626', bg: '#fef2f2' },
  UPLOADED:   { label: 'Uploaded',   color: '#7c3aed', bg: '#f5f3ff' },
};

function formatSize(bytes) {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

function StatusBadge({ status }) {
  const meta = STATUS_META[status] || { label: status, color: '#6b7280', bg: '#f3f4f6' };
  return (
    <span className="status-badge" style={{ color: meta.color, background: meta.bg }}>
      {meta.label === 'Processing' && <span className="spinner" />}
      {meta.label}
    </span>
  );
}

export default function UploadSheetPage() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();
  const fileInputRef = useRef(null);
  const mediaInputRef = useRef(null);
  const dropzoneRef = useRef(null);
  const mediaDropRef = useRef(null);
  const mainRef = useRef(null);

  const [tab, setTab] = useState('sheets'); // 'sheets' | 'media'

  // Sheet state
  const [dragging, setDragging] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [sheetMsg, setSheetMsg] = useState('');
  const [sheetErr, setSheetErr] = useState('');
  const [uploads, setUploads] = useState([]);

  // Media state
  const [mediaDragging, setMediaDragging] = useState(false);
  const [mediaUploading, setMediaUploading] = useState(false);
  const [mediaMsg, setMediaMsg] = useState('');
  const [mediaErr, setMediaErr] = useState('');
  const [mediaContextId, setMediaContextId] = useState('');
  const [mediaType, setMediaType] = useState('PRODUCT_IMAGE');
  const [mediaItems, setMediaItems] = useState([]);  // { name, url, category, size }
  const [mediaPreview, setMediaPreview] = useState(null);  // { url, category }

  const loadUploads = useCallback(async () => {
    if (!auth?.userId) return;
    try {
      const data = await listSheets(auth.userId, auth.accessToken);
      setUploads(data);
    } catch { /* silent on first load */ }
  }, [auth]);

  // Entry animations
  useEffect(() => {
    const ctx = gsap.context(() => {
      gsap.from('.upload-header',  { y: -30, opacity: 0, duration: 0.6, ease: 'power2.out' });
      gsap.from('.upload-title',   { y: 30,  opacity: 0, duration: 0.7, delay: 0.15, ease: 'power3.out' });
      gsap.from('.upload-tabs',    { y: 20,  opacity: 0, duration: 0.6, delay: 0.25 });
    }, mainRef);
    loadUploads();
    return () => ctx.revert();
  }, [loadUploads]);

  // Real-time polling: refresh every 3 s while any upload is QUEUED or PROCESSING
  useEffect(() => {
    const hasPending = uploads.some(u => u.status === 'QUEUED' || u.status === 'PROCESSING');
    if (!hasPending) return;
    const timer = setTimeout(() => loadUploads(), 3000);
    return () => clearTimeout(timer);
  }, [uploads, loadUploads]);

  // ── Sheet upload ──────────────────────────────────────────────────────────

  const handleSheetFile = async (file) => {
    if (!file) return;
    setSheetErr(''); setSheetMsg('');
    setUploading(true);
    try {
      const result = await uploadSheet(auth.userId, file, auth.accessToken);
      setSheetMsg('Sheet uploaded — Kafka will process it shortly.');
      gsap.fromTo(dropzoneRef.current,
        { scale: 1 }, { scale: 1.03, duration: 0.15, yoyo: true, repeat: 1 });
      await loadUploads();
    } catch (err) {
      setSheetErr(err.message);
    } finally {
      setUploading(false);
    }
  };

  const onSheetDrop = (e) => {
    e.preventDefault(); setDragging(false);
    handleSheetFile(e.dataTransfer.files[0]);
  };

  // ── Media upload ──────────────────────────────────────────────────────────

  const handleMediaFile = async (file) => {
    if (!file) return;
    setMediaErr(''); setMediaMsg('');
    const objectUrl = URL.createObjectURL(file);
    const isVideo = file.type.startsWith('video/');
    setMediaPreview({ url: objectUrl, category: isVideo ? 'VIDEO' : 'IMAGE', name: file.name });

    setMediaUploading(true);
    try {
      const ctxId = mediaContextId || String(auth.userId);
      const result = await uploadMedia(auth.userId, ctxId, mediaType, file, auth.accessToken);
      setMediaMsg(`Uploaded to S3!`);
      setMediaItems(prev => [result, ...prev]);
      gsap.fromTo(mediaDropRef.current,
        { scale: 1 }, { scale: 1.03, duration: 0.15, yoyo: true, repeat: 1 });
    } catch (err) {
      setMediaErr(err.message);
    } finally {
      setMediaUploading(false);
    }
  };

  const onMediaDrop = (e) => {
    e.preventDefault(); setMediaDragging(false);
    handleMediaFile(e.dataTransfer.files[0]);
  };

  const onLogout = () => { logout(); navigate('/login'); };

  return (
    <div className="upload-page" ref={mainRef}>
      <header className="upload-header">
        <div className="upload-brand">🌾 Farm Kart</div>
        <div className="upload-user">
          <span>User #{auth?.userId} · {auth?.role}</span>
          <button type="button" className="btn-ghost" onClick={onLogout}>Logout</button>
        </div>
      </header>

      <main className="upload-main">
        <h1 className="upload-title">Upload Centre</h1>
        <p className="upload-desc">
          Upload product sheets (processed via Kafka) or product images &amp; videos (stored to AWS S3).
        </p>

        {/* Tabs */}
        <div className="upload-tabs">
          <button
            type="button"
            className={`tab-btn ${tab === 'sheets' ? 'active' : ''}`}
            onClick={() => setTab('sheets')}>
            📄 Sheets
          </button>
          <button
            type="button"
            className={`tab-btn ${tab === 'media' ? 'active' : ''}`}
            onClick={() => setTab('media')}>
            🖼 Images &amp; Videos
          </button>
        </div>

        {/* ── Sheet Tab ─────────────────────────────────────────────── */}
        {tab === 'sheets' && (
          <>
            {sheetErr  && <div className="error-banner">{sheetErr}</div>}
            {sheetMsg  && <div className="success-banner">{sheetMsg}</div>}

            <div
              ref={dropzoneRef}
              className={`dropzone ${dragging ? 'dragging' : ''}`}
              onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
              onDragLeave={() => setDragging(false)}
              onDrop={onSheetDrop}
              onClick={() => fileInputRef.current?.click()}
              role="button" tabIndex={0}
              onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}>
              <div className="dropzone-icon">{uploading ? '⏳' : '📄'}</div>
              <h3>{uploading ? 'Uploading to S3…' : 'Drop your sheet here'}</h3>
              <p>or click to browse files</p>
              <p className="dropzone-types">CSV · XLS · XLSX — max 50 MB</p>
              <div className="kafka-badge">⚡ Processed via Confluent Kafka</div>
              <input ref={fileInputRef} type="file" accept={SHEET_ACCEPT} hidden
                onChange={(e) => handleSheetFile(e.target.files[0])} />
            </div>

            <section className="upload-list">
              <div className="list-header">
                <h3>Your uploads</h3>
                {uploads.some(u => u.status === 'QUEUED' || u.status === 'PROCESSING') && (
                  <span className="live-indicator">● Live</span>
                )}
              </div>

              {uploads.length === 0 ? (
                <div className="empty-state">No sheets uploaded yet. Drop a file above.</div>
              ) : (
                <table className="upload-table">
                  <thead>
                    <tr>
                      <th>File</th>
                      <th>Type</th>
                      <th>Size</th>
                      <th>Rows</th>
                      <th>Status</th>
                      <th>S3</th>
                    </tr>
                  </thead>
                  <tbody>
                    {uploads.map((u) => (
                      <tr key={u.id} className={u.status === 'PROCESSING' ? 'row-processing' : ''}>
                        <td>{u.originalName}</td>
                        <td>{u.fileType?.toUpperCase()}</td>
                        <td>{formatSize(u.fileSize)}</td>
                        <td>{u.rowCount ?? (u.status === 'PROCESSED' ? '—' : '…')}</td>
                        <td>
                          <StatusBadge status={u.status} />
                          {u.errorMessage && (
                            <span className="error-hint" title={u.errorMessage}>⚠</span>
                          )}
                        </td>
                        <td>
                          {u.s3Url
                            ? <a href={u.s3Url} target="_blank" rel="noreferrer" className="s3-link">↓ Download</a>
                            : <span className="muted">—</span>}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </section>
          </>
        )}

        {/* ── Media Tab ─────────────────────────────────────────────── */}
        {tab === 'media' && (
          <>
            <div className="media-controls">
              <div className="form-group">
                <label htmlFor="contextId">Product / Vendor ID</label>
                <input
                  id="contextId"
                  type="text"
                  className="form-input"
                  placeholder="e.g. 42"
                  value={mediaContextId}
                  onChange={(e) => setMediaContextId(e.target.value)}
                />
              </div>
              <div className="form-group">
                <label htmlFor="mediaType">Upload Type</label>
                <select
                  id="mediaType"
                  className="form-input"
                  value={mediaType}
                  onChange={(e) => setMediaType(e.target.value)}>
                  <option value="PRODUCT_IMAGE">Product Image</option>
                  <option value="PRODUCT_VIDEO">Product Video</option>
                  <option value="KYC_DOCUMENT">KYC Document</option>
                </select>
              </div>
            </div>

            {mediaErr && <div className="error-banner">{mediaErr}</div>}
            {mediaMsg && <div className="success-banner">{mediaMsg}</div>}

            <div
              ref={mediaDropRef}
              className={`dropzone ${mediaDragging ? 'dragging' : ''}`}
              onDragOver={(e) => { e.preventDefault(); setMediaDragging(true); }}
              onDragLeave={() => setMediaDragging(false)}
              onDrop={onMediaDrop}
              onClick={() => mediaInputRef.current?.click()}
              role="button" tabIndex={0}
              onKeyDown={(e) => e.key === 'Enter' && mediaInputRef.current?.click()}>
              <div className="dropzone-icon">{mediaUploading ? '⏳' : '🖼'}</div>
              <h3>{mediaUploading ? 'Uploading to S3…' : 'Drop image or video here'}</h3>
              <p>or click to browse</p>
              <p className="dropzone-types">JPG · PNG · WEBP · GIF · MP4 · MOV · WEBM</p>
              <div className="s3-badge">☁ Stored on AWS S3</div>
              <input ref={mediaInputRef} type="file" accept={MEDIA_ACCEPT} hidden
                onChange={(e) => handleMediaFile(e.target.files[0])} />
            </div>

            {/* Preview */}
            {mediaPreview && (
              <div className="media-preview">
                <h4>Preview — {mediaPreview.name}</h4>
                {mediaPreview.category === 'IMAGE' ? (
                  <img src={mediaPreview.url} alt="preview" className="preview-image" />
                ) : (
                  <video src={mediaPreview.url} controls className="preview-video" />
                )}
              </div>
            )}

            {/* Uploaded media gallery */}
            {mediaItems.length > 0 && (
              <section className="upload-list">
                <h3>Uploaded media this session</h3>
                <div className="media-gallery">
                  {mediaItems.map((item, idx) => (
                    <div key={idx} className="media-card">
                      {item.category === 'IMAGE' ? (
                        <img src={item.presignedUrl} alt={item.originalName} className="gallery-thumb" />
                      ) : item.category === 'VIDEO' ? (
                        <video src={item.presignedUrl} className="gallery-thumb" controls />
                      ) : (
                        <div className="gallery-doc">📄</div>
                      )}
                      <div className="media-card-info">
                        <span className="media-card-name">{item.originalName}</span>
                        <span className="media-card-size">{formatSize(item.fileSize)}</span>
                        <a href={item.presignedUrl} target="_blank" rel="noreferrer" className="s3-link">
                          ↗ S3 Link
                        </a>
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </>
        )}
      </main>
    </div>
  );
}
