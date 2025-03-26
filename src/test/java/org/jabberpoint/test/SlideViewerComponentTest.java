package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;

import javax.swing.JFrame;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for SlideViewerComponent class
 */
class SlideViewerComponentTest {

    private SlideViewerComponent slideViewerComponent;
    private Presentation presentation;
    private JFrame mockFrame;
    private Graphics mockGraphics;
    private Graphics2D mockGraphics2D;
    private FontRenderContext frc;

    @BeforeEach
    void setUp() {
        // Create real presentation
        presentation = new Presentation();
        presentation.setTitle("Test Presentation");
        
        // Create a mock JFrame
        mockFrame = Mockito.mock(JFrame.class);
        
        // Create the component under test
        slideViewerComponent = new SlideViewerComponent(presentation, mockFrame);
        
        // Set up graphics mocks for painting tests
        mockGraphics = Mockito.mock(Graphics.class);
        mockGraphics2D = Mockito.mock(Graphics2D.class);
        
        // Create a real FontRenderContext for text rendering
        AffineTransform at = new AffineTransform();
        frc = new FontRenderContext(at, true, true);
        when(mockGraphics2D.getFontRenderContext()).thenReturn(frc);
    }

    @Test
    @DisplayName("Should return correct preferred size")
    void shouldReturnCorrectPreferredSize() {
        // Act
        Dimension preferredSize = slideViewerComponent.getPreferredSize();
        
        // Assert
        assertEquals(Slide.WIDTH, preferredSize.width);
        assertEquals(Slide.HEIGHT, preferredSize.height);
    }

    @Test
    @DisplayName("Should update when presentation changes")
    void shouldUpdateWhenPresentationChanges() {
        // Arrange
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Item"));
        presentation.append(slide);
        
        // Act
        presentation.setSlideNumber(0);
        
        // Assert
        verify(mockFrame).setTitle("Test Presentation");
    }
    
    @Test
    @DisplayName("Should handle null slide in update")
    void shouldHandleNullSlideInUpdate() {
        // Act - call update with null slide
        slideViewerComponent.update(presentation, null);
        
        // Assert - should not throw exception and should trigger repaint
        // Verification is implicit since no exception is thrown
    }
    
    @Test
    @DisplayName("Should paint slide correctly")
    void shouldPaintSlideCorrectly() {
        // Arrange
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        presentation.append(slide);
        presentation.setSlideNumber(0);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert
        // Verify background is filled
        verify(mockGraphics).setColor(any());
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Verify slide number is drawn
        verify(mockGraphics).setFont(any());
        verify(mockGraphics).drawString(contains("Slide 1 of 1"), anyInt(), anyInt());
        
        // Verify a drawString was called
        verify(mockGraphics).drawString(anyString(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should handle empty presentation when painting")
    void shouldHandleEmptyPresentationWhenPainting() {
        // Arrange - empty presentation with no slides
        presentation.setSlideNumber(-1);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert - should still fill background but not draw slide
        verify(mockGraphics).setColor(any());
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Should not attempt to draw slide number or slide
        verify(mockGraphics, never()).drawString(contains("Slide"), anyInt(), anyInt());
    }
}
