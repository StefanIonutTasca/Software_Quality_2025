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
    private Rectangle testArea;

    @BeforeEach
    void setUp() {
        testSlide = new Slide();
        mockGraphics = mock(Graphics.class);
        mockObserver = mock(ImageObserver.class);
        testArea = new Rectangle(0, 0, 800, 600);
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
    @DisplayName("Should calculate correct slide size")
    void shouldCalculateCorrectSlideSize() {
        // Arrange
        testSlide.append(new TextItem(1, "Item 1"));
        testSlide.append(new TextItem(2, "Item 2"));
        testSlide.append(new TextItem(1, "Item 3"));
        
        // Act & Assert
        assertEquals(3, testSlide.getSize());
    }
    
    @Test
    @DisplayName("Should retrieve specific slide item")
    void shouldRetrieveSpecificSlideItem() {
        // Arrange
        TextItem item1 = new TextItem(1, "Item 1");
        TextItem item2 = new TextItem(2, "Item 2");
        testSlide.append(item1);
        testSlide.append(item2);
        
        // Act & Assert
        assertSame(item1, testSlide.getSlideItem(0));
        assertSame(item2, testSlide.getSlideItem(1));
    }
    
    @Test
    @DisplayName("Should append text with level")
    void shouldAppendTextWithLevel() {
        // Arrange & Act
        testSlide.append(1, "Test Message");
        
        // Assert
        assertEquals(1, testSlide.getSize());
        SlideItem item = testSlide.getSlideItem(0);
        assertTrue(item instanceof TextItem);
        assertEquals(1, item.getLevel());
        assertEquals("Test Message", ((TextItem)item).getText());
    }
    
    @Test
    @DisplayName("Should draw all slide items")
    void shouldDrawAllSlideItems() {
        // Arrange
        testSlide.setTitle("Test Title");
        SlideItem item1 = spy(new TextItem(1, "Item 1"));
        SlideItem item2 = spy(new TextItem(2, "Item 2"));
        testSlide.append(item1);
        testSlide.append(item2);
        
        // Act
        testSlide.draw(mockGraphics, testArea, mockObserver);
        
        // Assert
        // Verify the title and both items were drawn
        verify(item1).draw(anyInt(), anyInt(), anyFloat(), eq(mockGraphics), any(Style.class), eq(mockObserver));
        verify(item2).draw(anyInt(), anyInt(), anyFloat(), eq(mockGraphics), any(Style.class), eq(mockObserver));
    }
    
    @Test
    @DisplayName("Draw method should handle empty slides")
    void drawShouldHandleEmptySlides() {
        // Arrange
        testSlide.setTitle("Empty Slide");
        
        // Act & Assert - no exceptions should be thrown
        assertDoesNotThrow(() -> testSlide.draw(mockGraphics, testArea, mockObserver));
    }
}
