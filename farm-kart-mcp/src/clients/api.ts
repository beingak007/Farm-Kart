import axios, { AxiosInstance } from "axios";
import { ServiceConfig } from "../types/index.js";

/**
 * Creates a pre-configured Axios instance for a given base URL.
 * Adds the JWT bearer token from env if set (used in secured deployments).
 */
export function createClient(baseUrl: string): AxiosInstance {
  const token = process.env.FARMKART_JWT_TOKEN;
  return axios.create({
    baseURL: baseUrl,
    timeout: 15_000,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });
}

/** Unwraps a Farm Kart standard ApiResponse<T> */
export function unwrap<T>(response: { data: { data: T } }): T {
  return response.data.data;
}

/** Helper: create all service clients from the config at once */
export function createAllClients(cfg: ServiceConfig) {
  return {
    marketplace:  createClient(cfg.marketplaceUrl),
    farmer:       createClient(cfg.farmerUrl),
    buyer:        createClient(cfg.buyerUrl),
    logistics:    createClient(cfg.logisticsUrl),
    warehouse:    createClient(cfg.warehouseUrl),
    marketPrice:  createClient(cfg.marketPriceUrl),
    admin:        createClient(cfg.adminUrl),
    notification: createClient(cfg.notificationUrl),
    catalog:      createClient(cfg.catalogUrl),
    reporting:    createClient(cfg.reportingUrl),
    aiAdvisory:   createClient(cfg.aiAdvisoryUrl),
  };
}

export type Clients = ReturnType<typeof createAllClients>;
