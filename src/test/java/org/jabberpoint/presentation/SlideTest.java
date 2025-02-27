package org.jabberpoint.presentation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SlideTest {

  @Test
  public void testSlideCreation() {
    Slide slide = new Slide("Test Slide");
    assertEquals("Test Slide", slide.getTitle());
    assertEquals(0, slide.getItemCount());
  }

  @Test
  public void testAddTextItem() {
    Slide slide = new Slide("Test Slide");
    slide.addTextItem("Item 1");
    slide.addTextItem("Item 2");

    assertEquals(2, slide.getItemCount());
    assertEquals("Item 1", slide.getTextItems().get(0));
    assertEquals("Item 2", slide.getTextItems().get(1));
  }

  @Test
  public void testSetTitle() {
    Slide slide = new Slide("Old Title");
    assertEquals("Old Title", slide.getTitle());

    slide.setTitle("New Title");
    assertEquals("New Title", slide.getTitle());
  }
}
