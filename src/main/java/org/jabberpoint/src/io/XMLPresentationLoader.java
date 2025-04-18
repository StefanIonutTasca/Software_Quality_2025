package org.jabberpoint.src.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jabberpoint.src.model.BitmapItem;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;
import org.jabberpoint.src.model.SlideItem;
import org.jabberpoint.src.model.SlideItemFactory;
import org.jabberpoint.src.model.TextItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** Strategy implementation for loading presentations from XML files */
public class XMLPresentationLoader implements PresentationLoader {
  /** Default API to use. */
  protected static final String DEFAULT_API_TO_USE = "dom";

  /** names of xml tags or attributes */
  protected static final String SHOWTITLE = "showtitle";

  protected static final String SLIDETITLE = "title";
  protected static final String SLIDE = "slide";
  protected static final String ITEM = "item";
  protected static final String LEVEL = "level";
  protected static final String KIND = "kind";
  protected static final String TEXT = "text";
  protected static final String IMAGE = "image";

  /** Loads a presentation from an XML file */
  @Override
  public void loadPresentation(Presentation presentation, String filename) throws IOException {
    int slideNumber, itemNumber, max = 0, maxItems = 0;
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = builder.parse(new File(filename)); // parse an XML file
      Element doc = document.getDocumentElement();
      presentation.setTitle(getTitle(doc, SHOWTITLE));

      NodeList slides = doc.getElementsByTagName(SLIDE);
      max = slides.getLength();
      for (slideNumber = 0; slideNumber < max; slideNumber++) {
        Element xmlSlide = (Element) slides.item(slideNumber);
        Slide slide = new Slide();
        slide.setTitle(getTitle(xmlSlide, SLIDETITLE));
        presentation.append(slide);

        NodeList slideItems = xmlSlide.getElementsByTagName(ITEM);
        maxItems = slideItems.getLength();
        for (itemNumber = 0; itemNumber < maxItems; itemNumber++) {
          Element item = (Element) slideItems.item(itemNumber);
          loadSlideItem(slide, item);
        }
      }
    } catch (IOException iox) {
      System.err.println(iox.toString());
      throw iox;
    } catch (SAXException sax) {
      System.err.println(sax.getMessage());
      throw new IOException("Parse error: " + sax.getMessage());
    } catch (ParserConfigurationException pcx) {
      System.err.println(pcx.getMessage());
      throw new IOException("Parser Configuration error: " + pcx.getMessage());
    }
  }

  /** Loads a slide item from an XML element */
  protected void loadSlideItem(Slide slide, Element item) {
    int level = 1; // default
    NamedNodeMap attributes = item.getAttributes();
    String leveltext = attributes.getNamedItem(LEVEL).getTextContent();
    if (leveltext != null) {
      try {
        level = Integer.parseInt(leveltext);
      } catch (NumberFormatException x) {
        System.err.println("Number format exception for level: " + leveltext);
      }
    }
    String type = attributes.getNamedItem(KIND).getTextContent();
    if (TEXT.equals(type)) {
      slide.append(SlideItemFactory.createSlideItem("text", level, item.getTextContent()));
    } else {
      if (IMAGE.equals(type)) {
        slide.append(SlideItemFactory.createSlideItem("image", level, item.getTextContent()));
      } else {
        System.err.println("Unknown element type: " + type);
      }
    }
  }

  /** Gets the title from an XML element */
  protected String getTitle(Element element, String tagName) {
    NodeList titles = element.getElementsByTagName(tagName);
    return titles.item(0).getTextContent();
  }

  /** Saves a presentation to an XML file */
  public void savePresentation(Presentation presentation, String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(filename));
    out.println("<?xml version=\"1.0\"?>");
    // Removed DOCTYPE declaration to avoid dependency on jabberpoint.dtd
    out.println("<presentation>");
    out.print("<showtitle>");
    out.print(presentation.getTitle());
    out.println("</showtitle>");
    for (int slideNumber = 0; slideNumber < presentation.getSize(); slideNumber++) {
      Slide slide = presentation.getSlide(slideNumber);
      out.println("<slide>");
      out.println("<title>" + slide.getTitle() + "</title>");
      for (int itemNumber = 0; itemNumber < slide.getSize(); itemNumber++) {
        SlideItem slideItem = slide.getSlideItem(itemNumber);
        out.print("<item kind=");
        if (slideItem instanceof TextItem) {
          out.print("\"text\" level=\"" + slideItem.getLevel() + "\">");
          out.print(((TextItem) slideItem).getText());
        } else {
          if (slideItem instanceof BitmapItem) {
            out.print("\"image\" level=\"" + slideItem.getLevel() + "\">");
            out.print(((BitmapItem) slideItem).getImageName());
          } else {
            System.out.println(
                "Ignoring unknown SlideItem type: " + slideItem.getClass().getName());
          }
        }
        out.println("</item>");
      }
      out.println("</slide>");
    }
    out.println("</presentation>");
    out.close();
  }
}
