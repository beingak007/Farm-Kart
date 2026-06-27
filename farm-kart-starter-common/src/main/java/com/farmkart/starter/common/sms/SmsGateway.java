package com.farmkart.starter.common.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.starter.common.configuration.FarmKartProperties;
import com.farmkart.starter.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Sends SMS via Fast2SMS (free tier for India) or logs to console in dev mode.
 * Sign up at https://www.fast2sms.com and set FAST2SMS_API_KEY to enable real delivery.
 */
public class SmsGateway {

    private static final Logger log = LoggerFactory.getLogger(SmsGateway.class);
    private static final URI FAST2SMS_URI = URI.create("https://www.fast2sms.com/dev/bulkV2");

    private final FarmKartProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SmsGateway(FarmKartProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendOtp(String mobile, String otp) {
        long ttlMinutes = properties.getOtp().getTtlMinutes();
        String message = "Your Farm Kart OTP is " + otp + ". Valid for " + ttlMinutes + " minutes. Do not share.";
        send(mobile, message, otp);
    }

    public void send(String mobile, String message) {
        send(mobile, message, null);
    }

    private void send(String mobile, String message, String otp) {
        FarmKartProperties.Sms sms = properties.getSms();
        String normalizedMobile = normalizeMobile(mobile);

        if (useConsoleProvider(sms)) {
            log.info("SMS (console) → {} : {}", normalizedMobile, message);
            return;
        }

        if ("fast2sms".equalsIgnoreCase(sms.getProvider())) {
            sendViaFast2Sms(normalizedMobile, message, otp, sms.getApiKey());
            return;
        }

        log.warn("Unknown SMS provider '{}', falling back to console", sms.getProvider());
        log.info("SMS (console) → {} : {}", normalizedMobile, message);
    }

    private boolean useConsoleProvider(FarmKartProperties.Sms sms) {
        return "console".equalsIgnoreCase(sms.getProvider())
                || sms.getApiKey() == null
                || sms.getApiKey().isBlank();
    }

    private void sendViaFast2Sms(String mobile, String message, String otp, String apiKey) {
        try {
            Map<String, Object> body = otp != null
                    ? Map.of("route", "otp", "variables_values", otp, "numbers", mobile)
                    : Map.of("route", "q", "message", message, "numbers", mobile, "language", "english");

            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder(FAST2SMS_URI)
                    .timeout(Duration.ofSeconds(15))
                    .header("authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode result = objectMapper.readTree(response.body());

            if (response.statusCode() >= 400 || !result.path("return").asBoolean(false)) {
                String reason = result.path("message").isArray()
                        ? result.path("message").get(0).asText("SMS delivery failed")
                        : result.path("message").asText("SMS delivery failed");
                log.error("Fast2SMS failed for {}: status={} body={}", mobile, response.statusCode(), response.body());
                throw new BusinessException("Failed to send OTP SMS: " + reason);
            }

            log.info("SMS sent via Fast2SMS to {}", mobile);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Fast2SMS request failed for {}: {}", mobile, ex.getMessage());
            throw new BusinessException("Failed to send OTP SMS. Please try again.");
        }
    }

    static String normalizeMobile(String mobile) {
        if (mobile == null) {
            return "";
        }
        String digits = mobile.replaceAll("\\D", "");
        if (digits.length() > 10 && digits.startsWith("91")) {
            return digits.substring(digits.length() - 10);
        }
        return digits;
    }
}
