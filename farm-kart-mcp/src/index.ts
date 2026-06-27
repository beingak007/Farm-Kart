#!/usr/bin/env node
/**
 * Farm Kart MCP Server
 *
 * Exposes all Farm Kart microservice APIs as Model Context Protocol (MCP) tools.
 * AI agents (Claude, GPT-4, etc.) connected to this server can:
 *   - Search the crop catalog and market prices
 *   - Get AI crop recommendations and demand forecasts
 *   - Track shipments and find warehouses
 *   - Generate business reports
 *   - Send notifications
 *   - Query farmer/buyer profiles and audit logs
 *
 * Transport: stdio (default) — compatible with Cursor, Claude Desktop, etc.
 *
 * Usage:
 *   npx tsx src/index.ts              # development
 *   node dist/index.js                # production
 *
 * Environment variables:
 *   MARKETPLACE_URL    (default: http://localhost:8080)
 *   FARMER_URL         (default: http://localhost:8081/farmer-service)
 *   BUYER_URL          (default: http://localhost:8082/buyer-service)
 *   LOGISTICS_URL      (default: http://localhost:8083/logistics-service)
 *   WAREHOUSE_URL      (default: http://localhost:8084/warehouse-service)
 *   MARKET_PRICE_URL   (default: http://localhost:8085/market-price-service)
 *   ADMIN_URL          (default: http://localhost:8086/admin-service)
 *   NOTIF_URL          (default: http://localhost:8087/notification-service)
 *   CATALOG_URL        (default: http://localhost:8088/catalog-service)
 *   REPORTING_URL      (default: http://localhost:8089/reporting-service)
 *   AI_ADVISORY_URL    (default: http://localhost:8090/ai-advisory-service)
 *   FARMKART_JWT_TOKEN (optional JWT bearer token for secured deployments)
 */

import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";
import { getServiceConfig } from "./types/index.js";
import { createAllClients } from "./clients/api.js";
import { registerFarmerTools } from "./tools/farmer-tools.js";
import { registerCatalogTools } from "./tools/catalog-tools.js";
import { registerMarketTools } from "./tools/market-tools.js";
import { registerLogisticsTools } from "./tools/logistics-tools.js";
import { registerAdminTools } from "./tools/admin-tools.js";

const config = getServiceConfig();
const clients = createAllClients(config);

const server = new McpServer({
  name: "farm-kart-mcp",
  version: "1.0.0",
});

// ── Register all tool groups ───────────────────────────────────────────────
registerFarmerTools(server, clients);
registerCatalogTools(server, clients);
registerMarketTools(server, clients);
registerLogisticsTools(server, clients);
registerAdminTools(server, clients);

// ── Resources: expose service health endpoints as readable resources ────────
server.resource(
  "farmkart://services",
  "farmkart://services",
  async () => {
    const services = [
      { name: "Marketplace",      port: 8080, path: "" },
      { name: "Farmer",           port: 8081, path: "/farmer-service" },
      { name: "Buyer",            port: 8082, path: "/buyer-service" },
      { name: "Logistics",        port: 8083, path: "/logistics-service" },
      { name: "Warehouse",        port: 8084, path: "/warehouse-service" },
      { name: "Market Price",     port: 8085, path: "/market-price-service" },
      { name: "Admin",            port: 8086, path: "/admin-service" },
      { name: "Notification",     port: 8087, path: "/notification-service" },
      { name: "Product Catalog",  port: 8088, path: "/catalog-service" },
      { name: "Reporting",        port: 8089, path: "/reporting-service" },
      { name: "AI Advisory",      port: 8090, path: "/ai-advisory-service" },
    ];
    return {
      contents: [
        {
          uri: "farmkart://services",
          text: JSON.stringify(services, null, 2),
          mimeType: "application/json",
        },
      ],
    };
  }
);

// ── Prompts: reusable agent prompts ─────────────────────────────────────────
server.prompt(
  "farmer_advisory",
  "Generate a complete advisory report for a farmer covering crop recommendations, current mandi prices, and demand forecast",
  {
    farmerId: z.string().describe("Farmer ID"),
    state:    z.string().describe("State of the farm"),
    district: z.string().describe("District of the farm"),
    season:   z.string().describe("Growing season: RABI, KHARIF, or ZAID"),
  },
  ({ farmerId, state, district, season }) => ({
    messages: [
      {
        role: "user" as const,
        content: {
          type: "text" as const,
          text: `You are a Farm Kart AI agricultural advisor. Generate a complete advisory for farmer ID ${farmerId} in ${district}, ${state} for the ${season} season.

Please:
1. Use farmkart_recommend_crops to get crop recommendations
2. For the top 2 recommended crops, use farmkart_get_mandi_prices to get current market rates
3. Use farmkart_get_demand_forecast for each crop (4 weeks)
4. Use farmkart_find_warehouses to check storage options in ${state}

Provide a structured advisory report with:
- Top crop recommendations with rationale
- Current price intelligence per crop
- 4-week price outlook
- Storage options if harvest exceeds direct sale capacity
- Final recommendation with expected income estimate`,
        },
      },
    ],
  })
);

server.prompt(
  "market_intelligence",
  "Get comprehensive market intelligence for a crop across multiple states",
  {
    cropName: z.string().describe("Crop name"),
  },
  ({ cropName }) => ({
    messages: [
      {
        role: "user" as const,
        content: {
          type: "text" as const,
          text: `You are a Farm Kart market intelligence analyst. Provide comprehensive market intelligence for ${cropName}.

Please:
1. Use farmkart_search_crops to get catalog details for ${cropName}
2. Get mandi prices from Maharashtra, Punjab, and Karnataka using farmkart_get_mandi_prices
3. Get a 8-week demand forecast using farmkart_get_demand_forecast
4. Compare prices across states and identify the best selling location

Provide:
- Current national price range
- State-wise price comparison table
- Price trend (rising/stable/falling) with evidence
- Best state to sell and expected premium
- Optimal selling window based on forecast`,
        },
      },
    ],
  })
);

// ── Start server ──────────────────────────────────────────────────────────
const transport = new StdioServerTransport();
await server.connect(transport);

process.stderr.write("Farm Kart MCP Server started. Listening on stdio.\n");
