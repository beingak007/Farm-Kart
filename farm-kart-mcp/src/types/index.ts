// ── Service base URLs ────────────────────────────────────────────────────────
export interface ServiceConfig {
  marketplaceUrl: string;
  farmerUrl: string;
  buyerUrl: string;
  logisticsUrl: string;
  warehouseUrl: string;
  marketPriceUrl: string;
  adminUrl: string;
  notificationUrl: string;
  catalogUrl: string;
  reportingUrl: string;
  aiAdvisoryUrl: string;
}

export function getServiceConfig(): ServiceConfig {
  // All domain APIs (farmer, buyer, logistics, warehouse, market-price, admin,
  // catalog, reporting, ai-advisory) are merged into the single Farm Kart app.
  // Only notification runs as a separate service.
  const appUrl = process.env.FARMKART_APP_URL ?? "http://localhost:8080/farm-kart";
  return {
    marketplaceUrl:  appUrl,
    farmerUrl:       appUrl,
    buyerUrl:        appUrl,
    logisticsUrl:    appUrl,
    warehouseUrl:    appUrl,
    marketPriceUrl:  appUrl,
    adminUrl:        appUrl,
    notificationUrl: process.env.NOTIF_URL ?? "http://localhost:8087/notification-service",
    catalogUrl:      appUrl,
    reportingUrl:    appUrl,
    aiAdvisoryUrl:   appUrl,
  };
}

// ── Common API response wrapper ───────────────────────────────────────────────
export interface FieldErrorDetail {
  field: string;
  message: string;
  code: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: FieldErrorDetail[];
}

export interface ResponseMeta {
  requestId: string;
  timestamp: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: ApiError;
  meta?: ResponseMeta;
}

// ── Domain types ─────────────────────────────────────────────────────────────
export interface FarmerProfile {
  id: number;
  userId: number;
  farmName: string;
  address: string;
  state: string;
  district: string;
  pincode: string;
  farmAreaAcres?: number;
  primaryCrop?: string;
  status: string;
  createdAt: string;
}

export interface CropListing {
  id: number;
  name: string;
  slug: string;
  categoryName: string;
  unit: string;
  basePrice: number;
  harvestSeason?: string;
  gradeStandard?: string;
  isOrganic: boolean;
  isActive: boolean;
}

export interface MandiPrice {
  id: number;
  cropName: string;
  mandiName: string;
  state: string;
  district: string;
  pricePerQuintal: number;
  minPrice?: number;
  maxPrice?: number;
  priceDate: string;
  source: string;
}

export interface ShipmentInfo {
  id: number;
  orderId: number;
  trackingNumber: string;
  pickupAddress: string;
  deliveryAddress: string;
  status: string;
  expectedDelivery?: string;
  actualDelivery?: string;
}

export interface WarehouseInfo {
  id: number;
  name: string;
  address: string;
  state: string;
  district: string;
  availableCapacityTons: number;
  coldStorage: boolean;
  status: string;
}
