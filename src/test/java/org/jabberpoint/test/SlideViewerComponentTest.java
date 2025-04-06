package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import javax.swing.JFrame;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;
import org.jabberpoint.src.ui.SlideViewerComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SlideViewerComponentTest {

  @Mock private Presentation mockPresentation;

  @Mock private JFrame mockFrame;

  @Mock private Slide mockSlide;

  @Mock private Graphics mockGraphics;

  private SlideViewerComponent viewerComponent;

  @BeforeEach
  void setUp() {
    if (!GraphicsEnvironment.isHeadless()) {
      // Setup Presentation mock behavior
      when(mockPresentation.getSlideNumber()).thenReturn(0);
      when(mockPresentation.getSize()).thenReturn(3);
      when(mockPresentation.getTitle()).thenReturn("Test Presentation");

      viewerComponent = new SlideViewerComponent(mockPresentation, mockFrame);
    }
  }

  @Test
  @DisplayName("Constructor should initialize the component correctly")
  void constructorShouldInitializeComponentCorrectly() {
    // Skip test in headless environment
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }

    // Verify background color is set
    assertEquals(Color.white, viewerComponent.getBackground(), "Background color should be white");

    // Verify that the component registered itself as an observer
    verify(mockPresentation).addObserver(viewerComponent);

    // Verify the labelFont is initialized properly using reflection
    try {
      Field labelFontField = SlideViewerComponent.class.getDeclaredField("labelFont");
      labelFontField.setAccessible(true);
      Font labelFont = (Font) labelFontField.get(viewerComponent);

      assertNotNull(labelFont, "Label font should not be null");
      assertEquals("Dialog", labelFont.getFamily(), "Font family should be Dialog");
      assertEquals(Font.BOLD, labelFont.getStyle(), "Font style should be BOLD");
      assertEquals(10, labelFont.getSize(), "Font size should be 10");
    } catch (Exception e) {
      fail("Failed to access labelFont field: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("getPreferredSize should return the correct dimensions")
  void getPreferredSizeShouldReturnCorrectDimensions() {
    // Skip test in headless environment
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }

    Dimension preferredSize = viewerComponent.getPreferredSize();

    assertEquals(Slide.WIDTH, preferredSize.width, "Preferred width should match Slide.WIDTH");
    assertEquals(Slide.HEIGHT, preferredSize.height, "Preferred height should match Slide.HEIGHT");
  }

  @Test
  @DisplayName("update should set the slide and repaint the component")
  void updateShouldSetSlideAndRepaintComponent() {
    // Skip test in headless environment
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }

    // Create a spy of the component to verify repaint is called
    SlideViewerComponent spy = spy(viewerComponent);

    // Call update with a slide
    spy.update(mockPresentation, mockSlide);

    // Verify the component is repainted
    verify(spy).repaint();

    // Verify the frame title is updated
    verify(mockFrame).setTitle("Test Presentation");

    // Verify the slide is stored using reflection
    try {
      Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
      slideField.setAccessible(true);
      Slide storedSlide = (Slide) slideField.get(spy);

      assertEquals(mockSlide, storedSlide, "The slide should be stored in the component");
    } catch (Exception e) {
      fail("Failed to access slide field: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("update should handle null slide")
  void updateShouldHandleNullSlide() {
    // Skip test in headless environment
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }

    // Create a spy of the component to verify repaint is called
    SlideViewerComponent spy = spy(viewerComponent);

    // Call update with null slide
    spy.update(mockPresentation, null);

    // Verify the component is repainted
    verify(spy).repaint();

    // Verify the frame title is updated
    verify(mockFrame).setTitle("Test Presentation");
  }

  @Test
  @DisplayName("paintComponent should render the slide correctly")
  void paintComponentShouldRenderSlideCorrectly() {
    // Skip test in headless environment
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }

    // Set up the slide in the component using update
    viewerComponent.update(mockPresentation, mockSlide);

    // Call paintComponent
    viewerComponent.paintComponent(mockGraphics);

    // Verify graphics operations
    verify(mockGraphics).setColor(Color.white);
    verify(mockGraphics, times(1)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    verify(mockGraphics).setFont(any(Font.class));
    verify(mockGraphics).setColor(Color.black);
    verify(mockGraphics).drawString(contains("Slide 1 of 3"), anyInt(), anyInt());

    // Verify slide.draw was called
    verify(mockSlide).draw(eq(mockGraphics), any(Rectangle.class), eq(viewerComponent));
  }

  @Test
  @DisplayName("paintComponent should handle null slide or invalid slide number")
  void paintComponentShouldHandleNullSlideOrInvalidSlideNumber() {
    // Skip test in headless environment
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }

    // Set up mock to return negative slide number (invalid)
    when(mockPresentation.getSlideNumber()).thenReturn(-1);

    // Don't set a slide in the component

    // Call paintComponent
    viewerComponent.paintComponent(mockGraphics);

    // Verify basic graphics operations
    verify(mockGraphics).setColor(Color.white);
    verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), anyInt());

    // Verify that no other operations are performed
    verify(mockGraphics, never()).drawString(anyString(), anyInt(), anyInt());
    verify(mockSlide, never()).draw(any(), any(), any());
  }
}
