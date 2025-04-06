package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import org.jabberpoint.src.command.ExitCommand;
import org.jabberpoint.src.command.NewPresentationCommand;
import org.jabberpoint.src.command.NextSlideCommand;
import org.jabberpoint.src.command.PrevSlideCommand;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** Unit tests for Command pattern implementations */
public class CommandTest {

  private Presentation presentation;
  private Slide testSlide1;
  private Slide testSlide2;

  @BeforeEach
  void setUp() {
    // Create a test presentation with two slides
    presentation = new Presentation();
    testSlide1 = new Slide();
    testSlide1.setTitle("Test Slide 1");
    testSlide2 = new Slide();
    testSlide2.setTitle("Test Slide 2");
    presentation.append(testSlide1);
    presentation.append(testSlide2);
    presentation.setSlideNumber(0); // Start at first slide
  }

  @Nested
  @DisplayName("NextSlideCommand Tests")
  class NextSlideCommandTest {
    private NextSlideCommand nextSlideCommand;

    @BeforeEach
    void setUp() {
      nextSlideCommand = new NextSlideCommand(presentation);
    }

    @Test
    @DisplayName("Should move to next slide when not at last slide")
    void executeShouldMoveToNextSlide() {
      // Arrange
      assertEquals(0, presentation.getSlideNumber());

      // Act
      nextSlideCommand.execute();

      // Assert
      assertEquals(1, presentation.getSlideNumber());
    }

    @Test
    @DisplayName("Should stay at last slide when already at last slide")
    void executeShouldStayAtLastSlideWhenAtEnd() {
      // Arrange
      presentation.setSlideNumber(1); // Move to last slide

      // Act
      nextSlideCommand.execute();

      // Assert
      assertEquals(1, presentation.getSlideNumber());
    }

    @Test
    @DisplayName("Should handle empty presentation")
    void executeShouldHandleEmptyPresentation() {
      // Arrange
      presentation = new Presentation();
      nextSlideCommand = new NextSlideCommand(presentation);

      // Act & Assert
      assertDoesNotThrow(() -> nextSlideCommand.execute());
    }
  }

  @Nested
  @DisplayName("PrevSlideCommand Tests")
  class PrevSlideCommandTest {
    private PrevSlideCommand prevSlideCommand;

    @BeforeEach
    void setUp() {
      prevSlideCommand = new PrevSlideCommand(presentation);
    }

    @Test
    @DisplayName("Should move to previous slide when not at first slide")
    void executeShouldMoveToPreviousSlide() {
      // Arrange
      presentation.setSlideNumber(1);

      // Act
      prevSlideCommand.execute();

      // Assert
      assertEquals(0, presentation.getSlideNumber());
    }

    @Test
    @DisplayName("Should stay at first slide when already at first slide")
    void executeShouldStayAtFirstSlideWhenAtStart() {
      // Arrange
      presentation.setSlideNumber(0);

      // Act
      prevSlideCommand.execute();

      // Assert
      assertEquals(0, presentation.getSlideNumber());
    }

    @Test
    @DisplayName("Should handle empty presentation")
    void executeShouldHandleEmptyPresentation() {
      // Arrange
      presentation = new Presentation();
      prevSlideCommand = new PrevSlideCommand(presentation);

      // Act & Assert
      assertDoesNotThrow(() -> prevSlideCommand.execute());
    }
  }

  @Nested
  @DisplayName("ExitCommand Tests")
  class ExitCommandTest {
    private ExitCommand exitCommand;
    private boolean exitCalled;

    @BeforeEach
    void setUp() {
      exitCalled = false;
      // Create a test presentation that tracks exit calls instead of actually exiting
      presentation =
          new Presentation() {
            @Override
            public void exit(int n) {
              exitCalled = true;
            }
          };
      exitCommand = new ExitCommand(presentation);
    }

    @Test
    @DisplayName("Should call exit when executed")
    void executeShouldCallExit() {
      // Act
      exitCommand.execute();

      // Assert
      assertTrue(exitCalled, "Exit should have been called");
    }

    @Test
    @DisplayName("Should handle null presentation")
    void constructorShouldHandleNullPresentation() {
      // Act & Assert
      assertThrows(
          NullPointerException.class,
          () -> new ExitCommand(null),
          "Constructor should throw NullPointerException for null presentation");
    }
  }

  @Nested
  @DisplayName("Tests for OpenFileCommand")
  class OpenFileCommandTest {
    // Skip OpenFileCommand tests in CI environment due to HeadlessException
    // These tests require a GUI environment to run properly
  }

  @Nested
  @DisplayName("Tests for SaveFileCommand")
  class SaveFileCommandTest {
    // Skip SaveFileCommand tests in CI environment due to HeadlessException
    // These tests require a GUI environment to run properly
  }

  @Nested
  @DisplayName("NewPresentationCommand Tests")
  class NewPresentationCommandTest {
    private NewPresentationCommand newPresentationCommand;

    @BeforeEach
    void setUp() {
      newPresentationCommand = new NewPresentationCommand(presentation);
    }

    @Test
    @DisplayName("Should clear existing presentation")
    void executeShouldClearPresentation() {
      // Arrange - ensure presentation has content
      assertEquals(2, presentation.getSize());

      // Act
      newPresentationCommand.execute();

      // Assert
      assertEquals(0, presentation.getSize(), "Presentation should be empty after clear");
      assertEquals(-1, presentation.getSlideNumber(), "Slide number should be reset");
    }

    @Test
    @DisplayName("Should handle already empty presentation")
    void executeShouldHandleEmptyPresentation() {
      // Arrange
      presentation = new Presentation();
      newPresentationCommand = new NewPresentationCommand(presentation);

      // Act & Assert
      assertDoesNotThrow(() -> newPresentationCommand.execute());
    }
  }
}
