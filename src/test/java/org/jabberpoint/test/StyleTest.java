package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import org.jabberpoint.src.model.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for Style class */
class StyleTest {

  private Style style;
  private static final String FONTNAME = "SansSerif";

  @BeforeEach
  void setUp() {
    // Reset the singleton instance before each test
    resetSingletonInstance();
  }

  @Test
  @DisplayName("Should create Style with correct properties")
  void shouldCreateStyleWithCorrectProperties() {
    // Act
    style = new Style(30, Color.BLUE, 24, 15);

    // Assert - can't directly access private fields, testing indirectly
    assertEquals("[30,java.awt.Color[r=0,g=0,b=255]; 24 on 15]", style.toString());
  }

  @Test
  @DisplayName("Should return correct string representation")
  void shouldReturnCorrectStringRepresentation() {
    // Arrange
    style = new Style(40, Color.RED, 18, 12);

    // Act
    String result = style.toString();

    // Assert
    assertTrue(result.contains("40"));
    assertTrue(result.contains("18"));
    assertTrue(result.contains("12"));
  }

  @Test
  @DisplayName("Should return scaled font")
  void shouldReturnScaledFont() {
    // Arrange
    style = new Style(10, Color.BLACK, 20, 10);
    float scale = 1.5f;

    // Act
    Font scaledFont = style.getFont(scale);

    // Assert
    assertNotNull(scaledFont);
    assertEquals(FONTNAME, scaledFont.getFamily());
    assertEquals(Font.BOLD, scaledFont.getStyle());
    assertEquals(30.0f, scaledFont.getSize2D(), 0.001); // 20 * 1.5 = 30
  }

  @Test
  @DisplayName("Should initialize default styles")
  void shouldInitializeDefaultStyles() {
    // Act
    Style.createStyles();

    // Assert - Check if styles are created with correct values
    Style level0Style = Style.getStyle(0);
    assertNotNull(level0Style);

    // Test via toString to avoid direct field access
    String level0String = level0Style.toString();
    assertTrue(level0String.contains("0"));

    Style level1Style = Style.getStyle(1);
    assertNotNull(level1Style);
    String level1String = level1Style.toString();
    assertTrue(level1String.contains("20"));

    // Check remaining styles
    assertNotNull(Style.getStyle(2));
    assertNotNull(Style.getStyle(3));
    assertNotNull(Style.getStyle(4));
  }

  @Test
  @DisplayName("Should return highest available style for level exceeding maximum")
  void shouldReturnHighestAvailableStyleForLevelExceedingMaximum() {
    // Arrange
    Style.createStyles();

    // Act
    Style highLevelStyle = Style.getStyle(10); // Level higher than available styles
    Style maxLevelStyle = Style.getStyle(4); // Max available level

    // Assert
    assertSame(maxLevelStyle, highLevelStyle);
  }

  @Test
  @DisplayName("Should use singleton pattern correctly")
  void shouldUseSingletonPatternCorrectly() {
    // Act
    Style instance1 = Style.getInstance();
    Style instance2 = Style.getInstance();

    // Assert
    assertNotNull(instance1);
    assertSame(instance1, instance2);
  }

  /** Helper method to reset the singleton instance between tests */
  private void resetSingletonInstance() {
    try {
      Field instanceField = Style.class.getDeclaredField("instance");
      instanceField.setAccessible(true);
      instanceField.set(null, null);
    } catch (Exception e) {
      System.err.println("Error resetting Style singleton: " + e.getMessage());
    }
  }
}
