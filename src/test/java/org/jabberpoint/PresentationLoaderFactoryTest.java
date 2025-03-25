package org.jabberpoint;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.jabberpoint.PresentationLoader;
import org.jabberpoint.PresentationLoaderFactory;
import org.jabberpoint.XMLPresentationLoader;
import org.jabberpoint.DemoPresentationLoader;

public class PresentationLoaderFactoryTest {
    @Test
    void createLoaderForXMLFile() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("test.xml");
        assertTrue(loader instanceof XMLPresentationLoader);
    }

    @Test
    void createLoaderForXMLFileWithUpperCase() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("test.XML");
        assertTrue(loader instanceof XMLPresentationLoader);
    }

    @Test
    void createLoaderForDemoFile() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("demo");
        assertTrue(loader instanceof DemoPresentationLoader);
    }

    @Test
    void createLoaderForDemoFileWithUpperCase() {
        PresentationLoader loader = PresentationLoaderFactory.createLoader("DEMO");
        assertTrue(loader instanceof DemoPresentationLoader);
    }

    @Test
    void createLoaderForUnknownExtension() {
        assertThrows(IllegalArgumentException.class, () -> {
            PresentationLoaderFactory.createLoader("test.unknown");
        });
    }

    @Test
    void createLoaderForNullFilename() {
        assertThrows(IllegalArgumentException.class, () -> {
            PresentationLoaderFactory.createLoader(null);
        });
    }
}


