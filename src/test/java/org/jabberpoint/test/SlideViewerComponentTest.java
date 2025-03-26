package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.argThat;

/**
 * Unit tests for SlideViewerComponent class
 */
public class SlideViewerComponentTest {
    private SlideViewerComponent slideViewerComponent;
    private Presentation mockPresentation;
    private JFrame mockFrame;
    private Slide testSlide;
    private Graphics2D mockGraphics;
    private FontMetrics mockFontMetrics;
    private Font testFont;

    @BeforeEach
    void setUp() {
        // Initialize styles
        Style.createStyles();
        
        // Create test font
        testFont = new Font("SansSerif", Font.PLAIN, 12);
        
        // Create mocks
        mockGraphics = mock(Graphics2D.class);
        mockPresentation = mock(Presentation.class);
        mockFrame = mock(JFrame.class);
        mockFontMetrics = mock(FontMetrics.class);
        
        // Create a real slide for testing
        testSlide = new Slide();
        testSlide.setTitle("Test Slide");
        
        // Setup mock behavior for FontMetrics
        when(mockFontMetrics.stringWidth(anyString())).thenReturn(100);
        when(mockFontMetrics.getHeight()).thenReturn(20);
        when(mockFontMetrics.getAscent()).thenReturn(15);
        when(mockGraphics.getFontMetrics(any(Font.class))).thenReturn(mockFontMetrics);
        
        // Setup mock behavior for Graphics
        when(mockGraphics.create()).thenReturn(mockGraphics);
        doNothing().when(mockGraphics).setColor(any(Color.class));
        doNothing().when(mockGraphics).setFont(any(Font.class));
        doNothing().when(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        doNothing().when(mockGraphics).drawString(anyString(), anyInt(), anyInt());
        
        // Mock AffineTransform
        AffineTransform mockTransform = mock(AffineTransform.class);
        when(mockTransform.getScaleX()).thenReturn(1.0);
        when(mockTransform.getScaleY()).thenReturn(1.0);
        when(mockTransform.getTranslateX()).thenReturn(0.0);
        when(mockTransform.getTranslateY()).thenReturn(0.0);
        when(mockGraphics.getTransform()).thenReturn(mockTransform);
        
        // Mock FontRenderContext
        FontRenderContext mockFrc = mock(FontRenderContext.class);
        when(mockGraphics.getFontRenderContext()).thenReturn(mockFrc);
        
        // Create the component under test
        slideViewerComponent = new SlideViewerComponent(mockPresentation, mockFrame);
    }
    
    @Test
    @DisplayName("Should create component with correct preferred size")
    void shouldCreateComponentWithCorrectPreferredSize() {
        // Assert
        Dimension size = slideViewerComponent.getPreferredSize();
        assertEquals(SlideViewerComponent.WIDTH, size.width);
        assertEquals(SlideViewerComponent.HEIGHT, size.height);
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
        
        // Act
        spyComponent.update(mockPresentation, testSlide);
        
        // Assert
        verify(spyComponent).repaint();
    }
    
    @Test
    @DisplayName("Should handle null slide in update")
    void shouldHandleNullSlideInUpdate() {
        // Arrange
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        
        // Act - should not throw exception
        assertDoesNotThrow(() -> spyComponent.update(mockPresentation, null));
        
        // Assert - should still repaint
        verify(spyComponent).repaint();
    }
    
    @Test
    @DisplayName("Should paint component correctly")
    void shouldPaintComponentCorrectly() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(2);
        when(mockPresentation.getSize()).thenReturn(5);
        
        // Create a spy of the component to verify and mock some methods
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        doReturn(new Dimension(800, 600)).when(spyComponent).getSize();
        
        // Ensure the slide is set in the component before testing
        // Need to use a real slide with a real implementation
        Slide realSlide = new Slide();
        realSlide.setTitle("Test Slide");
        
        // Set up the style for the slide to avoid NPE
        try {
            for (int i = 0; i <= 5; i++) {
                Style style = Style.getStyle(i);
                java.lang.reflect.Field fontField = Style.class.getDeclaredField("font");
                fontField.setAccessible(true);
                fontField.set(style, testFont);
            }
        } catch (Exception e) {
            fail("Failed to set font field: " + e.getMessage());
        }
        
        // Set the slide in the component
        spyComponent.update(mockPresentation, realSlide);
        
        // Clear invocations from the update call to isolate paint behavior
        clearInvocations(mockGraphics);
        clearInvocations(realSlide);
        
        // Act
        spyComponent.paintComponent(mockGraphics);
        
        // Verify the basic graphics setup
        verify(mockGraphics).setColor(Color.white);
        verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        
        // Verify the text rendering
        verify(mockGraphics).setFont(any(Font.class));
        verify(mockGraphics).setColor(Color.black);
        verify(mockGraphics).drawString(argThat(contains("Slide 3 of 5")), anyInt(), anyInt());
        
        // Verify the slide is drawn
        verify(realSlide).draw(any(Graphics2D.class), any(Rectangle.class), eq(spyComponent));
    }
    
    @Test
    @DisplayName("Should not draw slide when slide number is negative")
    void shouldNotDrawSlideWhenSlideNumberIsNegative() {
        // Arrange
        when(mockPresentation.getSlideNumber()).thenReturn(-1);
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        
        // We need to set a mock slide in the component
        Slide mockSlide = mock(Slide.class);
        spyComponent.update(mockPresentation, mockSlide);
        clearInvocations(mockSlide);
        
        // Act
        spyComponent.paintComponent(mockGraphics);
        
        // Assert
        verify(mockSlide, never()).draw(any(Graphics2D.class), any(Rectangle.class), any());
    }
    
    @Test
    @DisplayName("Should not draw slide when current slide is null")
    void shouldNotDrawSlideWhenCurrentSlideIsNull() {
        // Arrange
        SlideViewerComponent spyComponent = spy(slideViewerComponent);
        spyComponent.update(mockPresentation, null);
        
        // Act
        spyComponent.paintComponent(mockGraphics);
        
        // Assert - should not throw exception
        // There's no mockSlide to verify, since it's null
        // Just asserting that the method completes without exception
    }
    
    // Helper method to match strings that contain a substring
    private org.mockito.ArgumentMatcher<String> contains(final String substring) {
        return new org.mockito.ArgumentMatcher<String>() {
            public boolean matches(String arg) {
                return arg != null && arg.contains(substring);
            }
            
            public String toString() {
                return "contains(" + substring + ")";
            }
        };
    }
}
