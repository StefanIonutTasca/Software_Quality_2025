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
    
    @Mock
    private Style mockStyle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a text item with level and text
        textItem = new TextItem(testLevel, testText);
        
        // Mock Style behavior
        when(mockStyle.getFont(anyFloat())).thenReturn(new Font("Dialog", Font.PLAIN, 12));
        when(mockStyle.indent).thenReturn(10f);
        when(mockStyle.leading).thenReturn(20f);
        when(mockStyle.color).thenReturn(Color.BLACK);
        
        // Mock Graphics and Graphics2D behavior
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
        Font testFont = new Font("Arial", Font.BOLD, 14);
        when(mockStyle.getFont(scale)).thenReturn(testFont);
        
        AttributedString result = textItem.getAttributedString(mockStyle, scale);
        
        // We can't directly check attributes, but we can verify the mock was called
        verify(mockStyle).getFont(scale);
        assertNotNull(result, "AttributedString should not be null");
    }
    
    @Test
    @DisplayName("getBoundingBox should calculate correct dimensions")
    void getBoundingBoxShouldCalculateCorrectDimensions() {
        // Need to use spy to partially mock TextItem
        TextItem spyItem = spy(textItem);
        
        // Create a simple mock layout
        TextLayout mockLayout = mock(TextLayout.class);
        java.awt.geom.Rectangle2D mockBounds = new java.awt.geom.Rectangle2D.Double(0, 0, 100, 30);
        
        when(mockLayout.getBounds()).thenReturn(mockBounds);
        when(mockLayout.getLeading()).thenReturn(2f);
        when(mockLayout.getDescent()).thenReturn(3f);
        
        // Mock the getLayouts method to return our mock layout
        java.util.List<TextLayout> layouts = new java.util.ArrayList<>();
        layouts.add(mockLayout);
        doReturn(layouts).when(spyItem).getLayouts(any(Graphics.class), any(Style.class), anyFloat());
        
        // Call the method
        float scale = 1.0f;
        Rectangle boundingBox = spyItem.getBoundingBox(mockGraphics, mockObserver, scale, mockStyle);
        
        // Verify results
        assertNotNull(boundingBox, "Bounding box should not be null");
        assertEquals((int)(mockStyle.indent * scale), boundingBox.x, "X position should be style indent * scale");
        assertEquals(0, boundingBox.y, "Y position should be 0");
        assertEquals((int)mockBounds.getWidth(), boundingBox.width, "Width should match the text layout bounds");
        assertTrue(boundingBox.height > 0, "Height should be positive");
    }
    
    @Test
    @DisplayName("draw should not draw anything if text is empty")
    void drawShouldNotDrawIfTextIsEmpty() {
        // Create text item with empty text
        TextItem emptyItem = new TextItem(1, "");
        
        // Call draw
        emptyItem.draw(10, 10, 1.0f, mockGraphics, mockStyle, mockObserver);
        
        // Verify no drawing operations were performed
        verify(mockGraphics, never()).create();
    }
    
    @Test
    @DisplayName("draw should draw text with proper styling")
    void drawShouldDrawTextWithProperStyling() {
        // Need to use spy to partially mock TextItem
        TextItem spyItem = spy(textItem);
        
        // Create a simple mock layout
        TextLayout mockLayout = mock(TextLayout.class);
        
        // Mock layout behavior
        when(mockLayout.getAscent()).thenReturn(10f);
        when(mockLayout.getDescent()).thenReturn(5f);
        
        // Mock the getLayouts method to return our mock layout
        java.util.List<TextLayout> layouts = new java.util.ArrayList<>();
        layouts.add(mockLayout);
        doReturn(layouts).when(spyItem).getLayouts(any(Graphics.class), any(Style.class), anyFloat());
        
        // Mock Graphics2D casting
        when(mockGraphics.create()).thenReturn(mockGraphics2D);
        
        // Call the draw method
        int x = 5, y = 10;
        float scale = 1.0f;
        spyItem.draw(x, y, scale, mockGraphics, mockStyle, mockObserver);
        
        // Verify interactions
        verify(mockGraphics2D).setColor(mockStyle.color);
        verify(mockLayout).draw(eq(mockGraphics2D), anyFloat(), anyFloat());
        verify(mockLayout).getAscent();
        verify(mockLayout).getDescent();
    }
    
    @Test
    @DisplayName("toString should return formatted string representation")
    void toStringShouldReturnFormattedStringRepresentation() {
        String expected = "TextItem[" + testLevel + "," + testText + "]";
        assertEquals(expected, textItem.toString(), "toString should return formatted representation");
    }
}
