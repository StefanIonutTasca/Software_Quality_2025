package org.jabberpoint.test;

import org.jabberpoint.src.Style;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.ImageObserver;
import java.text.AttributedString;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TextItemTest {

    private TextItem textItem;
    private String testText = "Test text for TextItem";
    private int testLevel = 2;

    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private Graphics2D mockGraphics2D;
    
    @Mock
    private ImageObserver mockObserver;
    
    @Mock
    private FontRenderContext mockFontRenderContext;
    
    @Mock
    private LineBreakMeasurer mockMeasurer;
    
    @Mock
    private TextLayout mockTextLayout;
    
    // We'll use a real Style instance instead of a mock to avoid direct field access
    private Style realStyle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a text item with level and text
        textItem = new TextItem(testLevel, testText);
        
        // Initialize Style
        Style.createStyles();
        realStyle = Style.getStyle(testLevel);
        
        // Mock Graphics and Graphics2D behavior - properly setup mock to return Graphics2D
        when(mockGraphics.create()).thenReturn(mockGraphics2D);
        when(mockGraphics2D.getFontRenderContext()).thenReturn(mockFontRenderContext);
    }

    @Test
    @DisplayName("Constructor should initialize TextItem with level and text")
    void constructorShouldInitializeWithLevelAndText() {
        // Test constructor with parameters
        assertEquals(testLevel, textItem.getLevel(), "Level should be initialized correctly");
        assertEquals(testText, textItem.getText(), "Text should be initialized correctly");
        
        // Test default constructor
        TextItem defaultItem = new TextItem();
        assertEquals(0, defaultItem.getLevel(), "Default level should be 0");
        assertEquals("No Text Given", defaultItem.getText(), "Default text should be 'No Text Given'");
    }

    @Test
    @DisplayName("getText should return the text or empty string if null")
    void getTextShouldReturnTextOrEmptyString() {
        // With normal text
        assertEquals(testText, textItem.getText(), "getText should return the text");
        
        // Test with null text using reflection
        try {
            java.lang.reflect.Field textField = TextItem.class.getDeclaredField("text");
            textField.setAccessible(true);
            textField.set(textItem, null);
            
            assertEquals("", textItem.getText(), "getText should return empty string when text is null");
        } catch (Exception e) {
            fail("Failed to set text field to null: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("getAttributedString should return AttributedString with style font")
    void getAttributedStringShouldReturnAttributedStringWithStyleFont() {
        float scale = 1.5f;
        AttributedString result = textItem.getAttributedString(realStyle, scale);
        
        assertNotNull(result, "AttributedString should not be null");
    }
    
    @Test
    @DisplayName("getBoundingBox should calculate correct dimensions")
    void getBoundingBoxShouldCalculateCorrectDimensions() {
        // Set up minimal test environment
        float scale = 1.0f;
        Rectangle boundingBox = textItem.getBoundingBox(mockGraphics, mockObserver, scale, realStyle);
        
        // Basic assertions without relying on internal implementation details
        assertNotNull(boundingBox, "Bounding box should not be null");
        assertTrue(boundingBox.width > 0, "Width should be positive");
    }
    
    @Test
    @DisplayName("draw should not draw anything if text is empty")
    void drawShouldNotDrawIfTextIsEmpty() {
        // Create text item with empty text
        TextItem emptyItem = new TextItem(1, "");
        
        // Call draw
        emptyItem.draw(10, 10, 1.0f, mockGraphics, realStyle, mockObserver);
        
        // Verify no drawing operations were performed
        verify(mockGraphics, never()).create();
    }
    
    @Test
    @DisplayName("draw should draw text with proper styling")
    void drawShouldDrawTextWithProperStyling() {
        // Mock Graphics2D casting
        when(mockGraphics.create()).thenReturn(mockGraphics2D);
        
        // Call the draw method
        int x = 5, y = 10;
        float scale = 1.0f;
        textItem.draw(x, y, scale, mockGraphics, realStyle, mockObserver);
        
        // Verify the graphics context was created
        verify(mockGraphics).create();
        
        // We can't verify style-dependent operations without accessing fields directly
        // So we just verify the graphics context was disposed
        verify(mockGraphics2D).dispose();
    }
    
    @Test
    @DisplayName("toString should return formatted string representation")
    void toStringShouldReturnFormattedStringRepresentation() {
        String expected = "TextItem[" + testLevel + "," + testText + "]";
        assertEquals(expected, textItem.toString(), "toString should return formatted representation");
    }
}
