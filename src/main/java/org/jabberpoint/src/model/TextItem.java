package org.jabberpoint.src.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A tekst item.
 *
 * <p>A TextItem has drawingfunctionality.
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class TextItem extends SlideItem {
  private String text;

  private static final String EMPTYTEXT = "No Text Given";

  // a textitem of level level, with the text string
  public TextItem(int level, String string) {
    super(level);
    text = string;
  }

  // an empty textitem
  public TextItem() {
    this(0, EMPTYTEXT);
  }

  // give the text
  public String getText() {
    return text == null ? "" : text;
  }

  // geef de AttributedString voor het item
  public AttributedString getAttributedString(Style style, float scale) {
    String textToUse = getText();
    if (textToUse.isEmpty()) {
      textToUse = " "; // Use a space for empty text to avoid IllegalArgumentException
    }
    AttributedString attrStr = new AttributedString(textToUse);
    attrStr.addAttribute(TextAttribute.FONT, style.getFont(scale), 0, textToUse.length());
    return attrStr;
  }

  // give the bounding box of the item
  public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle) {
    if (g == null) {
      // Return a default bounding box if no graphics context
      return new Rectangle((int) (myStyle.indent * scale), 0, 10, (int) (myStyle.leading * scale));
    }

    List<TextLayout> layouts = getLayouts(g, myStyle, scale);
    int xsize = 0, ysize = (int) (myStyle.leading * scale);
    Iterator<TextLayout> iterator = layouts.iterator();
    while (iterator.hasNext()) {
      TextLayout layout = iterator.next();
      Rectangle2D bounds = layout.getBounds();
      if (bounds.getWidth() > xsize) {
        xsize = (int) bounds.getWidth();
      }
      if (bounds.getHeight() > 0) {
        ysize += bounds.getHeight();
      }
      ysize += layout.getLeading() + layout.getDescent();
    }
    return new Rectangle((int) (myStyle.indent * scale), 0, xsize, ysize);
  }

  // draw the item
  public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver o) {
    if (g == null || getText().isEmpty()) {
      return;
    }

    List<TextLayout> layouts = getLayouts(g, myStyle, scale);
    Point pen = new Point(x + (int) (myStyle.indent * scale), y + (int) (myStyle.leading * scale));
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(myStyle.color);
    Iterator<TextLayout> it = layouts.iterator();
    while (it.hasNext()) {
      TextLayout layout = it.next();
      pen.y += layout.getAscent();
      layout.draw(g2d, pen.x, pen.y);
      pen.y += layout.getDescent();
    }
  }

  private List<TextLayout> getLayouts(Graphics g, Style s, float scale) {
    List<TextLayout> layouts = new ArrayList<TextLayout>();
    if (g == null || getText().isEmpty()) {
      return layouts; // Return empty list if no graphics context or empty text
    }

    AttributedString attrStr = getAttributedString(s, scale);
    Graphics2D g2d = (Graphics2D) g;
    FontRenderContext frc = g2d.getFontRenderContext();
    LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
    float wrappingWidth = (Slide.WIDTH - s.indent) * scale;
    while (measurer.getPosition() < getText().length()) {
      TextLayout layout = measurer.nextLayout(wrappingWidth);
      layouts.add(layout);
    }
    return layouts;
  }

  public String toString() {
    return "TextItem[" + getLevel() + "," + getText() + "]";
  }
}
