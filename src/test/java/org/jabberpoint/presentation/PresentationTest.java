package org.jabberpoint.presentation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PresentationTest {

  private Presentation presentation;
  private Slide slide1;
  private Slide slide2;

  @BeforeEach
  public void setUp() {
    presentation = new Presentation("Test Presentation");

    slide1 = new Slide("Slide 1");
    slide1.addTextItem("Item 1.1");
    slide1.addTextItem("Item 1.2");

    slide2 = new Slide("Slide 2");
    slide2.addTextItem("Item 2.1");

    presentation.addSlide(slide1);
    presentation.addSlide(slide2);
  }

  @Test
  public void testInitialState() {
    assertEquals("Test Presentation", presentation.getTitle());
    assertEquals(2, presentation.getSlideCount());
    assertEquals(1, presentation.getCurrentSlideNumber()); // 1-based indexing
  }

  @Test
  public void testGetCurrentSlide() {
    Slide currentSlide = presentation.getCurrentSlide();
    assertNotNull(currentSlide);
    assertEquals("Slide 1", currentSlide.getTitle());
    assertEquals(2, currentSlide.getItemCount());
  }

  @Test
  public void testNextSlide() {
    assertTrue(presentation.nextSlide());
    assertEquals(2, presentation.getCurrentSlideNumber());
    assertEquals("Slide 2", presentation.getCurrentSlide().getTitle());

    // Already at last slide, should return false
    assertFalse(presentation.nextSlide());
    assertEquals(2, presentation.getCurrentSlideNumber());
  }

  @Test
  public void testPreviousSlide() {
    // Move to slide 2 first
    presentation.nextSlide();
    assertEquals(2, presentation.getCurrentSlideNumber());

    // Now go back to slide 1
    assertTrue(presentation.previousSlide());
    assertEquals(1, presentation.getCurrentSlideNumber());
    assertEquals("Slide 1", presentation.getCurrentSlide().getTitle());

    // Already at first slide, should return false
    assertFalse(presentation.previousSlide());
    assertEquals(1, presentation.getCurrentSlideNumber());
  }

  @Test
  public void testSetTitle() {
    presentation.setTitle("New Title");
    assertEquals("New Title", presentation.getTitle());
  }

  @Test
  public void testEmptyPresentation() {
    Presentation emptyPresentation = new Presentation("Empty");

    // Verify basic properties
    assertEquals("Empty", emptyPresentation.getTitle());
    assertNull(emptyPresentation.getCurrentSlide());
    assertEquals(0, emptyPresentation.getSlideCount());
    assertEquals(1, emptyPresentation.getCurrentSlideNumber()); // Should still report as 1

    // Navigation should fail on empty presentation
    assertFalse(emptyPresentation.nextSlide());
    assertFalse(emptyPresentation.previousSlide());

    // Verify that state remains consistent after navigation attempts
    assertNull(emptyPresentation.getCurrentSlide());
    assertEquals(0, emptyPresentation.getSlideCount());
  }
}
