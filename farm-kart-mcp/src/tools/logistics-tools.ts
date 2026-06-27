import { z } from "zod";
import { Clients, unwrap } from "../clients/api.js";

export function registerLogisticsTools(server: any, clients: Clients) {
  // ── Track shipment ─────────────────────────────────────────────────────────
  server.tool(
    "farmkart_track_shipment",
    "Track a Farm Kart shipment by tracking number. Returns current status, location, and expected delivery date.",
    {
      trackingNumber: z.string().describe("Shipment tracking number, e.g. FK-A1B2C3D4"),
    },
    async ({ trackingNumber }: { trackingNumber: string }) => {
      const res = await clients.logistics.get(`/api/v1/shipments/track/${trackingNumber}`);
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Get shipments for an order ─────────────────────────────────────────────
  server.tool(
    "farmkart_get_order_shipments",
    "Get all shipments associated with a specific order",
    {
      orderId: z.number().describe("The order ID"),
    },
    async ({ orderId }: { orderId: number }) => {
      const res = await clients.logistics.get(`/api/v1/shipments/order/${orderId}`);
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Find warehouses ────────────────────────────────────────────────────────
  server.tool(
    "farmkart_find_warehouses",
    "Find available warehouses in a state with sufficient capacity for storing crops",
    {
      state: z.string().describe("Indian state to search in"),
      tonsNeeded: z.number().describe("Minimum available storage capacity needed in metric tons"),
    },
    async ({ state, tonsNeeded }: { state: string; tonsNeeded: number }) => {
      const res = await clients.warehouse.get("/api/v1/warehouses/available", {
        params: { state, tons: tonsNeeded },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );
}
