package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DemoPresentationLoaderTest {

    private DemoPresentationLoader loader;
    private Presentation presentation;
    
    @BeforeEach
    public void setUp() {
        loader = new DemoPresentationLoader();
        presentation = new Presentation();
    }

    @Test
    @DisplayName("Test demo presentation structure")
    public void testDemoPresentation() throws IOException {
        // Load the demo presentation
        loader.loadPresentation(presentation, "dummy-filename");
        
        // Verify presentation title
        assertEquals("Demo Presentation", presentation.getTitle());
        
        // Verify number of slides
        assertEquals(3, presentation.getSize());
        
        // Verify first slide
        Slide slide1 = presentation.getSlide(0);
        assertEquals("JabberPoint", slide1.getTitle());
        assertNotNull(slide1);
        
        // Verify second slide
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Demonstration of levels and styles", slide2.getTitle());
        assertNotNull(slide2);
        
        // Verify third slide
        Slide slide3 = presentation.getSlide(2);
        assertEquals("The third slide", slide3.getTitle());
        assertNotNull(slide3);
        
        // Verify at least one item contains a BitmapItem
        boolean containsBitmapItem = false;
        for (int i = 0; i < slide3.getSize(); i++) {
            if (slide3.getSlideItem(i) instanceof BitmapItem) {
                containsBitmapItem = true;
                break;
            }
        }
        assertTrue(containsBitmapItem, "The third slide should contain a BitmapItem");
    }
    
    @Test
    @DisplayName("Test that source parameter is ignored")
    public void testSourceParameterIsIgnored() throws IOException {
        // Create two presentations with different source parameters
        Presentation presentation1 = new Presentation();
        Presentation presentation2 = new Presentation();
        
        loader.loadPresentation(presentation1, "source1");
        loader.loadPresentation(presentation2, "source2");
        
        // Both should have same title and number of slides
        assertEquals(presentation1.getTitle(), presentation2.getTitle());
        assertEquals(presentation1.getSize(), presentation2.getSize());
        
        // Compare content of first slides
        assertEquals(presentation1.getSlide(0).getTitle(), presentation2.getSlide(0).getTitle());
    }
    
    @Test
    @DisplayName("Test slide contents and levels")
    public void testSlideContentsAndLevels() throws IOException {
        loader.loadPresentation(presentation, "");
        
        // Test level 1 items exist in slide 1
        Slide slide1 = presentation.getSlide(0);
        boolean hasLevel1 = false;
        for (int i = 0; i < slide1.getSize(); i++) {
            SlideItem item = slide1.getSlideItem(i);
            if (item.getLevel() == 1) {
                hasLevel1 = true;
                break;
            }
        }
        assertTrue(hasLevel1, "Slide 1 should have level 1 items");
        
        // Test multiple levels exist in slide 2
        Slide slide2 = presentation.getSlide(1);
        boolean hasMultipleLevels = false;
        int previousLevel = -1;
        for (int i = 0; i < slide2.getSize(); i++) {
            int currentLevel = slide2.getSlideItem(i).getLevel();
            if (previousLevel != -1 && currentLevel != previousLevel) {
                hasMultipleLevels = true;
                break;
            }
            previousLevel = currentLevel;
        }
        assertTrue(hasMultipleLevels, "Slide 2 should have multiple level items");
    }
}
