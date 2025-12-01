package com.sacconnect.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

class WebConfigTest {

    @Test
    void addResourceHandlers_registersUploadsHandlerWithCorrectLocation() {
        
        WebConfig config = new WebConfig();

        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        
        // verify addResourceLocations is called 
        when(registry.addResourceHandler("/uploads/**")).thenReturn(registration);
        when(registration.addResourceLocations("file:uploads/")).thenReturn(registration);

       
        config.addResourceHandlers(registry);

        
        verify(registry).addResourceHandler("/uploads/**");
        verify(registration).addResourceLocations("file:uploads/");
    }
}
