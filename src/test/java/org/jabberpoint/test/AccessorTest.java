package org.jabberpoint.test;

import org.jabberpoint.io.Accessor;
import org.jabberpoint.model.Presentation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Accessor abstract class
 */
@ExtendWith(MockitoExtension.class)
class AccessorTest {

    @Mock
    private Presentation mockPresentation;
    
    @TempDir
    static Path tempDir;

    @Test
    @DisplayName("getDemoAccessor should return a valid Accessor")
    void getDemoAccessorShouldReturnValidAccessor() {
        // Act
        Accessor demoAccessor = Accessor.getDemoAccessor();
        
        // Assert
        assertNotNull(demoAccessor, "Demo accessor should not be null");
        // We can't check the exact type because DemoPresentation is not public,
        // but we can verify it's a valid accessor
        assertInstanceOf(Accessor.class, demoAccessor);
    }
    
    @Test
    @DisplayName("Constructor should initialize without errors")
    void constructorShouldInitializeWithoutErrors() {
        // Create a concrete implementation for testing
        Accessor accessor = new TestAccessor();
        
        // Simply verify no exception is thrown
        assertNotNull(accessor, "Accessor should be initialized");
    }
    
    @Test
    @DisplayName("DEMO_NAME should have correct value")
    void demoNameShouldHaveCorrectValue() {
        assertEquals("Demonstration presentation", Accessor.DEMO_NAME, 
                    "DEMO_NAME constant should have the correct value");
    }
    
    @Test
    @DisplayName("DEFAULT_EXTENSION should have correct value")
    void defaultExtensionShouldHaveCorrectValue() {
        assertEquals(".xml", Accessor.DEFAULT_EXTENSION, 
                    "DEFAULT_EXTENSION constant should have the correct value");
    }
    
    // Concrete implementation of Accessor for testing
    private static class TestAccessor extends Accessor {
        @Override
        public void loadFile(Presentation p, String fn) throws IOException {
            // Test implementation
        }
        
        @Override
        public void saveFile(Presentation p, String fn) throws IOException {
            // Test implementation
        }
    }
}

