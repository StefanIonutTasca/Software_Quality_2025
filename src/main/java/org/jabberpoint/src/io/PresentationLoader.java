package org.jabberpoint.src.io;
import org.jabberpoint.src.model.Presentation;

import java.io.IOException;

/**
 * Interface for presentation loaders
 * Part of the Strategy pattern implementation
 */
public interface PresentationLoader {
    /**
     * Loads a presentation from a source
     * @param presentation The presentation to load into
     * @param source The source to load from (could be a filename or other identifier)
     * @throws IOException If there is a problem loading the presentation
     */
    void loadPresentation(Presentation presentation, String source) throws IOException;
}




