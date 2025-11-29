package com.sacconnect;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

class SacConnectApplicationTest {

    @Test
    void applicationClass_isAnnotatedWithSpringBootApplication() {
        boolean annotated = SacConnectApplication.class
                .isAnnotationPresent(SpringBootApplication.class);

        assertTrue(annotated,
                "SacConnectApplication should be annotated with @SpringBootApplication");
    }

    @Test
    void mainMethod_existsWithCorrectSignature() throws NoSuchMethodException {
        Method mainMethod = SacConnectApplication.class
                .getMethod("main", String[].class);

        assertNotNull(mainMethod, "main method should exist with String[] args");
    }
}
