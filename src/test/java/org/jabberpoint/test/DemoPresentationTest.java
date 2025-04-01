package org.jabberpoint.test;

import org.jabberpoint.src.DemoPresentation;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DemoPresentation class
 */
class DemoPresentationTest {

    private DemoPresentation demoPresentation;
    private Presentation presentation;

    @BeforeEach
    void setUp() {
        demoPresentation = new DemoPresentation();
        presentation = new Presentation();
    }

    @Test
    @DisplayName("Should load demo presentation correctly")
    void shouldLoadDemoPresentationCorrectly() {
        // Act
        demoPresentation.loadFile(presentation, null); // filename is unused
        
        // Assert
        assertEquals("Demo Presentation", presentation.getTitle(), "Presentation title should be 'Demo Presentation'");
        assertEquals(3, presentation.getSize(), "Should have 3 slides");
        
        // Check first slide
        Slide slide1 = presentation.getSlide(0);
        assertEquals("JabberPoint", slide1.getTitle());
        assertTrue(slide1.getSize() > 0, "First slide should have items");
        
        // Check second slide
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Demonstration of levels and stijlen", slide2.getTitle());
        assertTrue(slide2.getSize() > 0, "Second slide should have items");
        
        // Check third slide
        Slide slide3 = presentation.getSlide(2);
        assertEquals("The third slide", slide3.getTitle());
        assertTrue(slide3.getSize() > 0, "Third slide should have items");
    }
    
    @Test
    @DisplayName("Should throw IllegalStateException when trying to save demo")
    void shouldThrowIllegalStateExceptionWhenTryingToSave() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> demoPresentation.saveFile(presentation, "any-file.xml")
        );
        
        assertEquals("Save As->Demo! called", exception.getMessage(), 
            "Exception should have the correct message");
    }
    
    @Test
    @DisplayName("Should load demo with expected content on each slide")
    void shouldLoadDemoWithExpectedContentOnEachSlide() {
        // Act
        demoPresentation.loadFile(presentation, null);
        
        // Assert - check specific content on slides
        Slide slide1 = presentation.getSlide(0);
        String slide1Item = slide1.getSlideItem(0).toString();
        assertTrue(slide1Item.contains("The Java Presentation Tool"), 
            "First slide should contain intro text");
        
        Slide slide2 = presentation.getSlide(1);
        String slide2Level1 = slide2.getSlideItem(0).toString();
        assertTrue(slide2Level1.contains("Level 1"), 
            "Second slide should demonstrate levels");
        
        Slide slide3 = presentation.getSlide(2);
        String slide3LastTextItem = slide3.getSlideItem(3).toString();
        assertTrue(slide3LastTextItem.contains("This is the end"), 
            "Third slide should have ending message");
    }
}
