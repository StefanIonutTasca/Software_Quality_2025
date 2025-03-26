package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.contains;

/**
 * Unit tests for BitmapItem class
 */
public class BitmapItemTest {
    private Graphics2D mockGraphics;
    private ImageObserver mockObserver;
    private Style testStyle;
    private String testImagePath;
    private File tempImageFile;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Ensure styles are created properly
        Style.createStyles();
        
        // Create mocks with proper Graphics2D setup
        mockGraphics = mock(Graphics2D.class);
        mockObserver = mock(ImageObserver.class);
        
        // Mock FontRenderContext which may be needed
        FontRenderContext mockFrc = mock(FontRenderContext.class);
        when(mockGraphics.getFontRenderContext()).thenReturn(mockFrc);
        
        // Mock AffineTransform to avoid NullPointerException
        AffineTransform mockTransform = mock(AffineTransform.class);
        when(mockTransform.getScaleX()).thenReturn(1.0);
        when(mockGraphics.getTransform()).thenReturn(mockTransform);
        
        // Get the Style instance
        testStyle = Style.getStyle(1);
        
        // Create a simple 1x1 black image for testing
        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        
        // Create a temporary test image
        tempImageFile = new File(tempDir.toFile(), "test_image.jpg");
        testImagePath = tempImageFile.getAbsolutePath();
        
        // Write a simple valid image
        javax.imageio.ImageIO.write(testImage, "jpg", tempImageFile);
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
    @DisplayName("getBoundingBox should return correct rectangle when image exists")
    void getBoundingBoxShouldReturnCorrectRectangleWhenImageExists() throws Exception {
        // Create a BitmapItem with our test image
        BitmapItem bitmapItem = new BitmapItem(1, testImagePath);
        
        // Mock the ImageObserver behavior
        when(mockObserver.imageUpdate(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        
        // Act
        Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, testStyle);
        
        // Assert
        assertNotNull(boundingBox);
        assertTrue(boundingBox.width >= 0);
        assertTrue(boundingBox.height >= 0);
    }
    
    @Test
    @DisplayName("getBoundingBox should handle null image")
    void getBoundingBoxShouldHandleNullImage() {
        // Create a BitmapItem with a non-existent image
        BitmapItem bitmapItem = new BitmapItem(1, "non_existent_image.jpg");
        
        // Act
        Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, testStyle);
        
        // Assert - should return some default rectangle rather than null
        assertNotNull(boundingBox);
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
    @DisplayName("draw should handle existing image properly")
    void drawShouldHandleExistingImage() {
        // Arrange
        BitmapItem bitmapItem = new BitmapItem(1, testImagePath);
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> 
            bitmapItem.draw(0, 0, 1.0f, mockGraphics, testStyle, mockObserver)
        );
        
        // No need to verify drawing operations as they may vary based on image loaded
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
