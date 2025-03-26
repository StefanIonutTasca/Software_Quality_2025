package org.jabberpoint.test;

import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Style class
 */
public class StyleTest {
    
    @BeforeEach
    void setUp() {
        // Reset the singleton instance before each test using reflection
        try {
            Field instanceField = Style.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            
            // Remove final modifier
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(instanceField, instanceField.getModifiers() & ~Modifier.FINAL);
            
            // Set the instance to null
            instanceField.set(null, null);
            
            // Reset styles array if it exists
            try {
                Field stylesField = Style.class.getDeclaredField("styles");
                stylesField.setAccessible(true);
                stylesField.set(null, null);
            } catch (Exception e) {
                // Ignore if styles field not found or cannot be reset
            }
        } catch (Exception e) {
            // If reflection fails, just proceed with the test
            System.out.println("Could not reset singleton: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should create singleton instance")
    void shouldCreateSingletonInstance() {
        // Act
        Style style1 = Style.getInstance();
        Style style2 = Style.getInstance();
        
        // Assert
        assertNotNull(style1);
        assertSame(style1, style2, "getInstance should always return the same instance");
    }
    
    @Test
    @DisplayName("Should create styles with createStyles method")
    void shouldCreateStylesWithCreateStylesMethod() {
        // Act
        Style.createStyles();
        Style style = Style.getStyle(0);
        
        // Assert
        assertNotNull(style);
        
        // Test that we have a valid style
        assertNotNull(style.toString());
    }
    
    @Test
    @DisplayName("Should get correct style for level")
    void shouldGetCorrectStyleForLevel() {
        // Arrange
        Style.getInstance(); // Make sure styles are initialized
        
        // Act & Assert
        Style level0Style = Style.getStyle(0);
        Style level1Style = Style.getStyle(1);
        Style level2Style = Style.getStyle(2);
        
        assertNotNull(level0Style);
        assertNotNull(level1Style);
        assertNotNull(level2Style);
        
        // Different levels should have different styles
        assertNotSame(level0Style, level1Style);
        assertNotSame(level1Style, level2Style);
        
        // Check some properties using toString()
        String style0String = level0Style.toString();
        String style1String = level1Style.toString();
        
        assertNotEquals(style0String, style1String);
    }
    
    @Test
    @DisplayName("Should return highest available style for too high level")
    void shouldReturnHighestAvailableStyleForTooHighLevel() {
        // Arrange - get the highest valid style level
        Style.getInstance(); // Ensure styles are created
        
        try {
            // Use reflection to get styles array length
            Field stylesField = Style.class.getDeclaredField("styles");
            stylesField.setAccessible(true);
            Style[] styles = (Style[])stylesField.get(null);
            int highestLevel = styles.length - 1;
            
            // Act - request a style with a level higher than available
            Style highestStyle = Style.getStyle(highestLevel);
            Style tooHighStyle = Style.getStyle(highestLevel + 100);
            
            // Assert
            assertSame(highestStyle, tooHighStyle);
        } catch (Exception e) {
            fail("Exception accessing styles array: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should create style with constructor parameters")
    void shouldCreateStyleWithConstructorParameters() {
        // Arrange
        int indent = 30;
        Color color = Color.GREEN;
        int fontSize = 24;
        int leading = 6;
        
        // Act
        Style customStyle = new Style(indent, color, fontSize, leading);
        
        // Assert
        assertNotNull(customStyle);
        
        // Test constructor parameters reflected in toString()
        String styleString = customStyle.toString();
        assertTrue(styleString.contains(String.valueOf(indent)));
        assertTrue(styleString.contains(String.valueOf(fontSize)));
        assertTrue(styleString.contains(String.valueOf(leading)));
    }
    
    @Test
    @DisplayName("Should return string representation")
    void shouldReturnStringRepresentation() {
        // Arrange
        Style customStyle = new Style(10, Color.RED, 12, 5);
        
        // Act
        String styleString = customStyle.toString();
        
        // Assert
        assertNotNull(styleString);
        assertTrue(styleString.contains("10"));
        assertTrue(styleString.contains("12"));
        assertTrue(styleString.contains("5"));
    }
    
    @Test
    @DisplayName("Should derive font with scale")
    void shouldDeriveFontWithScale() {
        // Arrange
        int fontSize = 24;
        Style customStyle = new Style(10, Color.BLUE, fontSize, 5);
        float scale = 1.5f;
        
        // Act
        Font derivedFont = customStyle.getFont(scale);
        
        // Assert
        assertNotNull(derivedFont);
        assertEquals(Math.round(fontSize * scale), derivedFont.getSize());
    }
}
