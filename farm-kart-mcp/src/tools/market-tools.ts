import { z } from "zod";
import { Clients, unwrap } from "../clients/api.js";

export function registerMarketTools(server: any, clients: Clients) {
  // ── Latest mandi prices ────────────────────────────────────────────────────
  server.tool(
    "farmkart_get_mandi_prices",
    "Get the latest mandi (wholesale market) prices for a crop in a specific state. Returns price per quintal from multiple mandis.",
    {
      cropName: z.string().describe("Crop name, e.g. wheat, onion, tomato, rice"),
      state: z.string().describe("Indian state, e.g. Maharashtra, Punjab"),
    },
    async ({ cropName, state }: { cropName: string; state: string }) => {
      const res = await clients.marketPrice.get("/api/v1/market-prices/latest", {
        params: { cropName, state },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Historical prices ──────────────────────────────────────────────────────
  server.tool(
    "farmkart_get_price_history",
    "Get historical mandi price data for a crop in a state between two dates. Useful for price trend analysis.",
    {
      cropName: z.string().describe("Crop name"),
      state: z.string().describe("Indian state"),
      fromDate: z.string().describe("Start date in YYYY-MM-DD format"),
      toDate: z.string().describe("End date in YYYY-MM-DD format"),
    },
    async ({ cropName, state, fromDate, toDate }: any) => {
      const res = await clients.marketPrice.get("/api/v1/market-prices/history", {
        params: { cropName, state, from: fromDate, to: toDate },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Demand forecast ────────────────────────────────────────────────────────
  server.tool(
    "farmkart_get_demand_forecast",
    "Get AI-powered weekly demand and price forecast for a crop. Returns predicted prices and demand volume for upcoming weeks.",
    {
      cropName: z.string().describe("Crop name to forecast"),
      state: z.string().optional().describe("State for localised forecast (optional)"),
      forecastWeeks: z
        .number()
        .min(1)
        .max(12)
        .default(4)
        .describe("Number of weeks to forecast (1-12)"),
    },
    async ({ cropName, state, forecastWeeks }: any) => {
      const res = await clients.aiAdvisory.post("/api/v1/ai-advisory/demand-forecast", {
        cropName,
        state,
        forecastWeeks,
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );
}
