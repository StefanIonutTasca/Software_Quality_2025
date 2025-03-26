package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JFrame;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Unit tests for SlideViewerComponent class
 */
class SlideViewerComponentTest {

    private JFrame frameMock;
    private Presentation presentationMock;
    private SlideViewerComponent component;
    private Graphics graphicsMock;
    private Slide slideMock;
    
    @BeforeAll
    static void setUpHeadlessMode() {
        // Set headless mode for UI testing
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setUp() {
        frameMock = mock(JFrame.class);
        presentationMock = mock(Presentation.class);
        component = new SlideViewerComponent(presentationMock, frameMock);
        graphicsMock = mock(Graphics.class);
        slideMock = mock(Slide.class);
        
        // Initialize common mock behaviors
        when(presentationMock.getSlideNumber()).thenReturn(0);
        when(presentationMock.getSize()).thenReturn(3);
        when(presentationMock.getTitle()).thenReturn("Test Presentation");
    }

    @Test
    @DisplayName("Should have correct preferred size")
    void shouldHaveCorrectPreferredSize() {
        // Act
        Dimension preferredSize = component.getPreferredSize();
        
        // Assert
        assertEquals(Slide.WIDTH, preferredSize.width);
        assertEquals(Slide.HEIGHT, preferredSize.height);
    }

    @Test
    @DisplayName("Should update and repaint when slide changes")
    void shouldUpdateAndRepaintWhenSlideChanges() {
        // Arrange
        SlideViewerComponent spyComponent = spy(component);
        
        // Act
        spyComponent.update(presentationMock, slideMock);
        
        // Assert
        verify(spyComponent).repaint();
        verify(frameMock).setTitle("Test Presentation");
    }
    
    @Test
    @DisplayName("Should repaint when slide is null")
    void shouldRepaintWhenSlideIsNull() {
        // Arrange
        SlideViewerComponent spyComponent = spy(component);
        
        // Act
        spyComponent.update(presentationMock, null);
        
        // Assert
        verify(spyComponent).repaint();
        verify(frameMock, never()).setTitle(anyString());
    }
    
    @Test
    @DisplayName("Should paint component when slide number is valid")
    void shouldPaintComponentWhenSlideNumberIsValid() {
        // Arrange
        component.update(presentationMock, slideMock);
        
        // Mock component dimensions
        when(presentationMock.getSlideNumber()).thenReturn(1);
        
        // Use Rectangle instead of Dimension for getClipBounds
        when(graphicsMock.getClipBounds()).thenReturn(new Rectangle(0, 0, 800, 600));
        
        // Act
        component.paintComponent(graphicsMock);
        
        // Assert - verify drawing operations
        verify(graphicsMock).setColor(any()); // Background color
        verify(graphicsMock).fillRect(anyInt(), anyInt(), anyInt(), anyInt()); // Fill background
        verify(graphicsMock, times(2)).setColor(any()); // Text color
        verify(graphicsMock).setFont(any());
        verify(graphicsMock).drawString(contains("Slide 2 of 3"), anyInt(), anyInt()); // Check slide number text
        
        // Verify slide is drawn
        ArgumentCaptor<Rectangle> rectangleCaptor = ArgumentCaptor.forClass(Rectangle.class);
        verify(slideMock).draw(eq(graphicsMock), rectangleCaptor.capture(), eq(component));
        Rectangle capturedRect = rectangleCaptor.getValue();
        assertEquals(0, capturedRect.x);
        assertTrue(capturedRect.height > 0);
        assertTrue(capturedRect.width > 0);
    }
    
    @Test
    @DisplayName("Should not draw slide when slide number is invalid")
    void shouldNotDrawSlideWhenSlideNumberIsInvalid() {
        // Arrange - set invalid slide number
        when(presentationMock.getSlideNumber()).thenReturn(-1);
        
        // Act
        component.paintComponent(graphicsMock);
        
        // Assert - verify only background operations happened
        verify(graphicsMock).setColor(any()); // Background color
        verify(graphicsMock).fillRect(anyInt(), anyInt(), anyInt(), anyInt()); // Fill background
        
        // Verify slide is NOT drawn
        verify(graphicsMock, never()).drawString(anyString(), anyInt(), anyInt());
        verifyNoInteractions(slideMock);
    }
    
    @Test
    @DisplayName("Should register as observer to presentation")
    void shouldRegisterAsObserverToPresentation() {
        // This test verifies that the component registers itself as an observer
        // during construction
        
        // Arrange & Act - already done in setUp()
        
        // Assert - verify component was registered
        verify(presentationMock).addObserver(component);
    }
}
