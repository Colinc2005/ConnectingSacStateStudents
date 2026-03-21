package com.sacconnect.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final long VERIFICATION_EXPIRY_SECONDS = 15 * 60;

    public String generateVerificationCode() {
        int maxValue = (int) Math.pow(10, VERIFICATION_CODE_LENGTH);
        int code = (int) (Math.random() * maxValue);
        return String.format("%0" + VERIFICATION_CODE_LENGTH + "d", code);
    }

    public Instant generateVerificationExpiry() {
        return Instant.now().plusSeconds(VERIFICATION_EXPIRY_SECONDS);
    }
}