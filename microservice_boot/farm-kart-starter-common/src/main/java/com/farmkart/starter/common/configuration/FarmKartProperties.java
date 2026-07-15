package com.farmkart.starter.common.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "farmkart")
public class FarmKartProperties {

    private Jwt jwt = new Jwt();
    private Otp otp = new Otp();
    private Sms sms = new Sms();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Otp getOtp() {
        return otp;
    }

    public void setOtp(Otp otp) {
        this.otp = otp;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }

    public static class Jwt {
        private String secret = "change-me-in-production-use-at-least-256-bits-secret-key-here";
        private long accessTokenExpiryMinutes = 15;
        private long refreshTokenExpiryDays = 7;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getAccessTokenExpiryMinutes() { return accessTokenExpiryMinutes; }
        public void setAccessTokenExpiryMinutes(long accessTokenExpiryMinutes) { this.accessTokenExpiryMinutes = accessTokenExpiryMinutes; }
        public long getRefreshTokenExpiryDays() { return refreshTokenExpiryDays; }
        public void setRefreshTokenExpiryDays(long refreshTokenExpiryDays) { this.refreshTokenExpiryDays = refreshTokenExpiryDays; }
    }

    public static class Otp {
        private long ttlMinutes = 5;
        private int maxSendRequests = 3;
        private long sendRateWindowMinutes = 15;
        private int maxVerifyAttempts = 5;
        private long lockDurationMinutes = 15;

        public long getTtlMinutes() { return ttlMinutes; }
        public void setTtlMinutes(long ttlMinutes) { this.ttlMinutes = ttlMinutes; }
        public int getMaxSendRequests() { return maxSendRequests; }
        public void setMaxSendRequests(int maxSendRequests) { this.maxSendRequests = maxSendRequests; }
        public long getSendRateWindowMinutes() { return sendRateWindowMinutes; }
        public void setSendRateWindowMinutes(long sendRateWindowMinutes) { this.sendRateWindowMinutes = sendRateWindowMinutes; }
        public int getMaxVerifyAttempts() { return maxVerifyAttempts; }
        public void setMaxVerifyAttempts(int maxVerifyAttempts) { this.maxVerifyAttempts = maxVerifyAttempts; }
        public long getLockDurationMinutes() { return lockDurationMinutes; }
        public void setLockDurationMinutes(long lockDurationMinutes) { this.lockDurationMinutes = lockDurationMinutes; }
    }

    public static class Sms {
        /** fast2sms (free tier) or console (log only, for local dev) */
        private String provider = "console";
        private String apiKey = "";
        private String senderId = "FARMKT";

        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
    }
}
