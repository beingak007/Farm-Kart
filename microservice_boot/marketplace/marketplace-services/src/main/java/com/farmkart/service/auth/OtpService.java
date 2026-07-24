package com.farmkart.service.auth;

import com.farmkart.starter.common.configuration.FarmKartProperties;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OtpService {

    private static final String OTP_PREFIX = "otp:";
    private static final String ATTEMPT_PREFIX = "otp_attempt:";
    private static final String RATE_PREFIX = "otp_rate:";
    private static final String LOCK_PREFIX = "otp_lock:";

    private final StringRedisTemplate redisTemplate;
    private final FarmKartProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(StringRedisTemplate redisTemplate, FarmKartProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public String generateAndStore(String key) {
        assertNotLocked(key);
        checkSendRateLimit(key);

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        redisTemplate.opsForValue().set(
                OTP_PREFIX + key,
                otp,
                Duration.ofMinutes(properties.getOtp().getTtlMinutes()));
        return otp;
    }

    public boolean verify(String key, String otp) {
        assertNotLocked(key);

        String redisKey = OTP_PREFIX + key;
        String stored = redisTemplate.opsForValue().get(redisKey);
        if (stored != null && stored.equals(otp)) {
            redisTemplate.delete(redisKey);
            redisTemplate.delete(ATTEMPT_PREFIX + key);
            return true;
        }

        recordFailedAttempt(key);
        return false;
    }

    public void invalidate(String key) {
        redisTemplate.delete(OTP_PREFIX + key);
    }

    private void assertNotLocked(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(LOCK_PREFIX + key))) {
            throw new BusinessException(429, "Too many failed attempts. Try again in "
                    + properties.getOtp().getLockDurationMinutes() + " minutes.");
        }
    }

    private void checkSendRateLimit(String key) {
        String rateKey = RATE_PREFIX + key;
        Long count = redisTemplate.opsForValue().increment(rateKey);
        if (count != null && count == 1) {
            redisTemplate.expire(rateKey, Duration.ofMinutes(properties.getOtp().getSendRateWindowMinutes()));
        }
        if (count != null && count > properties.getOtp().getMaxSendRequests()) {
            throw new BusinessException(429, "Too many OTP requests. Try again in "
                    + properties.getOtp().getSendRateWindowMinutes() + " minutes.");
        }
    }

    private void recordFailedAttempt(String key) {
        String attemptKey = ATTEMPT_PREFIX + key;
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey, Duration.ofMinutes(properties.getOtp().getSendRateWindowMinutes()));
        }
        if (attempts != null && attempts >= properties.getOtp().getMaxVerifyAttempts()) {
            redisTemplate.opsForValue().set(
                    LOCK_PREFIX + key,
                    "1",
                    Duration.ofMinutes(properties.getOtp().getLockDurationMinutes()));
            redisTemplate.delete(attemptKey);
            throw new BusinessException(429, "Too many invalid OTP attempts. Account locked for "
                    + properties.getOtp().getLockDurationMinutes() + " minutes.");
        }
    }
}
