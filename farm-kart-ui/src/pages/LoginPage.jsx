import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import gsap from 'gsap';
import { sendOtp, verifyOtp } from '../api/client';
import { useAuth } from '../context/AuthContext';
import FarmerVideoShowcase from '../components/FarmerVideoShowcase';
import SocialSignIn from '../components/SocialSignIn';
import AccountLoginForm from '../components/AccountLoginForm';
import '../styles/login.css';

const LOGO_TEXT = 'Farm Kart';
const googleConfigured = Boolean(import.meta.env.VITE_GOOGLE_CLIENT_ID);

export default function LoginPage() {
  const navigate = useNavigate();
  const { loginSuccess, isAuthenticated } = useAuth();
  const [mode, setMode] = useState('social');
  const [mobile, setMobile] = useState('');
  const [otp, setOtp] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const pageRef = useRef(null);
  const heroRef = useRef(null);
  const heroBgRef = useRef(null);
  const cardRef = useRef(null);
  const logoRef = useRef(null);
  const taglineRef = useRef(null);
  const featuresRef = useRef(null);
  const leavesRef = useRef([]);
  const formFieldsRef = useRef([]);
  const socialRef = useRef(null);

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/upload', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  useEffect(() => {
    const ctx = gsap.context(() => {
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } });

      tl.from(heroRef.current, { x: -100, opacity: 0, duration: 1.1 })
        .from(
          heroBgRef.current,
          { scale: 1.2, opacity: 0, duration: 1.4, ease: 'power2.out' },
          0
        );

      const chars = logoRef.current?.querySelectorAll('.logo-char');
      if (chars?.length) {
        tl.from(
          chars,
          {
            y: 60,
            opacity: 0,
            rotationX: -80,
            transformOrigin: '50% 100%',
            duration: 0.7,
            stagger: 0.045,
            ease: 'back.out(2)',
          },
          0.25
        );
      }

      tl.from(
        taglineRef.current,
        { y: 30, opacity: 0, duration: 0.75, ease: 'power2.out' },
        0.55
      ).from(
        featuresRef.current?.children || [],
        {
          x: -40,
          opacity: 0,
          duration: 0.55,
          stagger: 0.14,
          ease: 'power2.out',
        },
        0.7
      );

      tl.from(
        cardRef.current,
        {
          x: 80,
          opacity: 0,
          scale: 0.92,
          rotationY: -8,
          transformOrigin: '50% 50%',
          duration: 1,
          ease: 'power3.out',
        },
        0.2
      );

      leavesRef.current.forEach((leaf, i) => {
        gsap.set(leaf, { transformOrigin: '50% 50%' });
        gsap.to(leaf, {
          y: '+=28',
          x: i % 2 === 0 ? '+=14' : '-=10',
          rotation: i % 2 === 0 ? 15 : -12,
          duration: 3.5 + i * 0.5,
          repeat: -1,
          yoyo: true,
          ease: 'sine.inOut',
          delay: i * 0.3,
        });
      });

      gsap.to(heroBgRef.current, {
        backgroundPosition: '60px 60px',
        duration: 20,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
      });
    }, pageRef);

    return () => ctx.revert();
  }, []);

  useEffect(() => {
    const targets =
      mode === 'social'
        ? socialRef.current?.querySelectorAll('.btn-social, .login-existing')
        : formFieldsRef.current.filter(Boolean);

    if (targets?.length) {
      gsap.fromTo(
        targets,
        { y: 24, opacity: 0 },
        { y: 0, opacity: 1, duration: 0.45, stagger: 0.08, ease: 'power2.out' }
      );
    }
  }, [mode, otpSent]);

  const completeLogin = (data) => {
    loginSuccess(data);
    gsap.to(cardRef.current, {
      scale: 0.95,
      opacity: 0,
      y: -20,
      duration: 0.4,
      ease: 'power2.in',
      onComplete: () => navigate('/upload'),
    });
  };

  const shakeCard = () => {
    gsap.fromTo(
      cardRef.current,
      { x: 0 },
      { x: -10, duration: 0.07, repeat: 5, yoyo: true, ease: 'power1.inOut' }
    );
  };

  const switchMode = (next) => {
    const panel = cardRef.current?.querySelector('.login-panel-body');
    if (!panel) {
      setMode(next);
      setError('');
      setOtpSent(false);
      return;
    }
    gsap
      .timeline()
      .to(panel, { opacity: 0, y: 12, duration: 0.18, ease: 'power2.in' })
      .call(() => {
        setMode(next);
        setError('');
        setOtpSent(false);
      })
      .fromTo(panel, { opacity: 0, y: -12 }, { opacity: 1, y: 0, duration: 0.32, ease: 'power2.out' });
  };

  const handleSendOtp = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await sendOtp(mobile);
      setOtpSent(true);
    } catch (err) {
      setError(err.message);
      shakeCard();
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const data = await verifyOtp(mobile, otp);
      completeLogin(data);
    } catch (err) {
      setError(err.message);
      shakeCard();
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page" ref={pageRef}>
      <section className="login-hero" ref={heroRef}>
        <div className="login-hero-bg" ref={heroBgRef} />
        {['🌾', '🥬', '🍅'].map((icon, i) => (
          <span
            key={icon}
            className="floating-leaf"
            ref={(el) => (leavesRef.current[i] = el)}
            style={{
              top: ['15%', '72%', '42%'][i],
              left: ['8%', '20%', '48%'][i],
            }}
          >
            {icon}
          </span>
        ))}
        <div className="login-hero-inner">
          <div className="login-hero-content">
            <h1 className="login-logo" ref={logoRef} aria-label={LOGO_TEXT}>
              {LOGO_TEXT.split('').map((char, i) => (
                <span key={i} className="logo-char">
                  {char === ' ' ? '\u00A0' : char}
                </span>
              ))}
            </h1>
            <p className="login-tagline" ref={taglineRef}>
              Fresh produce marketplace for farmers and buyers. Upload your product sheets and grow your business.
            </p>
            <ul className="login-features" ref={featuresRef}>
              <li>Direct farm-to-table listings</li>
              <li>Bulk sheet upload for inventory</li>
              <li>Secure vendor onboarding</li>
            </ul>
          </div>
          <FarmerVideoShowcase />
        </div>
      </section>

      <section className="login-panel">
        <div className="login-card" ref={cardRef}>
          <h2>Welcome back</h2>
          <p className="login-subtitle">Sign in to upload your product sheets</p>

          {error && <div className="error-banner">{error}</div>}

          <div className="login-panel-body">
            {mode === 'social' ? (
              <div ref={socialRef}>
                <SocialSignIn
                  googleConfigured={googleConfigured}
                  loading={loading}
                  onLoading={setLoading}
                  onSuccess={completeLogin}
                  onError={(msg) => {
                    setError(msg);
                    if (msg) shakeCard();
                  }}
                  onMobileClick={() => switchMode('mobile')}
                  onPasswordClick={() => switchMode('password')}
                />
              </div>
            ) : mode === 'mobile' ? (
              <form className="login-form" onSubmit={otpSent ? handleVerifyOtp : handleSendOtp}>
                <button
                  type="button"
                  className="mobile-back"
                  onClick={() => switchMode('social')}
                >
                  ← Back to sign-in options
                </button>
                <div className="form-group" ref={(el) => (formFieldsRef.current[0] = el)}>
                  <label htmlFor="mobile">Mobile number</label>
                  <input
                    id="mobile"
                    type="tel"
                    placeholder="9876543210"
                    value={mobile}
                    onChange={(e) => setMobile(e.target.value.replace(/\D/g, '').slice(0, 10))}
                    required
                    disabled={otpSent}
                    pattern="[6-9][0-9]{9}"
                    title="Enter a valid 10-digit Indian mobile number"
                  />
                </div>
                {otpSent && (
                  <div className="form-group" ref={(el) => (formFieldsRef.current[1] = el)}>
                    <label htmlFor="otp">OTP</label>
                    <input
                      id="otp"
                      type="text"
                      inputMode="numeric"
                      placeholder="6-digit OTP"
                      maxLength={6}
                      value={otp}
                      onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                      required
                    />
                    <p className="login-hint">OTP sent to your mobile. Valid for 5 minutes.</p>
                  </div>
                )}
                <div ref={(el) => (formFieldsRef.current[2] = el)}>
                  <button type="submit" className="btn-primary" disabled={loading}>
                    {loading ? 'Please wait…' : otpSent ? 'Verify & sign in' : 'Send OTP'}
                  </button>
                </div>
              </form>
            ) : (
              <AccountLoginForm
                key={mode}
                mode={mode}
                loading={loading}
                onLoading={setLoading}
                onSuccess={completeLogin}
                onError={(msg) => {
                  setError(msg);
                  if (msg) shakeCard();
                }}
                onForgotClick={() => switchMode('forgot')}
                onBack={(target) => switchMode(target)}
                formFieldsRef={formFieldsRef}
              />
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
