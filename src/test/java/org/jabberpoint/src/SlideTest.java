package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SlideTest {

    private org.jabberpoint.src.Slide slide;
    
    @BeforeEach
    public void setUp() {
        slide = new org.jabberpoint.src.Slide();
        slide.setTitle("Test Slide");
    }
    
    @Test
    public void testGetTitle() {
        assertEquals("Test Slide", slide.getTitle());
    }
    
    @Test
    public void testAppendText() {
        int initialSize = slide.getSize();
        slide.append(1, "Test Item");
        assertEquals(initialSize + 1, slide.getSize());
    }
}
