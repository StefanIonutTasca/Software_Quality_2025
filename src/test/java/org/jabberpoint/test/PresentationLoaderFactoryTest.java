package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import org.jabberpoint.src.DemoPresentationLoader;
import org.jabberpoint.src.PresentationLoader;
import org.jabberpoint.src.PresentationLoaderFactory;
import org.jabberpoint.src.XMLPresentationLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Unit tests for PresentationLoaderFactory */
class PresentationLoaderFactoryTest {

  @Test
  @DisplayName("Should create XMLPresentationLoader when type is xml")
  void createLoaderShouldReturnXMLLoaderWhenTypeIsXml() {
    // Act
    PresentationLoader loader = PresentationLoaderFactory.createLoader("xml");

    // Assert
    assertTrue(
        loader instanceof XMLPresentationLoader,
        "Loader should be an instance of XMLPresentationLoader");
  }

  @Test
  @DisplayName("Should create XMLPresentationLoader when type is XML (case insensitive)")
  void createLoaderShouldReturnXMLLoaderWhenTypeIsXmlUppercase() {
    // Act
    PresentationLoader loader = PresentationLoaderFactory.createLoader("XML");

    // Assert
    assertTrue(
        loader instanceof XMLPresentationLoader,
        "Loader should be an instance of XMLPresentationLoader");
  }

  @Test
  @DisplayName("Should create DemoPresentationLoader when type is demo")
  void createLoaderShouldReturnDemoLoaderWhenTypeIsDemo() {
    // Act
    PresentationLoader loader = PresentationLoaderFactory.createLoader("demo");

    // Assert
    assertTrue(
        loader instanceof DemoPresentationLoader,
        "Loader should be an instance of DemoPresentationLoader");
  }

  @Test
  @DisplayName("Should create DemoPresentationLoader when type is DEMO (case insensitive)")
  void createLoaderShouldReturnDemoLoaderWhenTypeIsDemoUppercase() {
    // Act
    PresentationLoader loader = PresentationLoaderFactory.createLoader("DEMO");

    // Assert
    assertTrue(
        loader instanceof DemoPresentationLoader,
        "Loader should be an instance of DemoPresentationLoader");
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when type is unknown")
  void createLoaderShouldThrowExceptionWhenTypeIsUnknown() {
    // Arrange
    String unknownType = "unknown";

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> PresentationLoaderFactory.createLoader(unknownType),
            "Should throw IllegalArgumentException for unknown loader type");

    // Additional assertion on the exception message
    assertTrue(
        exception.getMessage().contains(unknownType),
        "Exception message should contain the unknown type");
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "pdf", "powerpoint", "invalid"})
  @DisplayName("Should throw IllegalArgumentException for various invalid types")
  void createLoaderShouldThrowExceptionForVariousInvalidTypes(String invalidType) {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> PresentationLoaderFactory.createLoader(invalidType),
        "Should throw IllegalArgumentException for invalid loader type: " + invalidType);
  }
}
