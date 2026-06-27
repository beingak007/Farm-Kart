import { z } from "zod";
import { Clients, unwrap } from "../clients/api.js";

export function registerCatalogTools(server: any, clients: Clients) {
  // ── Search crops ───────────────────────────────────────────────────────────
  server.tool(
    "farmkart_search_crops",
    "Search the Farm Kart crop catalog by name or description. Returns matching crops with pricing and category information.",
    {
      query: z.string().describe("Search term, e.g. 'wheat', 'organic tomato', 'basmati rice'"),
      page: z.number().default(0),
      size: z.number().default(10),
    },
    async ({ query, page, size }: { query: string; page: number; size: number }) => {
      const res = await clients.catalog.get("/api/v1/catalog/crops/search", {
        params: { q: query, page, size },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── List categories ────────────────────────────────────────────────────────
  server.tool(
    "farmkart_list_categories",
    "List all active crop categories in the Farm Kart marketplace (Cereals, Vegetables, Fruits, Pulses, etc.)",
    {},
    async () => {
      const res = await clients.catalog.get("/api/v1/catalog/categories");
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Get crop by slug ───────────────────────────────────────────────────────
  server.tool(
    "farmkart_get_crop",
    "Get detailed information about a specific crop using its URL slug",
    {
      slug: z.string().describe("Crop slug, e.g. 'wheat', 'basmati-rice', 'alphonso-mango'"),
    },
    async ({ slug }: { slug: string }) => {
      const res = await clients.catalog.get(`/api/v1/catalog/crops/slug/${slug}`);
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── List organic crops ─────────────────────────────────────────────────────
  server.tool(
    "farmkart_list_organic_crops",
    "List all certified organic crops available on the platform",
    {
      page: z.number().default(0),
      size: z.number().default(20),
    },
    async ({ page, size }: { page: number; size: number }) => {
      const res = await clients.catalog.get("/api/v1/catalog/crops/organic", {
        params: { page, size },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );
}
