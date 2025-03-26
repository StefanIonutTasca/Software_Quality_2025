package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.SlideItem;
import org.jabberpoint.src.SlideItemFactory;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SlideItemFactory class
 */
public class SlideItemFactoryTest {
    
    @Test
    @DisplayName("Should create TextItem when type is 'text'")
    void shouldCreateTextItemWhenTypeIsText() {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem("text", 2, "Test content");
        
        // Assert
        assertTrue(item instanceof TextItem);
        assertEquals(2, item.getLevel());
        assertEquals("Test content", ((TextItem) item).getText());
    }
    
    @Test
    @DisplayName("Should create BitmapItem when type is 'image'")
    void shouldCreateBitmapItemWhenTypeIsImage() {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem("image", 3, "test.jpg");
        
        // Assert
        assertTrue(item instanceof BitmapItem);
        assertEquals(3, item.getLevel());
        assertEquals("test.jpg", ((BitmapItem) item).getName());
    }
    
    @Test
    @DisplayName("Should be case-insensitive for type parameter")
    void shouldBeCaseInsensitiveForTypeParameter() {
        // Act
        SlideItem textItem = SlideItemFactory.createSlideItem("TEXT", 1, "Test");
        SlideItem imageItem = SlideItemFactory.createSlideItem("IMAGE", 1, "test.jpg");
        
        // Assert
        assertTrue(textItem instanceof TextItem);
        assertTrue(imageItem instanceof BitmapItem);
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for unknown type")
    void shouldThrowIllegalArgumentExceptionForUnknownType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> SlideItemFactory.createSlideItem("unknown", 1, "content")
        );
        
        assertTrue(exception.getMessage().contains("Unknown slide item type"));
    }
}
