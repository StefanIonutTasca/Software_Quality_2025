package org.jabberpoint.src.io;
import org.jabberpoint.src.io.DemoPresentationLoader;
import org.jabberpoint.src.io.XMLPresentationLoader;
import org.jabberpoint.src.io.PresentationLoader;

/**
 * Factory for creating PresentationLoader instances
 * Implements the Factory Method pattern
 */
public class PresentationLoaderFactory {
    
    /**
     * Creates a PresentationLoader based on the type
     * @param type The type of loader to create
     * @return A PresentationLoader instance
     */
    public static PresentationLoader createLoader(String type) {
        if (type.equalsIgnoreCase("xml")) {
            return new XMLPresentationLoader();
        } else if (type.equalsIgnoreCase("demo")) {
            return new DemoPresentationLoader();
        } else {
            throw new IllegalArgumentException("Unknown loader type: " + type);
        }
    }
}




