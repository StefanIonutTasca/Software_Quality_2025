package org.jabberpoint.test;

import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideItem;
import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Slide class
 */
public class SlideTest {
    private Slide testSlide;
    private Graphics mockGraphics;
    private ImageObserver mockObserver;
    private SlideItem mockSlideItem;

    @BeforeEach
    void setUp() {
        testSlide = new Slide();
        mockGraphics = mock(Graphics.class);
        mockObserver = mock(ImageObserver.class);
        mockSlideItem = mock(SlideItem.class);
    }

    @Test
    @DisplayName("Should create empty slide with default values")
    void constructorShouldCreateEmptySlide() {
        // Assert
        assertEquals("", testSlide.getTitle());
        assertEquals(0, testSlide.getSize());
    }

    @Test
    @DisplayName("Should set and get slide title")
    void shouldSetAndGetTitle() {
        // Arrange
        String testTitle = "Test Slide Title";
        
        // Act
        testSlide.setTitle(testTitle);
        
        // Assert
        assertEquals(testTitle, testSlide.getTitle());
    }
    
    @Test
    @DisplayName("Should append and retrieve slide items")
    void shouldAppendAndRetrieveSlideItems() {
        // Arrange
        TextItem item1 = new TextItem(1, "Item 1");
        TextItem item2 = new TextItem(2, "Item 2");
        
        // Act
        testSlide.append(item1);
        testSlide.append(item2);
        
        // Assert
        assertEquals(2, testSlide.getSize());
        assertSame(item1, testSlide.getSlideItems().get(0));
        assertSame(item2, testSlide.getSlideItems().get(1));
    }
    
    @Test
    @DisplayName("Should return correct bounding box size")
    void shouldReturnCorrectBoundingBoxSize() {
        // Arrange
        Rectangle mockRect1 = new Rectangle(0, 0, 100, 50);
        Rectangle mockRect2 = new Rectangle(0, 0, 150, 75);
        
        // Create mock slide items with specific bounding boxes
        SlideItem item1 = mock(SlideItem.class);
        SlideItem item2 = mock(SlideItem.class);
        
        when(item1.getBoundingBox(eq(mockGraphics), eq(mockObserver), anyFloat(), any(Style.class)))
            .thenReturn(mockRect1);
        when(item2.getBoundingBox(eq(mockGraphics), eq(mockObserver), anyFloat(), any(Style.class)))
            .thenReturn(mockRect2);
        
        testSlide.append(item1);
        testSlide.append(item2);
        
        // Act
        Rectangle boundingBox = testSlide.getBoundingBox(mockGraphics, mockObserver);
        
        // Assert
        assertNotNull(boundingBox);
        assertTrue(boundingBox.width >= 150); // Should be at least as wide as the widest item
        assertTrue(boundingBox.height > 0);   // Should have some height
    }
    
    @Test
    @DisplayName("Should draw all slide items")
    void shouldDrawAllSlideItems() {
        // Arrange
        SlideItem item1 = mock(SlideItem.class);
        SlideItem item2 = mock(SlideItem.class);
        testSlide.append(item1);
        testSlide.append(item2);
        
        // Act
        testSlide.draw(mockGraphics, mockObserver);
        
        // Assert - Verify that draw was called on each item
        verify(item1).draw(anyInt(), anyInt(), anyFloat(), eq(mockGraphics), any(Style.class), eq(mockObserver));
        verify(item2).draw(anyInt(), anyInt(), anyFloat(), eq(mockGraphics), any(Style.class), eq(mockObserver));
    }
    
    @Test
    @DisplayName("Should calculate correct scale based on component size")
    void shouldCalculateCorrectScale() {
        // Arrange
        int componentWidth = 800;
        int componentHeight = 600;
        
        // Create a slide with a known bounding box
        final Rectangle mockBoundingBox = new Rectangle(0, 0, 400, 300);
        
        Slide spySlide = spy(new Slide());
        doReturn(mockBoundingBox).when(spySlide).getBoundingBox(any(), any());
        
        // Act
        float scale = spySlide.getScale(componentWidth, componentHeight);
        
        // Assert
        assertTrue(scale > 0, "Scale should be greater than 0");
        assertEquals(Math.min((float)componentWidth/mockBoundingBox.width, 
                              (float)componentHeight/mockBoundingBox.height), 
                    scale, 
                    0.001f, 
                    "Scale calculation should match expected formula");
    }
    
    @Test
    @DisplayName("Draw method should handle empty slides")
    void drawShouldHandleEmptySlides() {
        // Act & Assert - no exceptions should be thrown
        assertDoesNotThrow(() -> testSlide.draw(mockGraphics, mockObserver));
    }
}
