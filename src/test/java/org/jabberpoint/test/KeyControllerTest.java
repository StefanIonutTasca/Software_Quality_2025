package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.KeyEvent;
import org.jabberpoint.src.DummyComponent;
import org.jabberpoint.src.KeyController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for KeyController */
class KeyControllerTest {

  private KeyController keyController;
  private Presentation presentation;
  private int currentSlide;
  private boolean exitCalled;

  @BeforeEach
  void setUp() {
    exitCalled = false;
    currentSlide = 0;

    // Create a test presentation that tracks state changes instead of actually changing slides
    presentation =
        new Presentation() {
          @Override
          public void nextSlide() {
            currentSlide++;
          }

          @Override
          public void prevSlide() {
            if (currentSlide > 0) currentSlide--;
          }

          @Override
          public void exit(int n) {
            exitCalled = true;
          }
        };

    keyController = new KeyController(presentation);
  }

  @Test
  @DisplayName("Should move to next slide on PAGE_DOWN key")
  void shouldMoveToNextSlideOnPageDown() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_PAGE_DOWN,
            KeyEvent.CHAR_UNDEFINED);

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(1, currentSlide);
  }

  @Test
  @DisplayName("Should move to next slide on DOWN arrow key")
  void shouldMoveToNextSlideOnDownArrow() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_DOWN,
            KeyEvent.CHAR_UNDEFINED);

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(1, currentSlide);
  }

  @Test
  @DisplayName("Should move to next slide on ENTER key")
  void shouldMoveToNextSlideOnEnter() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            KeyEvent.CHAR_UNDEFINED);

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(1, currentSlide);
  }

  @Test
  @DisplayName("Should move to next slide on plus key")
  void shouldMoveToNextSlideOnPlus() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            (int) '+',
            '+');

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(1, currentSlide);
  }

  @Test
  @DisplayName("Should move to previous slide on PAGE_UP key")
  void shouldMoveToPrevSlideOnPageUp() {
    // Arrange
    currentSlide = 1;
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_PAGE_UP,
            KeyEvent.CHAR_UNDEFINED);

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(0, currentSlide);
  }

  @Test
  @DisplayName("Should move to previous slide on UP arrow key")
  void shouldMoveToPrevSlideOnUpArrow() {
    // Arrange
    currentSlide = 1;
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_UP,
            KeyEvent.CHAR_UNDEFINED);

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(0, currentSlide);
  }

  @Test
  @DisplayName("Should move to previous slide on minus key")
  void shouldMoveToPrevSlideOnMinus() {
    // Arrange
    currentSlide = 1;
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            (int) '-',
            '-');

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(0, currentSlide);
  }

  @Test
  @DisplayName("Should exit on lowercase q key")
  void shouldExitOnLowercaseQ() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            (int) 'q',
            'q');

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertTrue(exitCalled);
  }

  @Test
  @DisplayName("Should exit on uppercase Q key")
  void shouldExitOnUppercaseQ() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            (int) 'Q',
            'Q');

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertTrue(exitCalled);
  }

  @Test
  @DisplayName("Should do nothing on unregistered key")
  void shouldDoNothingOnUnregisteredKey() {
    // Arrange
    KeyEvent keyEvent =
        new KeyEvent(
            new DummyComponent(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_A,
            'a');
    int initialSlide = currentSlide;

    // Act
    keyController.keyPressed(keyEvent);

    // Assert
    assertEquals(initialSlide, currentSlide);
    assertFalse(exitCalled);
  }

  // Dummy component class for creating KeyEvents
  private static class DummyComponent extends java.awt.Component {
    private static final long serialVersionUID = 1L;
  }
}
