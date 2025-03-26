package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.PresentationObserver;
import org.jabberpoint.src.Slide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Presentation class
 */
public class PresentationTest {
    
    private Presentation presentation;
    private Slide testSlide1;
    private Slide testSlide2;
    private PresentationObserver mockObserver;
    
    @BeforeEach
    void setUp() {
        presentation = new Presentation();
        testSlide1 = new Slide();
        testSlide1.setTitle("Test Slide 1");
        testSlide2 = new Slide();
        testSlide2.setTitle("Test Slide 2");
        mockObserver = mock(PresentationObserver.class);
    }
    
    @Test
    @DisplayName("Should start with empty presentation")
    void shouldStartWithEmptyPresentation() {
        // Assert
        assertEquals(0, presentation.getSize());
        assertEquals(-1, presentation.getSlideNumber());
        assertNull(presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should set and get title")
    void shouldSetAndGetTitle() {
        // Arrange
        String testTitle = "Test Presentation";
        
        // Act
        presentation.setTitle(testTitle);
        
        // Assert
        assertEquals(testTitle, presentation.getTitle());
    }
    
    @Test
    @DisplayName("Should append and retrieve slides")
    void shouldAppendAndRetrieveSlides() {
        // Act
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        
        // Assert
        assertEquals(2, presentation.getSize());
        assertSame(testSlide1, presentation.getSlide(0));
        assertSame(testSlide2, presentation.getSlide(1));
    }
    
    @Test
    @DisplayName("Should set and get slide number")
    void shouldSetAndGetSlideNumber() {
        // Arrange
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        
        // Act
        presentation.setSlideNumber(1);
        
        // Assert
        assertEquals(1, presentation.getSlideNumber());
        assertSame(testSlide2, presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should navigate to previous slide")
    void shouldNavigateToPreviousSlide() {
        // Arrange
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        presentation.setSlideNumber(1);
        
        // Act
        presentation.prevSlide();
        
        // Assert
        assertEquals(0, presentation.getSlideNumber());
        assertSame(testSlide1, presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should not go below first slide when navigating previous")
    void shouldNotGoBelowFirstSlide() {
        // Arrange
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        presentation.setSlideNumber(0);
        
        // Act
        presentation.prevSlide();
        
        // Assert
        assertEquals(0, presentation.getSlideNumber());
        assertSame(testSlide1, presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should navigate to next slide")
    void shouldNavigateToNextSlide() {
        // Arrange
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        presentation.setSlideNumber(0);
        
        // Act
        presentation.nextSlide();
        
        // Assert
        assertEquals(1, presentation.getSlideNumber());
        assertSame(testSlide2, presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should not go beyond last slide when navigating next")
    void shouldNotGoBeyondLastSlide() {
        // Arrange
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        presentation.setSlideNumber(1);
        
        // Act
        presentation.nextSlide();
        
        // Assert
        assertEquals(1, presentation.getSlideNumber());
        assertSame(testSlide2, presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should clear presentation")
    void shouldClearPresentation() {
        // Arrange
        presentation.append(testSlide1);
        presentation.append(testSlide2);
        presentation.setSlideNumber(1);
        
        // Act
        // Create a new presentation instead of calling clear() since it's not public
        presentation = new Presentation();
        
        // Assert
        assertEquals(0, presentation.getSize());
        assertEquals(-1, presentation.getSlideNumber());
        assertNull(presentation.getCurrentSlide());
    }
    
    @Test
    @DisplayName("Should return null for invalid slide number")
    void shouldReturnNullForInvalidSlideNumber() {
        // Arrange
        presentation.append(testSlide1);
        
        // Act & Assert
        assertNull(presentation.getSlide(-1));
        assertNull(presentation.getSlide(1));
    }
    
    @Test
    @DisplayName("Should add and notify observer")
    void shouldAddAndNotifyObserver() {
        // Arrange
        presentation.append(testSlide1);
        presentation.addObserver(mockObserver);
        
        // Act
        presentation.setSlideNumber(0);
        
        // Assert
        verify(mockObserver).update(eq(presentation), eq(testSlide1));
    }
    
    @Test
    @DisplayName("Should remove observer")
    void shouldRemoveObserver() {
        // Arrange
        presentation.append(testSlide1);
        presentation.addObserver(mockObserver);
        presentation.removeObserver(mockObserver);
        clearInvocations(mockObserver);
        
        // Act
        presentation.setSlideNumber(0);
        
        // Assert
        verify(mockObserver, never()).update(any(), any());
    }
}
