package com.almoxaf.api.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    @Test
    void devePermitirAteOLimite() {
        RateLimiter limiter = new RateLimiter(3, Duration.ofMinutes(1));

        assertTrue(limiter.permitir("1.2.3.4"));
        assertTrue(limiter.permitir("1.2.3.4"));
        assertTrue(limiter.permitir("1.2.3.4"));
    }

    @Test
    void deveBloquearAposExcederOLimite() {
        RateLimiter limiter = new RateLimiter(3, Duration.ofMinutes(1));

        limiter.permitir("1.2.3.4");
        limiter.permitir("1.2.3.4");
        limiter.permitir("1.2.3.4");

        assertFalse(limiter.permitir("1.2.3.4"));
    }

    @Test
    void chavesDiferentesTemContadoresIndependentes() {
        RateLimiter limiter = new RateLimiter(1, Duration.ofMinutes(1));

        assertTrue(limiter.permitir("1.2.3.4"));
        assertFalse(limiter.permitir("1.2.3.4"));

        assertTrue(limiter.permitir("5.6.7.8"));
    }
}
