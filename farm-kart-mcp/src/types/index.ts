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
  return {
    marketplaceUrl:  process.env.MARKETPLACE_URL  ?? "http://localhost:8080",
    farmerUrl:       process.env.FARMER_URL        ?? "http://localhost:8081/farmer-service",
    buyerUrl:        process.env.BUYER_URL         ?? "http://localhost:8082/buyer-service",
    logisticsUrl:    process.env.LOGISTICS_URL     ?? "http://localhost:8083/logistics-service",
    warehouseUrl:    process.env.WAREHOUSE_URL     ?? "http://localhost:8084/warehouse-service",
    marketPriceUrl:  process.env.MARKET_PRICE_URL  ?? "http://localhost:8085/market-price-service",
    adminUrl:        process.env.ADMIN_URL         ?? "http://localhost:8086/admin-service",
    notificationUrl: process.env.NOTIF_URL         ?? "http://localhost:8087/notification-service",
    catalogUrl:      process.env.CATALOG_URL       ?? "http://localhost:8088/catalog-service",
    reportingUrl:    process.env.REPORTING_URL     ?? "http://localhost:8089/reporting-service",
    aiAdvisoryUrl:   process.env.AI_ADVISORY_URL   ?? "http://localhost:8090/ai-advisory-service",
  };
}

// ── Common API response wrapper ───────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp?: string;
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
