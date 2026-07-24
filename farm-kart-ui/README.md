# Farm Kart UI

React + Vite frontend with GSAP-animated login and sheet upload.

## Run

```bash
# Backend (separate terminal)
mvn -pl marketplace/marketplace-rest spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
cd farm-kart-ui
npm install
npm run dev
```

Open: http://localhost:5173

## Features

- **Login page** — GSAP animations (hero slide-in, staggered form, floating produce icons)
- **Email/password** or **OTP** login tabs
- **Upload page** — drag & drop CSV/XLS/XLSX sheets after login
- Upload history table with file size, row count, status

## API

Proxied to `http://localhost:8080/farm-kart` via Vite dev server.
