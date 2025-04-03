package org.jabberpoint.test;

import org.jabberpoint.src.Style;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TextItemTest {

    private TextItem textItem;
    private final String testText = "Test text for TextItem";
    private final int testLevel = 2;
    
    @BeforeAll
    static void setUpClass() {
        // Initialize Style singleton before all tests
        Style.getInstance();
    }
    
    @BeforeEach
    void setUp() {
        // Create a text item for testing
        textItem = new TextItem(testLevel, testText);
    }
    
    @Test
    @DisplayName("Constructor should initialize TextItem with level and text")
    void constructorShouldInitializeWithLevelAndText() {
        assertEquals(testLevel, textItem.getLevel(), "Level should be initialized correctly");
        assertEquals(testText, textItem.getText(), "Text should be initialized correctly");
    }
    
    @Test
    @DisplayName("getText should return the text content")
    void getTextShouldReturnTextContent() {
        assertEquals(testText, textItem.getText(), "getText should return the correct text");
    }
    
    @Test
    @DisplayName("getLevel should return the item level")
    void getLevelShouldReturnItemLevel() {
        assertEquals(testLevel, textItem.getLevel(), "getLevel should return the correct level");
    }
    
    @Test
    @DisplayName("toString should return text with level information")
    void toStringShouldReturnTextContent() {
        String expected = "TextItem[" + testLevel + "," + testText + "]";
        assertEquals(expected, textItem.toString(), "toString should return the formatted text");
    }
    
    @Test
    @DisplayName("Draw with null Graphics should not throw exception")
    void drawWithNullGraphicsShouldNotThrowException() {
        // We only verify that no exception is thrown
        assertDoesNotThrow(() -> {
            textItem.draw(10, 10, 1.0f, null, Style.getStyle(testLevel), null);
        });
    }
    
    @Test
    @DisplayName("Draw with empty text should not throw exception")
    void drawWithEmptyTextShouldNotThrowException() {
        TextItem emptyItem = new TextItem(1, "");
        assertDoesNotThrow(() -> {
            emptyItem.draw(10, 10, 1.0f, null, Style.getStyle(1), null);
        });
    }
    
    @Test
    @DisplayName("getBoundingBox with null Graphics should return default rectangle")
    void getBoundingBoxWithNullGraphicsShouldReturnDefaultRectangle() {
        // When given null Graphics, should still return a default rectangle
        Rectangle result = textItem.getBoundingBox(null, null, 1.0f, Style.getStyle(testLevel));
        assertNotNull(result, "Bounding box should not be null with null Graphics");
    }
    
    @Test
    @DisplayName("getBoundingBox with empty text should return small rectangle")
    void getBoundingBoxWithEmptyTextShouldReturnSmallRectangle() {
        TextItem emptyItem = new TextItem(1, "");
        Rectangle result = emptyItem.getBoundingBox(null, null, 1.0f, Style.getStyle(1));
        assertNotNull(result, "Bounding box should not be null for empty text");
        // Empty text should have zero or very small width
        assertTrue(result.width <= 10, "Width should be small for empty text");
    }
}
