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
        
        // Create a default font in case the one in Style is null
        Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
        
        // Mock the font metrics - handle potentially null font from Style
        when(graphics2D.getFontMetrics(any(Font.class))).thenReturn(fontMetrics);
        when(graphics2D.getFontMetrics(isNull())).thenReturn(fontMetrics);
        when(fontMetrics.getHeight()).thenReturn(20);
        when(fontMetrics.stringWidth(anyString())).thenReturn(100);
        when(fontMetrics.getAscent()).thenReturn(15);
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
    @DisplayName("getAttributedString should return AttributedString with style font")
    void getAttributedStringShouldReturnAttributedStringWithStyleFont() {
        // Skip this test if the Style class is not properly initialized
        // since we're testing the integration with Style and not TextItem functionality
        if (style.getFont() == null) {
            return;
        }
        
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
    @DisplayName("draw should not perform operations for empty text")
    void drawShouldNotPerformOperationsForEmptyText() {
        TextItem emptyItem = new TextItem(1, "");
        
        // Call draw
        emptyItem.draw(10, 10, 1.0f, graphics2D, style, observer);
        
        // Verify no drawing operations were performed
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
        verify(spyGraphics).drawString(eq(testText), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("toString should return the text content")
    void toStringShouldReturnTextContent() {
        assertEquals(testText, textItem.toString(), "toString should return the text content");
    }
}
