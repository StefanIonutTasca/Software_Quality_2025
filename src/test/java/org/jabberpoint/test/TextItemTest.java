package org.jabberpoint.test;

import org.jabberpoint.src.Style;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Graphics2D;
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
    private Style testStyle;
    private FontRenderContext mockFrc;
    private AffineTransform mockTransform;

    @BeforeEach
    void setUp() {
        // Ensure styles are initialized properly
        Style.createStyles();
        
        // Create test TextItem
        textItem = new TextItem(1, "Test Message");
        
        // Create mocks
        mockGraphics = mock(Graphics2D.class);
        mockObserver = mock(ImageObserver.class);
        mockFrc = mock(FontRenderContext.class);
        mockTransform = mock(AffineTransform.class);
        
        // Setup mock behavior
        when(mockGraphics.getFontRenderContext()).thenReturn(mockFrc);
        doReturn(1.0).when(mockTransform).getScaleX();
        doReturn(mockTransform).when(mockGraphics).getTransform();
        
        // Get style for level 1
        testStyle = Style.getStyle(1);
        assertNotNull(testStyle, "Style object should not be null");
        
        // Mock the font behavior
        Font mockFont = mock(Font.class);
        when(mockGraphics.getFont()).thenReturn(mockFont);
    }

    @Test
    @DisplayName("Should create text item with level and text")
    void constructorShouldSetLevelAndText() {
        // Assert
        assertEquals(1, textItem.getLevel());
        assertEquals("Test Message", textItem.getText());
    }

    @Test
    @DisplayName("getBoundingBox should return non-null rectangle")
    void getBoundingBoxShouldReturnNonNullRectangle() {
        try {
            // Since we can't mock private methods without PowerMock, we'll test a simpler case
            // Mock style to return a non-null font to avoid NullPointerException
            Font mockFont = new Font("Arial", Font.PLAIN, 12);
            doReturn(mockFont).when(testStyle).getFont(anyFloat());
            
            // Act - use the real getBoundingBox method
            Rectangle boundingBox = textItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, testStyle);
            
            // Assert
            assertNotNull(boundingBox, "Bounding box should not be null");
        } catch (NullPointerException e) {
            fail("Should not throw NullPointerException: " + e.getMessage());
        }
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
        try {
            // Mock the necessary behavior for draw
            Font mockFont = new Font("Arial", Font.PLAIN, 12);
            doReturn(mockFont).when(testStyle).getFont(anyFloat());
            
            // Act & Assert - should not throw exception
            assertDoesNotThrow(() -> 
                textItem.draw(0, 0, 1.0f, mockGraphics, testStyle, mockObserver)
            );
        } catch (NullPointerException e) {
            fail("Should not throw NullPointerException: " + e.getMessage());
        }
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
