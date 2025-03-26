package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentMatcher;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

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
    private Graphics mockGraphics;
    
    @BeforeEach
    void setUp() {
        // Ensure styles are created for other tests
        org.jabberpoint.src.Style.createStyles();
        
        mockPresentation = mock(Presentation.class);
        mockFrame = mock(JFrame.class);
        mockGraphics = mock(Graphics.class);
        testSlide = new Slide();
        testSlide.setTitle("Test Slide");
        
        // Mock some basic behavior
        when(mockPresentation.getCurrentSlide()).thenReturn(testSlide);
        when(mockPresentation.getSlideNumber()).thenReturn(0);
        when(mockPresentation.getSize()).thenReturn(1);
        when(mockPresentation.getTitle()).thenReturn("Test Presentation");
        
        slideViewerComponent = new SlideViewerComponent(mockPresentation, mockFrame);
    }
    
    @Test
    @DisplayName("Should create component with correct preferred size")
    void shouldCreateComponentWithCorrectPreferredSize() {
        // Act
        Dimension dimension = slideViewerComponent.getPreferredSize();
        
        // Assert
        assertEquals(Slide.WIDTH, dimension.width);
        assertEquals(Slide.HEIGHT, dimension.height);
    }
    
    @Test
    @DisplayName("Should register as observer on presentation")
    void shouldRegisterAsObserverOnPresentation() {
        // Verify that the component registered itself as an observer
        verify(mockPresentation).addObserver(slideViewerComponent);
    }
    
    @Test
    @DisplayName("Should update slide and repaint when notified")
    void shouldUpdateSlideAndRepaintWhenNotified() {
        // Arrange
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        Slide newSlide = new Slide();
        newSlide.setTitle("New Test Slide");
        
        // Act
        spyComponent.update(mockPresentation, newSlide);
        
        // Assert
        verify(mockFrame).setTitle(mockPresentation.getTitle());
        verify(spyComponent).repaint();
    }
    
    @Test
    @DisplayName("Should handle null slide in update")
    void shouldHandleNullSlideInUpdate() {
        // Arrange
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        
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
        
        // Act
        spyComponent.paintComponent(mockGraphics);
        
        // Assert
        // Verify background is filled
        verify(mockGraphics).setColor(Color.white);
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Verify color is set for text
        verify(mockGraphics).setColor(Color.black);
        
        // Verify font is set
        verify(mockGraphics).setFont(any(Font.class));
        
        // Verify slide number text is drawn
        verify(mockGraphics).drawString(matches("Slide 3 of 5"), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should not draw slide when slide number is negative")
    void shouldNotDrawSlideWhenSlideNumberIsNegative() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(-1);
        
        // Act
        slideViewerComponent.paintComponent(mockGraphics);
        
        // Assert
        // Verify background is filled
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Verify no text or slide is drawn
        verify(mockGraphics, never()).drawString(anyString(), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should not draw slide when current slide is null")
    void shouldNotDrawSlideWhenCurrentSlideIsNull() {
        // Arrange
        SlideViewerComponent svComponent = new SlideViewerComponent(mockPresentation, mockFrame);
        when(mockPresentation.getCurrentSlide()).thenReturn(null);
        
        // Reset mock to clear the verification count from constructor
        clearInvocations(mockGraphics);
        
        // Act
        svComponent.paintComponent(mockGraphics);
        
        // Assert
        // Verify background is filled
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Verify no text is drawn if slide is null
        verify(mockGraphics, never()).drawString(anyString(), anyInt(), anyInt());
    }
    
    // Helper method to match strings that contain a substring
    private String matches(final String substring) {
        return argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String argument) {
                return argument != null && argument.contains(substring);
            }
            
            @Override
            public String toString() {
                return "String containing [" + substring + "]";
            }
        });
    }
}
