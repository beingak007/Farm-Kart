/**
 * Structured API error thrown by Farm Kart client helpers.
 * Mirrors backend {@code ApiError} envelope — safe for UI display.
 */
export class ApiClientError extends Error {
  /**
   * @param {object} payload - Parsed JSON body from API
   * @param {number} [status] - HTTP status code
   */
  constructor(payload, status) {
    const error = payload?.error ?? {};
    const message = error.message || payload?.message || 'Something went wrong. Please try again.';
    super(message);
    this.name = 'ApiClientError';
    this.code = error.code ?? 'UNKNOWN';
    this.details = error.details ?? [];
    this.requestId = payload?.meta?.requestId ?? null;
    this.status = status ?? 0;
  }

  /** First validation message for a form field, if any. */
  fieldError(field) {
    return this.details.find((d) => d.field === field)?.message ?? null;
  }

  /** All field-level validation messages as a map. */
  fieldErrors() {
    return Object.fromEntries(this.details.map((d) => [d.field, d.message]));
  }
}

/**
 * @param {Response} response
 * @returns {Promise<object>}
 */
export async function parseApiResponse(response) {
  let json;
  try {
    json = await response.json();
  } catch {
    throw new ApiClientError(
      { error: { code: 'INVALID_RESPONSE', message: 'Unexpected server response' } },
      response.status
    );
  }

  if (!response.ok || json.success === false) {
    throw new ApiClientError(json, response.status);
  }

  return json;
}
