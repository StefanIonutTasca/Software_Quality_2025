package org.jabberpoint.test;

import org.jabberpoint.src.Style;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TextItem class
 */
public class TextItemTest {
    private TextItem textItem;
    private Graphics2D mockGraphics;
    private ImageObserver mockObserver;
    private FontRenderContext mockFrc;
    private AffineTransform mockTransform;
    private FontMetrics mockFontMetrics;
    private Font testFont;

    @BeforeEach
    void setUp() {
        // Ensure styles are initialized properly
        Style.createStyles();
        
        // Create test TextItem
        textItem = new TextItem(1, "Test Message");
        
        // Create real test font
        testFont = new Font("Arial", Font.PLAIN, 12);
        
        // Create mocks
        mockGraphics = mock(Graphics2D.class);
        mockObserver = mock(ImageObserver.class);
        mockFrc = mock(FontRenderContext.class);
        mockTransform = mock(AffineTransform.class);
        mockFontMetrics = mock(FontMetrics.class);
        
        // Setup mock behavior for font metrics
        when(mockFontMetrics.stringWidth(anyString())).thenReturn(100);
        when(mockFontMetrics.getHeight()).thenReturn(20);
        when(mockFontMetrics.getAscent()).thenReturn(15);
        
        // Set up the graphics mock to return our font metrics
        when(mockGraphics.getFontMetrics(any(Font.class))).thenReturn(mockFontMetrics);
        when(mockGraphics.getFontRenderContext()).thenReturn(mockFrc);
        
        // Set up the transform
        doReturn(1.0).when(mockTransform).getScaleX();
        doReturn(1.0).when(mockTransform).getScaleY();
        doReturn(0.0).when(mockTransform).getTranslateX();
        doReturn(0.0).when(mockTransform).getTranslateY();
        doReturn(mockTransform).when(mockGraphics).getTransform();
    }

    @Test
    @DisplayName("Should create text item with level and text")
    void constructorShouldSetLevelAndText() {
        assertEquals(1, textItem.getLevel());
        assertEquals("Test Message", textItem.getText());
    }

    @Test
    @DisplayName("getBoundingBox should return non-null rectangle")
    void getBoundingBoxShouldReturnNonNullRectangle() {
        // Create a real Style with a real Font for testing
        Style testStyle = Style.getStyle(1);
        
        // Replace the font in the style to avoid NPE
        // We're using reflection to set the font directly
        try {
            java.lang.reflect.Field fontField = Style.class.getDeclaredField("font");
            fontField.setAccessible(true);
            fontField.set(testStyle, testFont);
        } catch (Exception e) {
            fail("Failed to set font field: " + e.getMessage());
        }
        
        // Act - use the real getBoundingBox method
        Rectangle boundingBox = textItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, testStyle);
        
        // Assert
        assertNotNull(boundingBox, "Bounding box should not be null");
    }

    @Test
    @DisplayName("getText should return the text content")
    void getTextShouldReturnTextContent() {
        // Act
        String text = textItem.getText();
        
        // Assert
        assertEquals("Test Message", text);
    }
    
    @Test
    @DisplayName("draw should not throw exception")
    void drawShouldNotThrowException() {
        // Create a real Style with a real Font for testing
        Style testStyle = Style.getStyle(1);
        
        // Replace the font in the style to avoid NPE
        // We're using reflection to set the font directly
        try {
            java.lang.reflect.Field fontField = Style.class.getDeclaredField("font");
            fontField.setAccessible(true);
            fontField.set(testStyle, testFont);
        } catch (Exception e) {
            fail("Failed to set font field: " + e.getMessage());
        }
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> 
            textItem.draw(0, 0, 1.0f, mockGraphics, testStyle, mockObserver)
        );
    }
    
    @Test
    @DisplayName("toString should return proper representation")
    void toStringShouldReturnProperRepresentation() {
        // Arrange
        TextItem item = new TextItem(2, "Test");
        
        // Act
        String result = item.toString();
        
        // Assert
        assertEquals("TextItem[2,Test]", result);
    }
}
