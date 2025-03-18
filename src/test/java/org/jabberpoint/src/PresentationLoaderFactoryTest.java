package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PresentationLoaderFactoryTest {

    @Test
    @DisplayName("Test creating XMLPresentationLoader")
    public void testCreateXMLLoader() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("xml");
        assertNotNull(loader);
        assertTrue(loader instanceof XMLPresentationLoader);
    }

    @Test
    @DisplayName("Test creating DemoPresentationLoader")
    public void testCreateDemoLoader() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("demo");
        assertNotNull(loader);
        assertTrue(loader instanceof DemoPresentationLoader);
    }

    @Test
    @DisplayName("Test case insensitivity for XML loader")
    public void testXMLLoaderCaseInsensitivity() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("XML");
        assertTrue(loader instanceof XMLPresentationLoader);
    }

    @Test
    @DisplayName("Test case insensitivity for Demo loader")
    public void testDemoLoaderCaseInsensitivity() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("DEMO");
        assertTrue(loader instanceof DemoPresentationLoader);
    }

    @Test
    @DisplayName("Test unknown loader type throws exception")
    public void testUnknownLoaderType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PresentationLoaderFactory.createLoader("unknown");
        });

        String expectedMessage = "Unknown loader type";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {"xml", "XML", "Xml", "xMl"})
    @DisplayName("Test different case variations for XML loader")
    public void testXMLLoaderVariations(String type) {
        PresentationLoader loader = PresentationLoaderFactory.createLoader(type);
        assertTrue(loader instanceof XMLPresentationLoader);
    }

    @ParameterizedTest
    @ValueSource(strings = {"demo", "DEMO", "Demo", "dEmO"})
    @DisplayName("Test different case variations for Demo loader")
    public void testDemoLoaderVariations(String type) {
        PresentationLoader loader = PresentationLoaderFactory.createLoader(type);
        assertTrue(loader instanceof DemoPresentationLoader);
    }
}
