package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BitmapItem class
 */
public class BitmapItemTest {
    private Graphics mockGraphics;
    private ImageObserver mockObserver;
    private Style testStyle;
    private String testImagePath;
    private File tempImageFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create mocks
        mockGraphics = mock(Graphics.class);
        mockObserver = mock(ImageObserver.class);
        
        // Get the Style singleton
        testStyle = Style.getInstance();
        
        // Create a temporary test image
        tempImageFile = File.createTempFile("test_image", ".jpg");
        tempImageFile.deleteOnExit();
        testImagePath = tempImageFile.getAbsolutePath();
        
        // Copy a test image to the temp file if needed
        // For this test we'll just use an empty file
        Files.write(Paths.get(testImagePath), new byte[100]);
    }

    @Test
    @DisplayName("Should create BitmapItem with proper level and name")
    void constructorShouldSetLevelAndName() {
        // Arrange & Act
        BitmapItem bitmapItem = new BitmapItem(2, testImagePath);
        
        // Assert
        assertEquals(2, bitmapItem.getLevel());
        assertEquals(testImagePath, bitmapItem.getName());
    }

    @Test
    @DisplayName("Should create empty BitmapItem with default values")
    void defaultConstructorShouldCreateEmptyItem() {
        // Arrange & Act
        BitmapItem bitmapItem = new BitmapItem();
        
        // Assert
        assertEquals(0, bitmapItem.getLevel());
        assertNull(bitmapItem.getName());
    }
    
    @Test
    @DisplayName("Should handle non-existent image file")
    void shouldHandleNonExistentImageFile() {
        // Arrange & Act
        BitmapItem bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Assert - should not throw exception
        assertNotNull(bitmapItem);
        assertEquals(1, bitmapItem.getLevel());
        assertEquals("non_existent_image.jpg", bitmapItem.getName());
    }
    
    @Test
    @DisplayName("getBoundingBox should return correct rectangle")
    void getBoundingBoxShouldReturnCorrectRectangle() {
        // Skip if image can't be loaded in test environment
        BitmapItem bitmapItem = new BitmapItem(1, testImagePath);
        
        // Mock for ImageObserver
        when(mockObserver.imageUpdate(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        
        // Act
        Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, testStyle);
        
        // Assert
        assertNotNull(boundingBox);
        assertTrue(boundingBox.width >= 0);
        assertTrue(boundingBox.height >= 0);
    }
    
    @Test
    @DisplayName("Should return proper string representation")
    void toStringShouldReturnProperRepresentation() {
        // Arrange
        BitmapItem bitmapItem = new BitmapItem(3, "test.jpg");
        
        // Act
        String result = bitmapItem.toString();
        
        // Assert
        assertEquals("BitmapItem[3,test.jpg]", result);
    }
    
    @Test
    @DisplayName("draw should handle null image gracefully")
    void drawShouldHandleNullImage() {
        // Arrange
        BitmapItem bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> 
            bitmapItem.draw(0, 0, 1.0f, mockGraphics, testStyle, mockObserver)
        );
        
        // Verify that text was drawn for missing image
        verify(mockGraphics).drawString(contains("Image not found"), anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("getImageName should return same as getName")
    void getImageNameShouldReturnSameAsGetName() {
        // Arrange
        BitmapItem bitmapItem = new BitmapItem(1, "test.jpg");
        
        // Act & Assert
        assertEquals(bitmapItem.getName(), bitmapItem.getImageName());
    }
}
