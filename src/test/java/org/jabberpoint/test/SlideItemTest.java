package org.jabberpoint.test;

import org.jabberpoint.src.SlideItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SlideItem abstract class
 * Using a concrete implementation to test the base functionality
 */
class SlideItemTest {

    /**
     * Concrete implementation of SlideItem for testing
     */
    private static class TestSlideItem extends SlideItem {
        public TestSlideItem(int level) {
            super(level);
        }
        
        public TestSlideItem() {
            super();
        }

        @Override
        public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style) {
            return new Rectangle(0, 0, 100, 100);
        }

        @Override
        public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer) {
            // Mock implementation, does nothing
        }
    }

    @Test
    @DisplayName("Should create SlideItem with specified level")
    void shouldCreateSlideItemWithLevel() {
        // Arrange & Act
        SlideItem item = new TestSlideItem(3);
        
        // Assert
        assertEquals(3, item.getLevel());
    }

    @Test
    @DisplayName("Should create SlideItem with default level 0")
    void shouldCreateSlideItemWithDefaultLevel() {
        // Arrange & Act
        SlideItem item = new TestSlideItem();
        
        // Assert
        assertEquals(0, item.getLevel());
    }

    @Test
    @DisplayName("Should call abstract methods properly")
    void shouldCallAbstractMethodsProperly() {
        // Arrange
        SlideItem item = new TestSlideItem(1);
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        ImageObserver mockObserver = Mockito.mock(ImageObserver.class);
        Style mockStyle = Mockito.mock(Style.class);
        
        // Act
        Rectangle boundingBox = item.getBoundingBox(mockGraphics, mockObserver, 1.0f, mockStyle);
        
        // Assert
        assertNotNull(boundingBox);
        assertEquals(100, boundingBox.width);
        assertEquals(100, boundingBox.height);
        
        // Verify draw doesn't throw exceptions
        assertDoesNotThrow(() -> item.draw(10, 10, 1.0f, mockGraphics, mockStyle, mockObserver));
    }
}
