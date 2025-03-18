package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SlideItemFactoryTest {

    @Test
    @DisplayName("Test creating TextItem")
    public void testCreateTextItem() {
        SlideItem item = SlideItemFactory.createSlideItem("text", 2, "Test Text");
        assertNotNull(item);
        assertTrue(item instanceof TextItem);
        assertEquals(2, item.getLevel());
        assertEquals("Test Text", ((TextItem) item).getText());
    }

    @Test
    @DisplayName("Test creating BitmapItem")
    public void testCreateBitmapItem() {
        SlideItem item = SlideItemFactory.createSlideItem("image", 3, "test.jpg");
        assertNotNull(item);
        assertTrue(item instanceof BitmapItem);
        assertEquals(3, item.getLevel());
        assertEquals("test.jpg", ((BitmapItem) item).getImageName());
    }

    @Test
    @DisplayName("Test case insensitivity for TextItem")
    public void testTextItemCaseInsensitivity() {
        SlideItem item = SlideItemFactory.createSlideItem("TEXT", 1, "Upper Case");
        assertTrue(item instanceof TextItem);
    }

    @Test
    @DisplayName("Test case insensitivity for BitmapItem")
    public void testBitmapItemCaseInsensitivity() {
        SlideItem item = SlideItemFactory.createSlideItem("IMAGE", 1, "test.png");
        assertTrue(item instanceof BitmapItem);
    }

    @Test
    @DisplayName("Test unknown item type throws exception")
    public void testUnknownItemType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            SlideItemFactory.createSlideItem("unknown", 1, "content");
        });

        String expectedMessage = "Unknown SlideItem type";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @ValueSource(strings = {"text", "TEXT", "Text", "tExT"})
    @DisplayName("Test different case variations for text item")
    public void testTextItemVariations(String type) {
        SlideItem item = SlideItemFactory.createSlideItem(type, 1, "Test");
        assertTrue(item instanceof TextItem);
    }

    @ParameterizedTest
    @ValueSource(strings = {"image", "IMAGE", "Image", "iMaGe"})
    @DisplayName("Test different case variations for bitmap item")
    public void testBitmapItemVariations(String type) {
        SlideItem item = SlideItemFactory.createSlideItem(type, 1, "test.jpg");
        assertTrue(item instanceof BitmapItem);
    }
}
