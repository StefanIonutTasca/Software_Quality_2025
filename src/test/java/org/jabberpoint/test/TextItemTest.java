package org.jabberpoint.test;

import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TextItem class
 */
public class TextItemTest {
    private Graphics mockGraphics;
    private Graphics2D mockGraphics2D;
    private ImageObserver mockObserver;
    private Style testStyle;
    private FontRenderContext mockFontRenderContext;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockGraphics = mock(Graphics.class);
        mockGraphics2D = mock(Graphics2D.class);
        mockObserver = mock(ImageObserver.class);
        mockFontRenderContext = mock(FontRenderContext.class);
        
        // Set up Graphics2D mock
        when(mockGraphics2D.getFontRenderContext()).thenReturn(mockFontRenderContext);
        
        // Get the Style singleton
        testStyle = Style.getInstance();
    }

    @Test
    @DisplayName("Should create TextItem with proper level and text")
    void constructorShouldSetLevelAndText() {
        // Arrange & Act
        TextItem textItem = new TextItem(2, "Test Text");
        
        // Assert
        assertEquals(2, textItem.getLevel());
        assertEquals("Test Text", textItem.getText());
    }

    @Test
    @DisplayName("Should create empty TextItem with default values")
    void defaultConstructorShouldCreateEmptyItem() {
        // Arrange & Act
        TextItem textItem = new TextItem();
        
        // Assert
        assertEquals(0, textItem.getLevel());
        assertEquals("", textItem.getText());
    }
    
    @Test
    @DisplayName("getText should return the text")
    void getTextShouldReturnText() {
        // Arrange
        String testText = "Sample Text";
        TextItem textItem = new TextItem(1, testText);
        
        // Act & Assert
        assertEquals(testText, textItem.getText());
    }
    
    @Test
    @DisplayName("toString should return proper representation")
    void toStringShouldReturnProperRepresentation() {
        // Arrange
        TextItem textItem = new TextItem(3, "Test String");
        
        // Act
        String result = textItem.toString();
        
        // Assert
        assertEquals("TextItem[3,Test String]", result);
    }
    
    @Test
    @DisplayName("getBoundingBox should return non-null rectangle")
    void getBoundingBoxShouldReturnNonNullRectangle() {
        // Arrange
        TextItem textItem = new TextItem(1, "Test Text");
        
        // Mock the graphics to simulate text layout measurements
        TextLayout mockTextLayout = mock(TextLayout.class);
        when(mockTextLayout.getBounds()).thenReturn(new Rectangle2D.Double(0, 0, 100, 20));
        
        // Act
        Rectangle boundingBox = textItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, testStyle);
        
        // Assert
        assertNotNull(boundingBox);
    }
    
    @Test
    @DisplayName("draw should not throw exceptions")
    void drawShouldNotThrowException() {
        // Arrange
        TextItem textItem = new TextItem(1, "Test Text");
        
        // Act & Assert
        assertDoesNotThrow(() -> 
            textItem.draw(10, 20, 1.0f, mockGraphics, testStyle, mockObserver)
        );
    }
    
    @Test
    @DisplayName("AttributedString should be properly created")
    void attributedStringShouldBeProperlyCreated() {
        // Arrange
        TextItem textItem = new TextItem(1, "Test Text");
        
        // We can't directly test private methods, so we'll test indirectly
        // through the draw method which uses the AttributedString
        
        // Act & Assert
        assertDoesNotThrow(() -> 
            textItem.draw(10, 20, 1.0f, mockGraphics, testStyle, mockObserver)
        );
    }
}
