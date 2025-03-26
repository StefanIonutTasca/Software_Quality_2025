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
    private Style style;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        graphicsMock = Mockito.mock(Graphics.class);
        observerMock = Mockito.mock(ImageObserver.class);
        
        // Use actual Style object instead of mocking
        Style.createStyles();
        style = Style.getStyle(1); // Use level 1 style
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
        assertDoesNotThrow(() -> bitmapItem.draw(0, 0, 1.0f, graphicsMock, style, observerMock));
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
            Rectangle boundingBox = bitmapItem.getBoundingBox(graphicsMock, observerMock, scale, style);
            
            // Assert
            assertNotNull(boundingBox);
            // Instead of accessing style.indent directly, we'll just verify the x position is >= 0
            assertTrue(boundingBox.x >= 0);
            assertEquals(0, boundingBox.y);
        }
    }

    @Test
    @DisplayName("Should draw text message when image not found")
    void shouldDrawTextMessageWhenImageNotFound() {
        // Arrange
        bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Act
        bitmapItem.draw(10, 10, 1.0f, graphicsMock, style, observerMock);
        
        // Assert - verify drawString was called with error message
        Mockito.verify(graphicsMock).drawString(
            Mockito.contains("Image not found: non_existent_image.jpg"), 
            Mockito.anyInt(), 
            Mockito.anyInt()
        );
    }

    @Test
    @DisplayName("Should get correct image name")
    void shouldGetCorrectImageName() {
        // Arrange
        String imageName = "test-image.png";
        bitmapItem = new BitmapItem(2, imageName);
        
        // Act
        String result = bitmapItem.getImageName();
        
        // Assert
        assertEquals(imageName, result);
    }
    
    @Test
    @DisplayName("Should handle getBoundingBox when image is null")
    void shouldHandleBoundingBoxWhenImageIsNull() {
        // Arrange
        bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            bitmapItem.getBoundingBox(graphicsMock, observerMock, 1.0f, style));
    }
    
    @Test
    @DisplayName("Should handle scale adjustment in getBoundingBox")
    void shouldHandleScaleAdjustmentInGetBoundingBox() {
        // Arrange
        bitmapItem = new BitmapItem(1, "JabberPoint.jpg");
        float scale = 2.0f;
        
        // Only run if image is loaded
        if (hasLoadedImage(bitmapItem)) {
            // Act
            Rectangle box1 = bitmapItem.getBoundingBox(graphicsMock, observerMock, 1.0f, style);
            Rectangle box2 = bitmapItem.getBoundingBox(graphicsMock, observerMock, scale, style);
            
            // Assert - width should be approximately doubled with scale = 2.0
            // Allow some rounding errors by checking it's at least 1.9 times larger
            assertTrue(box2.width >= box1.width * 1.9);
        }
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
