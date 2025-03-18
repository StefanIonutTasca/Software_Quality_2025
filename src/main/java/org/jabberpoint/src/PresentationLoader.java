package org.jabberpoint.src;

import java.io.IOException;

/**
 * Strategy pattern interface for loading presentations from different sources
 * This interface defines the strategy for loading presentation content
 */
public interface PresentationLoader {
    /**
     * Loads a presentation from a source
     * @param presentation The presentation to load into
     * @param source The source identifier (e.g., filename)
     * @throws IOException If there's an error loading the presentation
     */
    void loadPresentation(Presentation presentation, String source) throws IOException;
}
