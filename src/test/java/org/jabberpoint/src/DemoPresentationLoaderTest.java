package org.jabberpoint.src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DemoPresentationLoaderTest {
    private DemoPresentationLoader loader;
    private Presentation presentation;

    @BeforeEach
    void setUp() {
        loader = new DemoPresentationLoader();
        presentation = new Presentation();
    }

    @Test
    void testLoadPresentation() throws Exception {
        loader.loadPresentation(presentation, "demo");
        assertEquals(3, presentation.getSize(), "Should load 3 slides");

        Slide slide = presentation.getSlide(0);
        assertNotNull(slide, "First slide should exist");
        assertEquals("JabberPoint", slide.getTitle(), "First slide should have correct title");
        assertEquals(4, slide.getSlideItems().size(), "First slide should have 4 items");
    }

    @Test
    void testLoadPresentationWithBitmapItem() throws Exception {
        loader.loadPresentation(presentation, "demo");
        Slide slide = presentation.getSlide(1);
        assertTrue(slide.getSlideItem(1) instanceof BitmapItem, "Second item should be a BitmapItem");
        BitmapItem bitmapItem = (BitmapItem) slide.getSlideItem(1);
        assertEquals(2, bitmapItem.getLevel(), "Image item should have level 2");
        assertEquals("JabberPoint.jpg", bitmapItem.getName(), "Image item should have correct name");
    }

    @Test
    void testLoadPresentationWithInvalidFile() {
        assertThrows(Exception.class, () -> {
            loader.loadPresentation(presentation, "nonexistent");
        }, "Should throw exception for invalid file");
    }

    @Test
    void testLoadPresentationWithNullPresentation() {
        assertThrows(NullPointerException.class, () -> {
            loader.loadPresentation(null, "demo");
        }, "Should throw NullPointerException for null presentation");
    }

    @Test
    void testLoadPresentationWithNullFilename() {
        assertThrows(NullPointerException.class, () -> {
            loader.loadPresentation(presentation, null);
        }, "Should throw NullPointerException for null filename");
    }
}
