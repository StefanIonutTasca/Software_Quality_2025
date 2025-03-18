package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for the PresentationLoaderFactory class
 */
public class PresentationLoaderFactoryTest {

    @Test
    @DisplayName("Test that XML loader is created correctly")
    public void testCreateXmlLoader() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("xml");
        assertNotNull(loader, "Loader should not be null");
        assertTrue(loader instanceof XMLPresentationLoader, "Loader should be an XMLPresentationLoader");
    }
    
    @Test
    @DisplayName("Test that Demo loader is created correctly")
    public void testCreateDemoLoader() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("demo");
        assertNotNull(loader, "Loader should not be null");
        assertTrue(loader instanceof DemoPresentationLoader, "Loader should be a DemoPresentationLoader");
    }
    
    @Test
    @DisplayName("Test that factory is case insensitive")
    public void testCaseInsensitivity() {
        PresentationLoader loader1 = PresentationLoaderFactory.createLoader("XML");
        PresentationLoader loader2 = PresentationLoaderFactory.createLoader("Demo");
        
        assertTrue(loader1 instanceof XMLPresentationLoader, "Upper case XML should create XMLPresentationLoader");
        assertTrue(loader2 instanceof DemoPresentationLoader, "Upper case Demo should create DemoPresentationLoader");
    }
    
    @Test
    @DisplayName("Test that unknown type throws IllegalArgumentException")
    public void testUnknownType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PresentationLoaderFactory.createLoader("unknown");
        });
        
        String expectedMessage = "Unknown presentation loader type";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should mention unknown type");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"xml", "XML", "Xml", "xMl"})
    @DisplayName("Test different case variations for XML loader")
    public void testXmlCaseVariations(String type) {
        PresentationLoader loader = PresentationLoaderFactory.createLoader(type);
        assertTrue(loader instanceof XMLPresentationLoader, "Should create XMLPresentationLoader regardless of case");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"demo", "DEMO", "Demo", "dEMo"})
    @DisplayName("Test different case variations for Demo loader")
    public void testDemoCaseVariations(String type) {
        PresentationLoader loader = PresentationLoaderFactory.createLoader(type);
        assertTrue(loader instanceof DemoPresentationLoader, "Should create DemoPresentationLoader regardless of case");
    }
}
