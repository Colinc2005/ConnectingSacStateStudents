package com.sacconnect.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    void homeEndpoint_shouldReturnBackendRunningMessage() {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.home();

        // Assert
        assertEquals("SacConnect backend is running!", result);
    }
}
