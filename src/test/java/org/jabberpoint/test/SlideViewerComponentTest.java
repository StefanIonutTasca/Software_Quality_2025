package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SlideViewerComponent class
 */
class SlideViewerComponentTest {

    private SlideViewerComponent component;
    
    @Mock
    private Presentation mockPresentation;
    
    @Mock
    private JFrame mockFrame;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private Slide mockSlide;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        component = new SlideViewerComponent(mockPresentation, mockFrame);
    }

    @Test
    @DisplayName("Should register as observer when constructed")
    void constructorShouldRegisterAsObserver() {
        // Assert
        verify(mockPresentation, times(1)).addObserver(component);
    }
    
    @Test
    @DisplayName("Should set background color when constructed")
    void constructorShouldSetBackgroundColor() {
        // Assert
        assertNotNull(component.getBackground(), "Background color should be set");
    }
    
    @Test
    @DisplayName("Should have correct preferred size")
    void getPreferredSizeShouldReturnCorrectDimension() {
        // Act
        Dimension dim = component.getPreferredSize();
        
        // Assert
        assertEquals(Slide.WIDTH, dim.width, "Width should be Slide.WIDTH");
        assertEquals(Slide.HEIGHT, dim.height, "Height should be Slide.HEIGHT");
    }
    
    @Test
    @DisplayName("Update should set slide and repaint")
    void updateShouldSetSlideAndRepaint() throws Exception {
        // Arrange
        // Create a spy to verify repaint is called
        SlideViewerComponent spyComponent = spy(component);
        
        // Act
        spyComponent.update(mockPresentation, mockSlide);
        
        // Assert
        verify(spyComponent, times(1)).repaint();
        verify(mockFrame, times(1)).setTitle(any());
        
        // Verify slide was set using reflection
        Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
        slideField.setAccessible(true);
        assertEquals(mockSlide, slideField.get(spyComponent), "Slide field should be set to mockSlide");
    }
    
    @Test
    @DisplayName("Update with null slide should only repaint")
    void updateWithNullSlideShouldOnlyRepaint() {
        // Arrange
        SlideViewerComponent spyComponent = spy(component);
        
        // Act
        spyComponent.update(mockPresentation, null);
        
        // Assert
        verify(spyComponent, times(1)).repaint();
        verify(mockFrame, never()).setTitle(any());
    }
    
    @Test
    @DisplayName("PaintComponent should draw slide if available")
    void paintComponentShouldDrawSlideIfAvailable() throws Exception {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(2);
        when(mockPresentation.getSize()).thenReturn(5);
        when(mockPresentation.getTitle()).thenReturn("Test Presentation");
        
        // Set the slide field using reflection
        Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
        slideField.setAccessible(true);
        slideField.set(component, mockSlide);
        
        // Act
        component.paintComponent(mockGraphics);
        
        // Assert
        verify(mockGraphics, atLeastOnce()).setColor(any());
        verify(mockGraphics, times(1)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(mockGraphics, times(1)).setFont(any());
        verify(mockGraphics, times(1)).drawString(contains("Slide 3 of 5"), anyInt(), anyInt());
        verify(mockSlide, times(1)).draw(eq(mockGraphics), any(), eq(component));
    }
    
    @Test
    @DisplayName("PaintComponent should not draw slide if not available")
    void paintComponentShouldNotDrawSlideIfNotAvailable() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(-1);
        
        // Act
        component.paintComponent(mockGraphics);
        
        // Assert
        verify(mockGraphics, atLeastOnce()).setColor(any());
        verify(mockGraphics, times(1)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(mockSlide, never()).draw(any(), any(), any());
    }
}
