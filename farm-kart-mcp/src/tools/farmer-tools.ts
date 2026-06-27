import { z } from "zod";
import { Clients, unwrap } from "../clients/api.js";

export function registerFarmerTools(server: any, clients: Clients) {
  // ── Get farmer profile by user ID ─────────────────────────────────────────
  server.tool(
    "farmkart_get_farmer_by_user",
    "Get a farmer's profile, farm details, and verification status using their platform user ID",
    {
      userId: z.number().describe("The platform user ID of the farmer"),
    },
    async ({ userId }: { userId: number }) => {
      const res = await clients.farmer.get(`/api/v1/farmers/user/${userId}`);
      const farmer = unwrap<any>(res);
      return {
        content: [
          {
            type: "text" as const,
            text: JSON.stringify(farmer, null, 2),
          },
        ],
      };
    }
  );

  // ── List farmers by state ──────────────────────────────────────────────────
  server.tool(
    "farmkart_list_farmers_by_state",
    "List verified farmers in a specific Indian state with pagination",
    {
      state: z.string().describe("Indian state name, e.g. Maharashtra, Punjab, Karnataka"),
      page: z.number().default(0).describe("Page number (0-indexed)"),
      size: z.number().default(10).describe("Results per page"),
    },
    async ({ state, page, size }: { state: string; page: number; size: number }) => {
      const res = await clients.farmer.get(`/api/v1/farmers/state/${state}`, {
        params: { page, size },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Get AI crop recommendation for a farmer ───────────────────────────────
  server.tool(
    "farmkart_recommend_crops",
    "Get AI-powered crop recommendations for a farmer based on their location, season, and soil type",
    {
      farmerId: z.number().describe("Farmer ID"),
      state: z.string().describe("State of the farm"),
      district: z.string().describe("District of the farm"),
      season: z.enum(["RABI", "KHARIF", "ZAID"]).describe("Upcoming growing season"),
      soilType: z
        .enum(["CLAY", "SANDY", "LOAMY", "BLACK", "RED"])
        .optional()
        .describe("Soil type if known"),
      farmAreaAcres: z.number().optional().describe("Farm area in acres"),
      irrigationAvailable: z
        .enum(["YES", "NO", "PARTIAL"])
        .optional()
        .describe("Irrigation availability"),
    },
    async (args: any) => {
      const res = await clients.aiAdvisory.post("/api/v1/ai-advisory/crop-recommendations", args);
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );
}
