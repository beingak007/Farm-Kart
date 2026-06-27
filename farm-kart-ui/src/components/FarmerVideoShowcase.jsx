import { useEffect, useRef } from 'react';
import gsap from 'gsap';

const POSTER = '/videos/indian-farmer-poster.png';

const FLOATING_CROPS = [
  { icon: '🌾', x: '-18%', y: '8%', size: '1.6rem', delay: 0 },
  { icon: '🌽', x: '108%', y: '22%', size: '1.4rem', delay: 0.3 },
  { icon: '🥬', x: '-12%', y: '68%', size: '1.3rem', delay: 0.6 },
  { icon: '🍅', x: '102%', y: '78%', size: '1.2rem', delay: 0.9 },
  { icon: '🌻', x: '45%', y: '-14%', size: '1.5rem', delay: 0.45 },
];

export default function FarmerVideoShowcase() {
  const rootRef = useRef(null);
  const orbitRef = useRef(null);
  const cardRef = useRef(null);
  const imageRef = useRef(null);
  const shimmerRef = useRef(null);
  const badgeRef = useRef(null);
  const captionRef = useRef(null);
  const cropsRef = useRef([]);
  const ringsRef = useRef([]);

  useEffect(() => {
    const ctx = gsap.context(() => {
      const master = gsap.timeline({ defaults: { ease: 'power3.out' } });

      master
        .from(ringsRef.current, {
          scale: 0,
          opacity: 0,
          duration: 1.4,
          stagger: 0.15,
          ease: 'expo.out',
        })
        .from(
          cardRef.current,
          {
            x: 120,
            opacity: 0,
            scale: 0.72,
            rotation: 8,
            duration: 1.1,
            ease: 'back.out(1.5)',
          },
          '-=0.9'
        )
        .from(
          imageRef.current,
          { scale: 1.35, opacity: 0, duration: 1.2, ease: 'power2.out' },
          '-=0.7'
        )
        .from(
          badgeRef.current,
          { y: -20, opacity: 0, scale: 0.6, duration: 0.55, ease: 'back.out(2.5)' },
          '-=0.4'
        )
        .from(
          captionRef.current,
          { y: 24, opacity: 0, duration: 0.6 },
          '-=0.25'
        )
        .from(
          cropsRef.current,
          {
            scale: 0,
            opacity: 0,
            rotation: gsap.utils.wrap([-20, 20, -15, 15, -10]),
            duration: 0.7,
            stagger: 0.1,
            ease: 'back.out(2)',
          },
          '-=0.35'
        );

      // Ken Burns — GSAP instead of CSS
      gsap.fromTo(
        imageRef.current,
        { scale: 1.05, x: '-2%', y: '-1%' },
        {
          scale: 1.18,
          x: '2%',
          y: '2%',
          duration: 10,
          repeat: -1,
          yoyo: true,
          ease: 'sine.inOut',
        }
      );

      // Card gentle float
      gsap.to(cardRef.current, {
        y: -14,
        duration: 3.2,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
        delay: 1,
      });

      // Orbit rotation
      gsap.to(orbitRef.current, {
        rotation: 360,
        duration: 28,
        repeat: -1,
        ease: 'none',
      });

      // Expanding rings
      ringsRef.current.forEach((ring, i) => {
        gsap.fromTo(
          ring,
          { scale: 1, opacity: 0.35 - i * 0.08 },
          {
            scale: 1.25 + i * 0.12,
            opacity: 0,
            duration: 2.8 + i * 0.4,
            repeat: -1,
            delay: i * 0.7,
            ease: 'power1.out',
          }
        );
      });

      // Shimmer sweep
      gsap.fromTo(
        shimmerRef.current,
        { x: '-120%', skewX: -20 },
        {
          x: '220%',
          duration: 2.4,
          repeat: -1,
          repeatDelay: 3.5,
          ease: 'power2.inOut',
        }
      );

      // Live badge dot pulse
      gsap.to('.farmer-live-dot', {
        scale: 0.6,
        opacity: 0.4,
        duration: 0.7,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
      });

      // Orbiting crops
      cropsRef.current.forEach((crop, i) => {
        gsap.to(crop, {
          y: '+=18',
          x: i % 2 === 0 ? '+=8' : '-=8',
          rotation: i % 2 === 0 ? 12 : -12,
          duration: 2.5 + i * 0.3,
          repeat: -1,
          yoyo: true,
          ease: 'sine.inOut',
          delay: FLOATING_CROPS[i].delay,
        });
      });
    }, rootRef);

    return () => ctx.revert();
  }, []);

  return (
    <div className="farmer-scene" ref={rootRef} aria-hidden="true">
      <div className="farmer-scene-orbit" ref={orbitRef}>
        {FLOATING_CROPS.map((crop, i) => (
          <span
            key={crop.icon}
            className="farmer-scene-crop"
            ref={(el) => (cropsRef.current[i] = el)}
            style={{ left: crop.x, top: crop.y, fontSize: crop.size }}
          >
            {crop.icon}
          </span>
        ))}
      </div>

      {[0, 1, 2].map((i) => (
        <span
          key={i}
          className="farmer-scene-ring"
          ref={(el) => (ringsRef.current[i] = el)}
          style={{ inset: `${8 + i * 10}%` }}
        />
      ))}

      <div className="farmer-scene-card" ref={cardRef}>
        <div className="farmer-scene-media">
          <img
            ref={imageRef}
            className="farmer-scene-image"
            src={POSTER}
            alt=""
            draggable={false}
          />
          <div className="farmer-scene-overlay" />
          <div className="farmer-scene-shimmer" ref={shimmerRef} />
          <span className="farmer-scene-badge" ref={badgeRef}>
            <span className="farmer-live-dot" />
            Live from the fields
          </span>
        </div>
        <p className="farmer-scene-caption" ref={captionRef}>
          Indian farmers bringing fresh harvest to your cart
        </p>
      </div>
    </div>
  );
}
