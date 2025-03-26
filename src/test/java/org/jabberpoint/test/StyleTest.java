package org.jabberpoint.test;

import org.jabberpoint.src.Style;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;
import java.awt.Font;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Style class (Singleton)
 */
public class StyleTest {
    
    @Test
    @DisplayName("Should return same instance for multiple getInstance calls")
    void shouldReturnSameInstanceForMultipleGetInstanceCalls() {
        // Act
        Style instance1 = Style.getInstance();
        Style instance2 = Style.getInstance();
        
        // Assert
        assertSame(instance1, instance2, "Style should use singleton pattern");
    }
    
    @Test
    @DisplayName("Should have appropriate default values")
    void shouldHaveAppropriateDefaultValues() {
        // Arrange
        Style style = Style.getInstance();
        
        // Assert
        assertNotNull(style.styles);
        assertTrue(style.styles.length > 0);
        
        // Test first style (level 0)
        assertEquals(0, style.styles[0].indent);
        assertEquals(Color.black, style.styles[0].color);
        assertNotNull(style.styles[0].font);
        
        // Test that styles for different levels have different values
        if (style.styles.length > 1) {
            assertNotEquals(style.styles[0].indent, style.styles[1].indent);
            assertNotEquals(style.styles[0].font.getSize(), style.styles[1].font.getSize());
        }
    }
    
    @Test
    @DisplayName("Should return correct style for valid level")
    void shouldReturnCorrectStyleForValidLevel() {
        // Arrange
        Style style = Style.getInstance();
        
        // Act
        Style result = style.getStyle(0);
        
        // Assert
        assertSame(style, result);
    }
    
    @Test
    @DisplayName("Should return highest level style for too high level")
    void shouldReturnHighestLevelStyleForTooHighLevel() {
        // Arrange
        Style style = Style.getInstance();
        int highestLevel = style.styles.length - 1;
        
        // Act
        Style result = style.getStyle(highestLevel + 10);
        
        // Assert
        assertSame(style, result);
        assertEquals(style.styles[highestLevel].indent, style.indent);
        assertEquals(style.styles[highestLevel].color, style.color);
        assertEquals(style.styles[highestLevel].font, style.font);
        assertEquals(style.styles[highestLevel].leading, style.leading);
    }
    
    @Test
    @DisplayName("getStyle should set member variables to correct values")
    void getStyleShouldSetMemberVariablesToCorrectValues() {
        // Arrange
        Style style = Style.getInstance();
        int testLevel = 2;
        
        // Ensure we don't exceed array bounds
        if (testLevel >= style.styles.length) {
            testLevel = style.styles.length - 1;
        }
        
        // Act
        Style result = style.getStyle(testLevel);
        
        // Assert
        assertEquals(style.styles[testLevel].indent, result.indent);
        assertEquals(style.styles[testLevel].color, result.color);
        assertEquals(style.styles[testLevel].font, result.font);
        assertEquals(style.styles[testLevel].leading, result.leading);
    }
    
    @Test
    @DisplayName("Should handle negative level by using level 0")
    void shouldHandleNegativeLevelByUsingLevelZero() {
        // Arrange
        Style style = Style.getInstance();
        
        // Act
        Style result = style.getStyle(-1);
        
        // Assert
        assertSame(style, result);
        assertEquals(style.styles[0].indent, result.indent);
        assertEquals(style.styles[0].color, result.color);
        assertEquals(style.styles[0].font, result.font);
        assertEquals(style.styles[0].leading, result.leading);
    }
}
