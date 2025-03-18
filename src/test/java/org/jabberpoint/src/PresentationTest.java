package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PresentationTest {

    private org.jabberpoint.src.Presentation presentation;
    
    @BeforeEach
    public void setUp() {
        presentation = new org.jabberpoint.src.Presentation();
        presentation.setTitle("Test Presentation");
    }
    
    @Test
    public void testGetTitle() {
        assertEquals("Test Presentation", presentation.getTitle());
    }
    
    @Test
    public void testSlideManagement() {
        int initialCount = presentation.getSize();
        org.jabberpoint.src.Slide slide = new org.jabberpoint.src.Slide();
        slide.setTitle("New Slide");
        presentation.append(slide);
        assertEquals(initialCount + 1, presentation.getSize());
    }
}
