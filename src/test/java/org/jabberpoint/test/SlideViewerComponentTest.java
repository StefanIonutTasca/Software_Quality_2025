package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for SlideViewerComponent class
 */
public class SlideViewerComponentTest {

    private SlideViewerComponent slideViewerComponent;
    private Presentation mockPresentation;
    private JFrame mockFrame;
    private Graphics mockGraphics;

    @BeforeEach
    void setUp() {
        mockPresentation = mock(Presentation.class);
        mockFrame = mock(JFrame.class);
        mockGraphics = mock(Graphics.class);
        
        slideViewerComponent = new SlideViewerComponent(mockPresentation, mockFrame);
    }

    @Test
    @DisplayName("Should register as an observer to the presentation")
    void shouldRegisterAsObserver() {
        // Verify that the component registered itself as an observer during initialization
        verify(mockPresentation).addObserver(slideViewerComponent);
    }
    
    @Test
    @DisplayName("Should return correct preferred size")
    void shouldReturnCorrectPreferredSize() {
        // Act
        Dimension preferredSize = slideViewerComponent.getPreferredSize();
        
        // Assert
        assertEquals(800, preferredSize.width);  // Assuming Slide.WIDTH is 800
        assertEquals(600, preferredSize.height); // Assuming Slide.HEIGHT is 600
    }
    
    @Test
    @DisplayName("Should update component when slide changes")
    void shouldUpdateComponentWhenSlideChanges() {
        // Arrange
        Slide testSlide = new Slide();
        testSlide.setTitle("Test Slide");
        testSlide.append(new TextItem(1, "Test Item"));
        when(mockPresentation.getTitle()).thenReturn("Test Presentation");
        
        // Act
        slideViewerComponent.update(mockPresentation, testSlide);
        
        // Assert
        verify(mockFrame).setTitle("Test Presentation");
    }
    
    @Test
    @DisplayName("Should handle null slide in update")
    void shouldHandleNullSlideInUpdate() {
        // Act - should not throw exception
        assertDoesNotThrow(() -> slideViewerComponent.update(mockPresentation, null));
    }
    
    @Test
    @DisplayName("Should paint component correctly with valid slide")
    void shouldPaintComponentCorrectlyWithValidSlide() {
        // Arrange
        Slide testSlide = mock(Slide.class);
        when(mockPresentation.getSlideNumber()).thenReturn(2); // Third slide (0-indexed)
        when(mockPresentation.getSize()).thenReturn(5); // Total 5 slides
        slideViewerComponent.update(mockPresentation, testSlide);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert
        verify(mockGraphics).setColor(any()); // Color is set
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt()); // Background is filled
        verify(mockGraphics).drawString(contains("Slide 3 of 5"), anyInt(), anyInt()); // Slide number is displayed
        verify(testSlide).draw(eq(mockGraphics), any(Rectangle.class), eq(slideViewerComponent)); // Slide is drawn
    }
    
    @Test
    @DisplayName("Should not draw slide when slide number is negative")
    void shouldNotDrawSlideWhenSlideNumberIsNegative() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(-1);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert - verify slide isn't drawn
        verify(mockGraphics, never()).drawString(contains("Slide"), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should not draw slide when slide is null")
    void shouldNotDrawSlideWhenSlideIsNull() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(0);
        slideViewerComponent.update(mockPresentation, null);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert - verify slide isn't drawn
        verify(mockGraphics, never()).drawString(contains("Slide"), anyInt(), anyInt());
    }
}
