package org.jabberpoint.src;

/**
 * Factory for creating slide items
 * Implements the Factory Method pattern
 */
public class SlideItemFactory {
    
    /**
     * Creates a SlideItem based on the type
     * @param type The type of slide item to create ("text" or "image")
     * @param level The indentation level of the slide item
     * @param content The content for the slide item (text content or image filename)
     * @return A SlideItem instance
     */
    public static SlideItem createSlideItem(String type, int level, String content) {
        if (type.equalsIgnoreCase("text")) {
            return new TextItem(level, content);
        } else if (type.equalsIgnoreCase("image")) {
            return new BitmapItem(level, content);
        } else {
            throw new IllegalArgumentException("Unknown slide item type: " + type);
        }
    }
}
