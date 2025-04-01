package org.jabberpoint.test;

import org.jabberpoint.src.Accessor;
import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DemoPresentation class
 * 
 * Note: Using reflection to access package-private class
 */
class DemoPresentationTest {

    private Accessor demoPresentation;
    private Presentation presentation;

    @BeforeEach
    void setUp() throws Exception {
        // Create DemoPresentation using reflection since it's package-private
        Class<?> demoPresentationClass = Class.forName("org.jabberpoint.src.DemoPresentation");
        demoPresentation = (Accessor) demoPresentationClass.getDeclaredConstructor().newInstance();
        
        presentation = new Presentation();
    }

    @Test
    @DisplayName("loadFile should create a presentation with demo content")
    void loadFileShouldCreatePresentationWithDemoContent() throws IOException {
        // Act
        demoPresentation.loadFile(presentation, "");

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
    @DisplayName("saveFile should throw IllegalStateException")
    void saveFileShouldThrowIllegalStateException() {
        // Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> demoPresentation.saveFile(presentation, ""),
            "saveFile should throw IllegalStateException");
        
        assertEquals("Save As->Demo! called", exception.getMessage(), 
            "Exception message should be 'Save As->Demo! called'");
    }
}
