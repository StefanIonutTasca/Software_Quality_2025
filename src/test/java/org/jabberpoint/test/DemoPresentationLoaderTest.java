package org.jabberpoint.src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DemoPresentationLoader
 */
class DemoPresentationLoaderTest {

    private DemoPresentationLoader loader;
    private Presentation presentation;

    @BeforeEach
    void setUp() {
        loader = new DemoPresentationLoader();
        presentation = new Presentation();
    }

    @Test
    @DisplayName("Should load demo presentation correctly")
    void loadPresentationShouldCreateDemoPresentation() throws IOException {
        // Act
        loader.loadPresentation(presentation, null);

        // Assert
        assertEquals("Demo Presentation", presentation.getTitle(), "Presentation title should be 'Demo Presentation'");
        assertEquals(3, presentation.getSize(), "Presentation should have 3 slides");
    }

    @Test
    @DisplayName("Should create first slide with correct title and items")
    void loadPresentationShouldCreateFirstSlideCorrectly() throws IOException {
        // Act
        loader.loadPresentation(presentation, "");

        // Assert
        Slide firstSlide = presentation.getSlide(0);
        assertNotNull(firstSlide, "First slide should exist");
        assertEquals("JabberPoint", firstSlide.getTitle(), "First slide title should be 'JabberPoint'");
        assertTrue(firstSlide.getSize() >= 10, "First slide should have at least 10 items");
    }

    @Test
    @DisplayName("Should create second slide with correct title and items")
    void loadPresentationShouldCreateSecondSlideCorrectly() throws IOException {
        // Act
        loader.loadPresentation(presentation, "");

        // Assert
        Slide secondSlide = presentation.getSlide(1);
        assertNotNull(secondSlide, "Second slide should exist");
        assertEquals("Demonstration of levels and styles", secondSlide.getTitle(), 
                "Second slide title should be 'Demonstration of levels and styles'");
        assertEquals(7, secondSlide.getSize(), "Second slide should have 7 items");
    }

    @Test
    @DisplayName("Should create third slide with correct title and items")
    void loadPresentationShouldCreateThirdSlideCorrectly() throws IOException {
        // Act
        loader.loadPresentation(presentation, "");

        // Assert
        Slide thirdSlide = presentation.getSlide(2);
        assertNotNull(thirdSlide, "Third slide should exist");
        assertEquals("The third slide", thirdSlide.getTitle(), "Third slide title should be 'The third slide'");
        
        // Check for BitmapItem presence
        boolean hasBitmapItem = false;
        for (int i = 0; i < thirdSlide.getSize(); i++) {
            if (thirdSlide.getSlideItem(i) instanceof BitmapItem) {
                hasBitmapItem = true;
                BitmapItem bitmapItem = (BitmapItem) thirdSlide.getSlideItem(i);
                assertEquals("JabberPoint.jpg", bitmapItem.getImageName(), 
                        "The bitmap item should have 'JabberPoint.jpg' as image name");
                break;
            }
        }
        assertTrue(hasBitmapItem, "Third slide should contain a bitmap item");
    }

    @Test
    @DisplayName("Should handle any source parameter without errors")
    void loadPresentationShouldHandleAnySourceParameterWithoutErrors() {
        // Arrange - various possible source parameters
        String[] sources = {null, "", "dummy", "test.xml", "demo"};
        
        // Act & Assert - none of these should throw exceptions
        for (String source : sources) {
            assertDoesNotThrow(() -> loader.loadPresentation(new Presentation(), source),
                    "Should not throw exception for source: " + source);
        }
    }
    
    @Test
    @DisplayName("Should create consistent demo presentation on multiple calls")
    void loadPresentationShouldCreateConsistentDemoPresentation() throws IOException {
        // Arrange
        Presentation presentation1 = new Presentation();
        Presentation presentation2 = new Presentation();
        
        // Act
        loader.loadPresentation(presentation1, "");
        loader.loadPresentation(presentation2, "different_source");
        
        // Assert - both presentations should be identical
        assertEquals(presentation1.getTitle(), presentation2.getTitle(), "Titles should be identical");
        assertEquals(presentation1.getSize(), presentation2.getSize(), "Number of slides should be identical");
        
        // Compare slides
        for (int i = 0; i < presentation1.getSize(); i++) {
            Slide slide1 = presentation1.getSlide(i);
            Slide slide2 = presentation2.getSlide(i);
            
            assertEquals(slide1.getTitle(), slide2.getTitle(), 
                    "Slide " + i + " titles should be identical");
            assertEquals(slide1.getSize(), slide2.getSize(), 
                    "Slide " + i + " should have same number of items");
        }
    }
}
