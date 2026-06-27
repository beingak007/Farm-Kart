import { z } from "zod";
import { Clients, unwrap } from "../clients/api.js";

export function registerAdminTools(server: any, clients: Clients) {
  // ── Get audit logs for a user ──────────────────────────────────────────────
  server.tool(
    "farmkart_get_user_audit_logs",
    "Get the audit trail of actions performed by a specific user on the platform",
    {
      userId: z.number().describe("User ID to fetch audit logs for"),
      page: z.number().default(0),
      size: z.number().default(20),
    },
    async ({ userId, page, size }: any) => {
      const res = await clients.admin.get(`/api/v1/admin/audit-logs/user/${userId}`, {
        params: { page, size },
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Submit a report ────────────────────────────────────────────────────────
  server.tool(
    "farmkart_generate_report",
    "Submit a business report generation job. Returns a job ID to poll for status and results. Supported types: SALES, ORDER_SUMMARY, FARMER_ACTIVITY, BUYER_ACTIVITY, PAYMENT_SUMMARY",
    {
      reportType: z
        .enum(["SALES", "ORDER_SUMMARY", "FARMER_ACTIVITY", "BUYER_ACTIVITY", "PAYMENT_SUMMARY"])
        .describe("Type of report to generate"),
      fromDate: z.string().describe("Start date in YYYY-MM-DD format"),
      toDate: z.string().describe("End date in YYYY-MM-DD format"),
      filters: z
        .record(z.string())
        .optional()
        .describe("Optional filters, e.g. { state: 'Maharashtra', cropName: 'Wheat' }"),
    },
    async ({ reportType, fromDate, toDate, filters }: any) => {
      const res = await clients.reporting.post("/api/v1/reports", {
        reportType,
        fromDate,
        toDate,
        filters: filters ?? {},
        format: "JSON",
      });
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Check report job ───────────────────────────────────────────────────────
  server.tool(
    "farmkart_get_report_status",
    "Check the status of a previously submitted report generation job",
    {
      jobId: z.number().describe("Report job ID returned by farmkart_generate_report"),
    },
    async ({ jobId }: { jobId: number }) => {
      const res = await clients.reporting.get(`/api/v1/reports/${jobId}`);
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );

  // ── Send notification ──────────────────────────────────────────────────────
  server.tool(
    "farmkart_send_notification",
    "Send an SMS, Email, or Push notification to a user on the platform",
    {
      userId: z.number().describe("Recipient user ID"),
      channel: z.enum(["SMS", "EMAIL", "PUSH", "WHATSAPP"]).describe("Notification channel"),
      recipientContact: z
        .string()
        .describe("Phone number (for SMS/WhatsApp) or email address"),
      templateCode: z
        .string()
        .describe(
          "Notification template code, e.g. ORDER_CONFIRMED, PAYMENT_SUCCESS, FARMER_VERIFIED"
        ),
      templateVars: z
        .record(z.string())
        .optional()
        .describe("Template variable substitutions"),
    },
    async (args: any) => {
      const res = await clients.notification.post("/api/v1/notifications/send", args);
      return {
        content: [{ type: "text" as const, text: JSON.stringify(unwrap<any>(res), null, 2) }],
      };
    }
  );
}
