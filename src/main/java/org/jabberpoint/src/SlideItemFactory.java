package org.jabberpoint.src;

/**
 * Factory Method pattern implementation for creating different types of SlideItems
 */
public class SlideItemFactory {
    
    /**
     * Creates a SlideItem based on the type specified
     * @param type The type of SlideItem to create
     * @param level The level of the SlideItem
     * @param content The content for the SlideItem (text or image path)
     * @return A SlideItem instance of the appropriate type
     */
    public static SlideItem createSlideItem(String type, int level, String content) {
        if (type.equalsIgnoreCase("text")) {
            return new TextItem(level, content);
        } else if (type.equalsIgnoreCase("image")) {
            return new BitmapItem(level, content);
        } else {
            throw new IllegalArgumentException("Unknown SlideItem type: " + type);
        }
    }
}
