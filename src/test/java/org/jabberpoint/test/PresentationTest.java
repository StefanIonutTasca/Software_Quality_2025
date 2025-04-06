package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.PresentationObserver;
import org.jabberpoint.src.model.Slide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Unit tests for Presentation class */
class PresentationTest {

  private Presentation presentation;
  private Slide mockSlide1;
  private Slide mockSlide2;
  private Slide mockSlide3;
  private PresentationObserver mockObserver;

  @BeforeEach
  void setUp() {
    presentation = new Presentation();
    mockSlide1 = new Slide();
    mockSlide1.setTitle("Slide 1");
    mockSlide2 = new Slide();
    mockSlide2.setTitle("Slide 2");
    mockSlide3 = new Slide();
    mockSlide3.setTitle("Slide 3");
    mockObserver = Mockito.mock(PresentationObserver.class);
  }

  @Test
  @DisplayName("Should create empty presentation")
  void shouldCreateEmptyPresentation() {
    // Assert
    assertEquals(0, presentation.getSize());
    assertEquals(-1, presentation.getSlideNumber());
    assertNull(presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should set and get title")
  void shouldSetAndGetTitle() {
    // Act
    presentation.setTitle("Test Presentation");

    // Assert
    assertEquals("Test Presentation", presentation.getTitle());
  }

  @Test
  @DisplayName("Should append slides")
  void shouldAppendSlides() {
    // Act
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);

    // Assert
    assertEquals(2, presentation.getSize());
    assertSame(mockSlide1, presentation.getSlide(0));
    assertSame(mockSlide2, presentation.getSlide(1));
  }

  @Test
  @DisplayName("Should set and get slide number")
  void shouldSetAndGetSlideNumber() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);

    // Act
    presentation.setSlideNumber(1);

    // Assert
    assertEquals(1, presentation.getSlideNumber());
    assertSame(mockSlide2, presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should navigate to next slide")
  void shouldNavigateToNextSlide() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);
    presentation.setSlideNumber(0);

    // Act
    presentation.nextSlide();

    // Assert
    assertEquals(1, presentation.getSlideNumber());
    assertSame(mockSlide2, presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should stay at last slide when nextSlide called on last slide")
  void shouldStayAtLastSlideWhenNextSlideCalledOnLastSlide() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);
    presentation.setSlideNumber(1); // Last slide

    // Act
    presentation.nextSlide();

    // Assert
    assertEquals(1, presentation.getSlideNumber());
    assertSame(mockSlide2, presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should navigate to previous slide")
  void shouldNavigateToPreviousSlide() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);
    presentation.setSlideNumber(1);

    // Act
    presentation.prevSlide();

    // Assert
    assertEquals(0, presentation.getSlideNumber());
    assertSame(mockSlide1, presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should stay at first slide when prevSlide called on first slide")
  void shouldStayAtFirstSlideWhenPrevSlideCalledOnFirstSlide() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);
    presentation.setSlideNumber(0); // First slide

    // Act
    presentation.prevSlide();

    // Assert
    assertEquals(0, presentation.getSlideNumber());
    assertSame(mockSlide1, presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should return null for invalid slide number")
  void shouldReturnNullForInvalidSlideNumber() {
    // Arrange
    presentation.append(mockSlide1);

    // Act & Assert
    assertNull(presentation.getSlide(-1));
    assertNull(presentation.getSlide(1));
  }

  @Test
  @DisplayName("Should clear presentation")
  void shouldClearPresentation() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);
    presentation.setSlideNumber(1);

    // Act - create a new Presentation (which calls clear internally)
    presentation = new Presentation();

    // Assert
    assertEquals(0, presentation.getSize());
    assertEquals(-1, presentation.getSlideNumber());
    assertNull(presentation.getCurrentSlide());
  }

  @Test
  @DisplayName("Should notify observers when slide changes")
  void shouldNotifyObserversWhenSlideChanges() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.append(mockSlide2);
    presentation.addObserver(mockObserver);

    // Act
    presentation.setSlideNumber(1);

    // Assert
    Mockito.verify(mockObserver).update(presentation, mockSlide2);
  }

  @Test
  @DisplayName("Should remove observer properly")
  void shouldRemoveObserverProperly() {
    // Arrange
    presentation.append(mockSlide1);
    presentation.addObserver(mockObserver);
    presentation.removeObserver(mockObserver);

    // Act
    presentation.setSlideNumber(0);

    // Assert - observer should not be notified after removal
    Mockito.verify(mockObserver, Mockito.never()).update(Mockito.any(), Mockito.any());
  }
}
