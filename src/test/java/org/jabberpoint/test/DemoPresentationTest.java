package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.PresentationLoader;
import org.jabberpoint.src.PresentationLoaderFactory;
import org.jabberpoint.src.Slide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Demo presentation loader functionality
 */
class DemoPresentationTest {

    private PresentationLoader demoLoader;
    private Presentation presentation;

    @BeforeEach
    void setUp() throws Exception {
        // Get the demo loader using the factory
        demoLoader = PresentationLoaderFactory.createLoader("demo");
        presentation = new Presentation();
    }

    @Test
    @DisplayName("loadPresentation should create a presentation with demo content")
    void loadPresentationShouldCreatePresentationWithDemoContent() throws IOException {
        // Act
        demoLoader.loadPresentation(presentation, "");

        // Assert
        assertEquals("Demo Presentation", presentation.getTitle(), "Presentation title should be set to 'Demo Presentation'");
        assertEquals(3, presentation.getSize(), "Presentation should have 3 slides");

        // Check slide 1
        Slide slide1 = presentation.getSlide(0);
        assertEquals("JabberPoint", slide1.getTitle(), "First slide title should be 'JabberPoint'");
        assertTrue(slide1.getSlideItems().size() > 0, "First slide should have items");

        // Check slide 2
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Demonstration of levels and stijlen", slide2.getTitle(), 
                "Second slide title should be 'Demonstration of levels and stijlen'");
        assertTrue(slide2.getSlideItems().size() > 0, "Second slide should have items");

        // Check slide 3
        Slide slide3 = presentation.getSlide(2);
        assertEquals("The third slide", slide3.getTitle(), "Third slide title should be 'The third slide'");
        assertTrue(slide3.getSlideItems().size() > 0, "Third slide should have items");
        
        // Verify BitmapItem on slide 3
        boolean hasBitmapItem = false;
        for (int i = 0; i < slide3.getSlideItems().size(); i++) {
            if (slide3.getSlideItems().get(i) instanceof BitmapItem) {
                hasBitmapItem = true;
                break;
            }
        }
        assertTrue(hasBitmapItem, "Third slide should contain a BitmapItem");
    }

    @Test
    @DisplayName("savePresentation should throw IllegalStateException")
    void savePresentationShouldThrowIllegalStateException() {
        // Assert that saving with the demo loader throws an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> demoLoader.savePresentation(presentation, ""),
            "savePresentation should throw IllegalStateException");
        
        assertEquals("Save As->Demo! called", exception.getMessage(), 
            "Exception message should be 'Save As->Demo! called'");
    }
}
