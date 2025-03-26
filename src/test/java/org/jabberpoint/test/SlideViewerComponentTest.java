package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JFrame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SlideViewerComponent class
 */
public class SlideViewerComponentTest {
    
    private SlideViewerComponent slideViewerComponent;
    private Presentation mockPresentation;
    private JFrame mockFrame;
    private Slide testSlide;
    private Graphics2D mockGraphics;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockPresentation = mock(Presentation.class);
        mockFrame = mock(JFrame.class);
        testSlide = mock(Slide.class);
        mockGraphics = mock(Graphics2D.class);
        
        // Mock the AffineTransform to avoid NullPointerException
        java.awt.geom.AffineTransform mockTransform = mock(java.awt.geom.AffineTransform.class);
        when(mockTransform.getScaleX()).thenReturn(1.0);
        when(mockGraphics.getTransform()).thenReturn(mockTransform);
        
        // Mock presentation methods
        when(mockPresentation.getCurrentSlide()).thenReturn(testSlide);
        
        // Create the component to test
        slideViewerComponent = new SlideViewerComponent(mockPresentation, mockFrame);
    }

    @Test
    @DisplayName("Should create component with correct preferred size")
    void shouldCreateComponentWithCorrectPreferredSize() {
        // Act
        Dimension preferredSize = slideViewerComponent.getPreferredSize();
        
        // Assert
        assertNotNull(preferredSize);
        assertEquals(1200, preferredSize.width);
        assertEquals(800, preferredSize.height);
    }
    
    @Test
    @DisplayName("Should register as observer on presentation")
    void shouldRegisterAsObserverOnPresentation() {
        // Assert
        verify(mockPresentation).addObserver(slideViewerComponent);
    }
    
    @Test
    @DisplayName("Should update slide and repaint when notified")
    void shouldUpdateSlideAndRepaintWhenNotified() {
        // Arrange
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        doNothing().when(spyComponent).repaint();
        when(mockPresentation.getTitle()).thenReturn("Test Presentation");
        
        // Act
        spyComponent.update(mockPresentation, testSlide);
        
        // Assert
        verify(spyComponent).repaint();
        verify(mockFrame).setTitle("Test Presentation");
    }
    
    @Test
    @DisplayName("Should handle null slide in update")
    void shouldHandleNullSlideInUpdate() {
        // Arrange
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        doNothing().when(spyComponent).repaint();
        
        // Act
        spyComponent.update(mockPresentation, null);
        
        // Assert
        verify(spyComponent).repaint();
        // Title should not be updated when slide is null
        verify(mockFrame, never()).setTitle(anyString());
    }
    
    @Test
    @DisplayName("Should paint component correctly")
    void shouldPaintComponentCorrectly() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(2);
        when(mockPresentation.getSize()).thenReturn(5);
        
        // Mock the component size and methods
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        doReturn(new Dimension(800, 600)).when(spyComponent).getSize();
        doReturn(800).when(spyComponent).getWidth();
        doReturn(600).when(spyComponent).getHeight();
        
        // We need to make sure the slide is not null
        when(mockPresentation.getCurrentSlide()).thenReturn(testSlide);
        
        // Act
        spyComponent.paintComponent(mockGraphics);
        
        // Verify the basic background setup
        verify(mockGraphics).setColor(Color.white);
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Verify the text rendering
        verify(mockGraphics).setFont(any(Font.class));
        verify(mockGraphics).setColor(Color.black);
        verify(mockGraphics).drawString(contains("Slide 3 of 5"), anyInt(), anyInt());
        
        // Verify the slide is drawn
        verify(testSlide).draw(eq(mockGraphics), any(Rectangle.class), eq(spyComponent));
    }
    
    @Test
    @DisplayName("Should not draw slide when slide number is negative")
    void shouldNotDrawSlideWhenSlideNumberIsNegative() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(-1);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert - only background should be drawn, not the slide
        verify(mockGraphics).setColor(Color.white);
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(mockGraphics, never()).drawString(anyString(), anyInt(), anyInt());
        verify(testSlide, never()).draw(any(), any(), any());
    }
    
    @Test
    @DisplayName("Should not draw slide when current slide is null")
    void shouldNotDrawSlideWhenCurrentSlideIsNull() {
        // Arrange
        when(mockPresentation.getCurrentSlide()).thenReturn(null);
        
        // Act
        slideViewerComponent.update(mockPresentation, null);
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert - only background should be drawn, not the slide
        verify(mockGraphics).setColor(Color.white);
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(mockGraphics, never()).drawString(anyString(), anyInt(), anyInt());
    }
    
    // Helper method to match strings that contain a substring
    private static String contains(final String substring) {
        return argThat(argument -> argument != null && argument.contains(substring));
    }
}
