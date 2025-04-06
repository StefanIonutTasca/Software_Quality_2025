package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.util.Vector;

import org.jabberpoint.src.model.Slide;
import org.jabberpoint.src.model.SlideItem;
import org.jabberpoint.src.model.Style;
import org.jabberpoint.src.model.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for Slide class
 */
class SlideTest {

    private Slide slide;
    private Graphics2D graphicsMock;
    private ImageObserver observerMock;
    private Rectangle areaMock;
    private FontRenderContext frc;

    @BeforeEach
    void setUp() {
        slide = new Slide();
        graphicsMock = Mockito.mock(Graphics2D.class);
        observerMock = Mockito.mock(ImageObserver.class);
        areaMock = new Rectangle(0, 0, 800, 600);
        
        // Create a real FontRenderContext with an identity AffineTransform
        AffineTransform at = new AffineTransform();
        frc = new FontRenderContext(at, true, true);
        
        // Configure Graphics2D mock to return the real FontRenderContext
        Mockito.when(graphicsMock.getFontRenderContext()).thenReturn(frc);
    }

    @Test
    @DisplayName("Should create empty slide")
    void shouldCreateEmptySlide() {
        // Assert
        assertNotNull(slide);
        assertEquals(0, slide.getSize());
        assertNull(slide.getTitle());
    }

    @Test
    @DisplayName("Should set and get title")
    void shouldSetAndGetTitle() {
        // Act
        slide.setTitle("Test Title");
        
        // Assert
        assertEquals("Test Title", slide.getTitle());
    }

    @Test
    @DisplayName("Should append SlideItem")
    void shouldAppendSlideItem() {
        // Arrange
        SlideItem item = new TextItem(1, "Test Item");
        
        // Act
        slide.append(item);
        
        // Assert
        assertEquals(1, slide.getSize());
        assertSame(item, slide.getSlideItem(0));
    }

    @Test
    @DisplayName("Should append text with level")
    void shouldAppendTextWithLevel() {
        // Act
        slide.append(2, "Test Message");
        
        // Assert
        assertEquals(1, slide.getSize());
        SlideItem item = slide.getSlideItem(0);
        assertTrue(item instanceof TextItem);
        assertEquals(2, item.getLevel());
        assertEquals("Test Message", ((TextItem)item).getText());
    }

    @Test
    @DisplayName("Should get all slide items")
    void shouldGetAllSlideItems() {
        // Arrange
        SlideItem item1 = new TextItem(1, "Item 1");
        SlideItem item2 = new TextItem(2, "Item 2");
        slide.append(item1);
        slide.append(item2);
        
        // Act
        Vector<SlideItem> items = slide.getSlideItems();
        
        // Assert
        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    @DisplayName("Should get slide item by index")
    void shouldGetSlideItemByIndex() {
        // Arrange
        SlideItem item1 = new TextItem(1, "Item 1");
        SlideItem item2 = new TextItem(2, "Item 2");
        slide.append(item1);
        slide.append(item2);
        
        // Act
        SlideItem retrievedItem = slide.getSlideItem(1);
        
        // Assert
        assertSame(item2, retrievedItem);
    }

    @Test
    @DisplayName("Should calculate correct scale")
    void shouldCalculateCorrectScale() {
        // Arrange - Use reflection to test private method
        try {
            java.lang.reflect.Method getScaleMethod = Slide.class.getDeclaredMethod("getScale", Rectangle.class);
            getScaleMethod.setAccessible(true);
            
            // Different test cases for scale calculation
            Rectangle smallArea = new Rectangle(0, 0, 600, 400);
            Rectangle wideArea = new Rectangle(0, 0, 2400, 400);
            Rectangle tallArea = new Rectangle(0, 0, 600, 1600);
            
            // Act & Assert
            float smallScale = (float) getScaleMethod.invoke(slide, smallArea);
            float wideScale = (float) getScaleMethod.invoke(slide, wideArea);
            float tallScale = (float) getScaleMethod.invoke(slide, tallArea);
            
            // Expected scales
            float expectedSmallScale = Math.min(600f/1200f, 400f/800f); // 0.5
            float expectedWideScale = Math.min(2400f/1200f, 400f/800f); // 0.5
            float expectedTallScale = Math.min(600f/1200f, 1600f/800f); // 0.5
            
            assertEquals(expectedSmallScale, smallScale, 0.001);
            assertEquals(expectedWideScale, wideScale, 0.001);
            assertEquals(expectedTallScale, tallScale, 0.001);
            
        } catch (Exception e) {
            fail("Exception while testing getScale: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should draw slide with items")
    void shouldDrawSlideWithItems() {
        // Arrange
        slide.setTitle("Test Slide");
        
        // Use a fully mocked SlideItem to avoid null text issues in real implementations
        SlideItem mockItem = Mockito.mock(SlideItem.class);
        Rectangle boundingBox = new Rectangle(0, 0, 100, 50);
        
        // Configure the mock to return the bounding box when getBoundingBox is called
        Mockito.when(mockItem.getBoundingBox(
            Mockito.any(Graphics2D.class),
            Mockito.any(ImageObserver.class),
            Mockito.anyFloat(),
            Mockito.any(Style.class)
        )).thenReturn(boundingBox);
        
        // Add the mock item to the slide
        slide.append(mockItem);
        
        // Act - draw the slide
        slide.draw(graphicsMock, areaMock, observerMock);
        
        // Assert - verify that draw was called on the mock item with the expected parameters
        Mockito.verify(mockItem).draw(
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyFloat(),
            Mockito.eq(graphicsMock),
            Mockito.any(Style.class),
            Mockito.eq(observerMock)
        );
    }
}

