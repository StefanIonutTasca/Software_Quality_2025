package org.jabberpoint.src.app;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.jabberpoint.src.io.PresentationLoader;
import org.jabberpoint.src.io.PresentationLoaderFactory;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Style;
import org.jabberpoint.src.ui.SlideViewerFrame;

/**
 * JabberPoint Main Programma
 *
 * <p>This program is distributed under the terms of the accompanying COPYRIGHT.txt file (which is
 * NOT the GNU General Public License). Please read it. Your use of the software constitutes
 * acceptance of the terms in the COPYRIGHT.txt file.
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class JabberPoint {
  protected static final String IOERR = "IO Error: ";
  protected static final String JABERR = "Jabberpoint Error ";
  protected static final String JABVERSION = "Jabberpoint 1.6 - OU version";

  /** The Main Program */
  public static void main(String argv[]) {

    // Initialize styles using Singleton pattern
    Style.getInstance();

    // Create the presentation
    Presentation presentation = new Presentation();

    // Create the frame with the presentation
    new SlideViewerFrame(JABVERSION, presentation);

    try {
      // Use the Strategy pattern with Factory Method to load the presentation
      PresentationLoader loader;

      if (argv.length == 0) {
        // Load a demo presentation
        loader = PresentationLoaderFactory.createLoader("demo");
      } else {
        // Load from XML file
        loader = PresentationLoaderFactory.createLoader("xml");
      }

      loader.loadPresentation(presentation, argv.length == 0 ? "" : argv[0]);
      presentation.setSlideNumber(0);
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(null, IOERR + ex, JABERR, JOptionPane.ERROR_MESSAGE);
    }
  }
}
