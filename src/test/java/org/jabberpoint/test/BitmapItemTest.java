package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.lang.reflect.Field;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit tests for BitmapItem class
 */
class BitmapItemTest {

    private BitmapItem bitmapItem;
    private Graphics graphicsMock;
    private ImageObserver observerMock;
    private Style styleMock;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        graphicsMock = Mockito.mock(Graphics.class);
        observerMock = Mockito.mock(ImageObserver.class);
        styleMock = Mockito.mock(Style.class);
        
        // Set up style properties
        styleMock.indent = 10;
        styleMock.leading = 20;
    }

    @Test
    @DisplayName("Should create BitmapItem with level and name")
    void shouldCreateBitmapItemWithLevelAndName() {
        // Act
        bitmapItem = new BitmapItem(2, "JabberPoint.jpg");
        
        // Assert
        assertEquals(2, bitmapItem.getLevel());
        assertEquals("JabberPoint.jpg", bitmapItem.getName());
        assertEquals("JabberPoint.jpg", bitmapItem.getImageName());
    }

    @Test
    @DisplayName("Should create empty BitmapItem with default values")
    void shouldCreateEmptyBitmapItem() {
        // Act
        bitmapItem = new BitmapItem();
        
        // Assert
        assertEquals(0, bitmapItem.getLevel());
        assertNull(bitmapItem.getName());
    }

    @Test
    @DisplayName("Should handle null image name")
    void shouldHandleNullImageName() {
        // Act
        bitmapItem = new BitmapItem(1, null);
        
        // Assert
        assertNull(bitmapItem.getName());
        assertEquals(1, bitmapItem.getLevel());
    }

    @Test
    @DisplayName("Should handle non-existent image file")
    void shouldHandleNonExistentImageFile() {
        // Act
        bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Assert - should not throw exception
        assertDoesNotThrow(() -> bitmapItem.draw(0, 0, 1.0f, graphicsMock, styleMock, observerMock));
    }

    @Test
    @DisplayName("Should return correct toString representation")
    void shouldReturnCorrectToStringRepresentation() {
        // Arrange
        bitmapItem = new BitmapItem(3, "test.jpg");
        
        // Act
        String result = bitmapItem.toString();
        
        // Assert
        assertEquals("BitmapItem[3,test.jpg]", result);
    }

    @Test
    @DisplayName("Should create a bounding box with correct dimensions")
    void shouldCreateBoundingBoxWithCorrectDimensions() {
        // Arrange
        bitmapItem = new BitmapItem(1, "JabberPoint.jpg");
        float scale = 1.5f;
        
        // This test may be skipped if the image can't be loaded in the test environment
        if (hasLoadedImage(bitmapItem)) {
            // Act
            Rectangle boundingBox = bitmapItem.getBoundingBox(graphicsMock, observerMock, scale, styleMock);
            
            // Assert
            assertNotNull(boundingBox);
            assertEquals((int)(styleMock.indent * scale), boundingBox.x);
            assertEquals(0, boundingBox.y);
        }
    }

    @Test
    @DisplayName("Should draw text message when image not found")
    void shouldDrawTextMessageWhenImageNotFound() {
        // Arrange
        bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Act
        bitmapItem.draw(10, 10, 1.0f, graphicsMock, styleMock, observerMock);
        
        // Assert - verify drawString was called with error message
        Mockito.verify(graphicsMock).drawString(
            Mockito.contains("Image not found: non_existent_image.jpg"), 
            Mockito.anyInt(), 
            Mockito.anyInt()
        );
    }

    /**
     * Helper method to check if the bitmap item has successfully loaded an image
     */
    private boolean hasLoadedImage(BitmapItem item) {
        try {
            Field field = BitmapItem.class.getDeclaredField("bufferedImage");
            field.setAccessible(true);
            return field.get(item) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
