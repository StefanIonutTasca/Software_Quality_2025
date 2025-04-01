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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedString;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TextItemTest {

    private TextItem textItem;
    private String testText = "Test text for TextItem";
    private int testLevel = 2;
    private Style style;
    private Graphics2D graphics2D;
    private final float scale = 1.0f;
    
    @Mock
    private ImageObserver observer;
    
    @Mock
    private FontMetrics fontMetrics;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize Style singleton
        Style.createStyles();
        style = Style.getStyle(testLevel);
        
        // Create a text item for testing
        textItem = new TextItem(testLevel, testText);
        
        // Create a real Graphics2D from a BufferedImage
        BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        graphics2D = image.createGraphics();
        
        // Mock the font metrics
        when(graphics2D.getFontMetrics(any(Font.class))).thenReturn(fontMetrics);
        when(fontMetrics.getHeight()).thenReturn(20);
        when(fontMetrics.stringWidth(anyString())).thenReturn(100);
        when(fontMetrics.getAscent()).thenReturn(15);
    }
    
    @Test
    @DisplayName("Constructor should initialize TextItem with level and text")
    void constructorShouldInitializeWithLevelAndText() {
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
        AttributedString result = textItem.getAttributedString(style, scale);
        
        assertNotNull(result, "AttributedString should not be null");
    }
    
    @Test
    @DisplayName("getBoundingBox should calculate correct dimensions")
    void getBoundingBoxShouldCalculateCorrectDimensions() {
        // Get the bounding box
        Rectangle boundingBox = textItem.getBoundingBox(graphics2D, observer, scale, style);
        
        // Verify dimensions
        assertNotNull(boundingBox, "Bounding box should not be null");
        assertTrue(boundingBox.width > 0, "Width should be positive");
        assertTrue(boundingBox.height > 0, "Height should be positive");
    }
    
    @Test
    @DisplayName("draw should not draw anything if text is empty")
    void drawShouldNotDrawIfTextIsEmpty() {
        // Create text item with empty text
        TextItem emptyItem = new TextItem(1, "");
        
        // Call draw
        emptyItem.draw(10, 10, 1.0f, graphics2D, style, observer);
        
        // Verify no drawing operations were performed
        verify(graphics2D, never()).setColor(any(Color.class));
        verify(graphics2D, never()).setFont(any(Font.class));
        verify(graphics2D, never()).drawString(anyString(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("draw should draw text with proper styling")
    void drawShouldDrawTextWithProperStyling() {
        // Create a spy on the Graphics2D to verify method calls
        Graphics2D spyGraphics = spy(graphics2D);
        
        // Call draw method
        int x = 5, y = 10;
        textItem.draw(x, y, scale, spyGraphics, style, observer);
        
        // Verify proper methods were called with correct arguments
        verify(spyGraphics).setColor(any(Color.class));
        verify(spyGraphics).setFont(any(Font.class));
        verify(spyGraphics).drawString(eq(testText), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("toString should return formatted string representation")
    void toStringShouldReturnFormattedStringRepresentation() {
        String expected = "TextItem[" + testLevel + "," + testText + "]";
        assertEquals(expected, textItem.toString(), "toString should return formatted representation");
    }
}
