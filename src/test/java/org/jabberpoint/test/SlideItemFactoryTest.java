package org.jabberpoint.test;

import org.jabberpoint.model.BitmapItem;
import org.jabberpoint.model.SlideItem;
import org.jabberpoint.model.SlideItemFactory;
import org.jabberpoint.model.TextItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SlideItemFactory class
 */
class SlideItemFactoryTest {

    @Test
    @DisplayName("Should create TextItem with correct parameters")
    void createSlideItemShouldCreateTextItem() {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem("text", 2, "Test Content");
        
        // Assert
        assertTrue(item instanceof TextItem, "Created item should be a TextItem");
        assertEquals(2, item.getLevel(), "Level should be set correctly");
        assertEquals("Test Content", ((TextItem)item).getText(), "Content should be set correctly");
    }
    
    @Test
    @DisplayName("Should create BitmapItem with correct parameters")
    void createSlideItemShouldCreateBitmapItem() {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem("image", 3, "test.jpg");
        
        // Assert
        assertTrue(item instanceof BitmapItem, "Created item should be a BitmapItem");
        assertEquals(3, item.getLevel(), "Level should be set correctly");
        assertEquals("test.jpg", ((BitmapItem)item).getImageName(), "Image name should be set correctly");
    }
    
    @ParameterizedTest
    @DisplayName("Should be case insensitive for item types")
    @ValueSource(strings = {"TEXT", "Text", "text", "tExT"})
    void createSlideItemShouldBeCaseInsensitiveForText(String textType) {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem(textType, 1, "Test");
        
        // Assert
        assertTrue(item instanceof TextItem, "Created item should be a TextItem regardless of case");
    }
    
    @ParameterizedTest
    @DisplayName("Should be case insensitive for image types")
    @ValueSource(strings = {"IMAGE", "Image", "image", "iMaGe"})
    void createSlideItemShouldBeCaseInsensitiveForImage(String imageType) {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem(imageType, 1, "test.jpg");
        
        // Assert
        assertTrue(item instanceof BitmapItem, "Created item should be a BitmapItem regardless of case");
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for unknown type")
    void createSlideItemShouldThrowExceptionForUnknownType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> SlideItemFactory.createSlideItem("unknown", 1, "content"),
            "Should throw IllegalArgumentException for unknown type"
        );
        
        assertTrue(exception.getMessage().contains("Unknown slide item type"), 
            "Exception message should mention unknown type");
    }
    
    @ParameterizedTest
    @DisplayName("Should handle various indentation levels")
    @CsvSource({
        "0, text, Zero level",
        "1, text, First level",
        "5, text, Fifth level", 
        "10, text, Tenth level"
    })
    void createSlideItemShouldHandleVariousLevels(int level, String type, String content) {
        // Act
        SlideItem item = SlideItemFactory.createSlideItem(type, level, content);
        
        // Assert
        assertEquals(level, item.getLevel(), "Level should be set correctly");
    }
}

