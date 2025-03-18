public class SlideItemFactory {
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
