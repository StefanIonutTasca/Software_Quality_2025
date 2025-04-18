package org.jabberpoint.src.model;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Vector;

/**
 * A slide. This class has a drawing functionality.
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class Slide {
  public static final int WIDTH = 1200;
  public static final int HEIGHT = 800;
  protected String title = ""; // title is saved separately and initialized to empty string
  protected Vector<SlideItem> items; // slide items are saved in a Vector

  public Slide() {
    items = new Vector<SlideItem>();
  }

  // Add a slide item
  public void append(SlideItem anItem) {
    items.addElement(anItem);
  }

  // give the title of the slide
  public String getTitle() {
    return title == null ? "" : title;
  }

  // change the title of the slide
  public void setTitle(String newTitle) {
    title = newTitle == null ? "" : newTitle;
  }

  // Create TextItem of String, and add the TextItem
  public void append(int level, String message) {
    append(new TextItem(level, message));
  }

  // give the  SlideItem
  public SlideItem getSlideItem(int number) {
    return (SlideItem) items.elementAt(number);
  }

  // give all SlideItems in a Vector
  public Vector<SlideItem> getSlideItems() {
    return items;
  }

  // give the size of the Slide
  public int getSize() {
    return items.size();
  }

  // draw the slide
  public void draw(Graphics g, Rectangle area, ImageObserver view) {
    float scale = getScale(area);
    int y = area.y;
    // Title is handled separately
    SlideItem slideItem = new TextItem(0, getTitle());
    Style style = Style.getStyle(slideItem.getLevel());
    slideItem.draw(area.x, y, scale, g, style, view);
    y += slideItem.getBoundingBox(g, view, scale, style).height;
    for (int number = 0; number < getSize(); number++) {
      slideItem = (SlideItem) getSlideItems().elementAt(number);
      style = Style.getStyle(slideItem.getLevel());
      slideItem.draw(area.x, y, scale, g, style, view);
      y += slideItem.getBoundingBox(g, view, scale, style).height;
    }
  }

  // Give the scale for drawing
  private float getScale(Rectangle area) {
    return Math.min(
        ((float) area.width) / ((float) WIDTH), ((float) area.height) / ((float) HEIGHT));
  }
}
