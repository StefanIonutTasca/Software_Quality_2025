package org.jabberpoint.test;

import org.jabberpoint.src.Command;
import org.jabberpoint.src.ExitCommand;
import org.jabberpoint.src.NextSlideCommand;
import org.jabberpoint.src.PrevSlideCommand;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for Command pattern implementations
 */
@ExtendWith(MockitoExtension.class)
class CommandsTest {

    @Mock
    private Presentation mockPresentation;

    @BeforeEach
    void setUp() {
        // Reset mock before each test
        reset(mockPresentation);
    }

    @Test
    @DisplayName("NextSlideCommand should call nextSlide on presentation")
    void nextSlideCommandShouldCallNextSlide() {
        // Arrange
        Command command = new NextSlideCommand(mockPresentation);
        
        // Act
        command.execute();
        
        // Assert
        verify(mockPresentation, times(1)).nextSlide();
        verifyNoMoreInteractions(mockPresentation);
    }
    
    @Test
    @DisplayName("PrevSlideCommand should call prevSlide on presentation")
    void prevSlideCommandShouldCallPrevSlide() {
        // Arrange
        Command command = new PrevSlideCommand(mockPresentation);
        
        // Act
        command.execute();
        
        // Assert
        verify(mockPresentation, times(1)).prevSlide();
        verifyNoMoreInteractions(mockPresentation);
    }
    
    @Test
    @DisplayName("ExitCommand should call exit(0) on presentation")
    void exitCommandShouldCallExit() {
        // Arrange
        Command command = new ExitCommand(mockPresentation);
        
        // Act
        command.execute();
        
        // Assert
        verify(mockPresentation, times(1)).exit(0);
        verifyNoMoreInteractions(mockPresentation);
    }
}
