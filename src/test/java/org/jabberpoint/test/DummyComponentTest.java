package org.jabberpoint.test;

import org.jabberpoint.src.DummyComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DummyComponent class
 */
public class DummyComponentTest {
    
    @Test
    @DisplayName("Should create DummyComponent instance")
    void shouldCreateDummyComponentInstance() {
        // Act
        DummyComponent component = new DummyComponent();
        
        // Assert
        assertNotNull(component);
        assertTrue(component instanceof DummyComponent);
    }
    
    @Test
    @DisplayName("Should have correct serialVersionUID")
    void shouldHaveCorrectSerialVersionUID() {
        // This tests a constant, but helps with coverage
        try {
            // Get serialVersionUID field through reflection
            java.lang.reflect.Field field = DummyComponent.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            
            // Get the value
            Object value = field.get(null); // null for static fields
            
            // Assert
            assertEquals(1L, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not access serialVersionUID field: " + e.getMessage());
        }
    }
}
