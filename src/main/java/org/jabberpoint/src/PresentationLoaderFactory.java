package org.jabberpoint.src;

/**
 * Factory for creating PresentationLoader instances
 * Implements the Factory Method pattern to provide different
 * strategies for loading presentation content
 */
public class PresentationLoaderFactory {
    
    /**
     * Creates a PresentationLoader based on the specified type
     * 
     * @param type The type of loader to create ("xml" or "demo")
     * @return A PresentationLoader instance appropriate for the type
     * @throws IllegalArgumentException if the type is unknown
     */
    public static PresentationLoader createLoader(String type) {
        // Convert to lowercase for case-insensitive comparison
        String loaderType = type.toLowerCase();
        
        // Select the appropriate loader based on type
        if (loaderType.equals("xml")) {
            return new XMLPresentationLoader();
        } else if (loaderType.equals("demo")) {
            return new DemoPresentationLoader();
        } else {
            // If type is not recognized, throw an exception
            throw new IllegalArgumentException("Unknown presentation loader type: " + type);
        }
    }
}
