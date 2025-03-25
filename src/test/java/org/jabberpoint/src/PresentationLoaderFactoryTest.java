package org.jabberpoint.src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PresentationLoaderFactoryTest {

    @Test
    void testGetLoaderForXMLFile() {
        PresentationLoader loader = PresentationLoaderFactory.getLoader("test.xml");
        assertNotNull(loader, "Loader should not be null");
        assertTrue(loader instanceof XMLPresentationLoader, "Should return XMLPresentationLoader for XML file");
    }

    @Test
    void testGetLoaderForUppercaseXMLFile() {
        PresentationLoader loader = PresentationLoaderFactory.getLoader("TEST.XML");
        assertNotNull(loader, "Loader should not be null");
        assertTrue(loader instanceof XMLPresentationLoader, "Should return XMLPresentationLoader for uppercase XML file");
    }

    @Test
    void testGetLoaderForDemoPresentation() {
        PresentationLoader loader = PresentationLoaderFactory.getLoader("demo");
        assertNotNull(loader, "Loader should not be null");
        assertTrue(loader instanceof DemoPresentationLoader, "Should return DemoPresentationLoader for demo");
    }

    @Test
    void testGetLoaderForUppercaseDemo() {
        PresentationLoader loader = PresentationLoaderFactory.getLoader("DEMO");
        assertNotNull(loader, "Loader should not be null");
        assertTrue(loader instanceof DemoPresentationLoader, "Should return DemoPresentationLoader for uppercase demo");
    }

    @Test
    void testGetLoaderForNullFilename() {
        assertThrows(IllegalArgumentException.class, () -> {
            PresentationLoaderFactory.getLoader(null);
        }, "Should throw IllegalArgumentException for null filename");
    }

    @Test
    void testGetLoaderForEmptyFilename() {
        assertThrows(IllegalArgumentException.class, () -> {
            PresentationLoaderFactory.getLoader("");
        }, "Should throw IllegalArgumentException for empty filename");
    }
}
