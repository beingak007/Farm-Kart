import axios, { AxiosError, AxiosInstance, AxiosResponse } from "axios";
import { ApiResponse } from "../types/index.js";

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

export class FarmKartApiError extends Error {
  readonly code: string;
  readonly details: Array<{ field: string; message: string; code: string }>;
  readonly requestId: string | null;
  readonly status: number;

  constructor(body: ApiResponse<unknown>, status: number) {
    const err = body.error;
    super(err?.message ?? body.message ?? "Request failed");
    this.name = "FarmKartApiError";
    this.code = err?.code ?? "UNKNOWN";
    this.details = err?.details ?? [];
    this.requestId = body.meta?.requestId ?? null;
    this.status = status;
  }
}

/** Unwraps a Farm Kart standard ApiResponse<T>; throws FarmKartApiError on failure. */
export function unwrap<T>(response: AxiosResponse<ApiResponse<T>>): T {
  const body = response.data;
  if (!body.success) {
    throw new FarmKartApiError(body, response.status);
  }
  if (body.data === undefined || body.data === null) {
    throw new FarmKartApiError(
      {
        success: false,
        error: { code: "EMPTY_RESPONSE", message: "API returned no data" },
      },
      response.status
    );
  }
  return body.data;
}

/** Extract structured error from axios failure, if present. */
export function toFarmKartError(err: unknown): FarmKartApiError | null {
  if (err instanceof FarmKartApiError) return err;
  if (axios.isAxiosError(err)) {
    const ax = err as AxiosError<ApiResponse<unknown>>;
    if (ax.response?.data && ax.response.data.success === false) {
      return new FarmKartApiError(ax.response.data, ax.response.status);
    }
  }
  return null;
}

/** Helper: create all service clients from the config at once */
export function createAllClients(cfg: import("../types/index.js").ServiceConfig) {
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
