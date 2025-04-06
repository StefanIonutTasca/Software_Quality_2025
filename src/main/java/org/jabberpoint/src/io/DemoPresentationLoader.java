package org.jabberpoint.src.io;

import java.io.IOException;
import org.jabberpoint.src.model.BitmapItem;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;

/** Strategy implementation for loading a demo presentation */
public class DemoPresentationLoader implements PresentationLoader {

  /** Loads a demo presentation */
  @Override
  public void loadPresentation(Presentation presentation, String source) throws IOException {
    presentation.setTitle("Demo Presentation");
    Slide slide;
    slide = new Slide();
    slide.setTitle("JabberPoint");
    slide.append(1, "The Java Presentation Tool");
    slide.append(2, "Copyright (c) 1996-2000: Ian Darwin");
    slide.append(2, "Copyright (c) 2000-now:");
    slide.append(2, "Gert Florijn and Sylvia Stuurman");
    slide.append(4, "Starting JabberPoint without a filename");
    slide.append(4, "shows this presentation");
    slide.append(1, "Navigate:");
    slide.append(3, "Next slide: PgDn or Enter");
    slide.append(3, "Previous slide: PgUp or up-arrow");
    slide.append(3, "Quit: q or Q");
    presentation.append(slide);

    slide = new Slide();
    slide.setTitle("Demonstration of levels and styles");
    slide.append(1, "Level 1");
    slide.append(2, "Level 2");
    slide.append(1, "Again level 1");
    slide.append(1, "Level 1 has style number 1");
    slide.append(2, "Level 2 has style number 2");
    slide.append(3, "This is how level 3 looks like");
    slide.append(4, "And this is level 4");
    presentation.append(slide);

    slide = new Slide();
    slide.setTitle("The third slide");
    slide.append(1, "To open a new presentation,");
    slide.append(2, "use File->Open from the menu.");
    slide.append(1, " ");
    slide.append(1, "This is the end of the presentation.");
    slide.append(new BitmapItem(1, "JabberPoint.jpg"));
    presentation.append(slide);
  }
}
