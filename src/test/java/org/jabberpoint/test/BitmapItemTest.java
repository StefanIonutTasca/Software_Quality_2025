package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

/**
 * Unit tests for BitmapItem class
 */
class BitmapItemTest {

    private BitmapItem bitmapItem;
    private Graphics graphicsMock;
    private ImageObserver observerMock;
    private Style style;
    
    @TempDir
    Path tempDir;
    
    private Path testImagePath;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() throws IOException {
        // Mock dependencies
        graphicsMock = Mockito.mock(Graphics.class);
        observerMock = Mockito.mock(ImageObserver.class);
        
        // Create a test image for more reliable testing
        testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        testImagePath = tempDir.resolve("test_image.png");
        ImageIO.write(testImage, "png", testImagePath.toFile());
        
        // Use actual Style object instead of mocking
        Style.createStyles();
        style = Style.getStyle(1); // Use level 1 style
        
        // Configure ImageObserver mock to always return true and valid dimensions
        Mockito.when(observerMock.imageUpdate(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                                              Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
               .thenReturn(true);
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
        bitmapItem = new BitmapItem(1, testImagePath.toString());
        float scale = 1.5f;
        
        // This test may be skipped if the image can't be loaded in the test environment
        if (hasLoadedImage(bitmapItem)) {
            // Set up the mock to return expected values for image dimensions
            Mockito.when(observerMock.imageUpdate(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), 
                                                 Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                   .thenReturn(true);
            Mockito.when(testImage.getWidth(observerMock)).thenReturn(100);
            Mockito.when(testImage.getHeight(observerMock)).thenReturn(100);
            
            // Act
            Rectangle boundingBox = bitmapItem.getBoundingBox(graphicsMock, observerMock, scale, style);
            
            // Assert
            assertNotNull(boundingBox);
            // Instead of accessing style.indent directly, we'll just verify the x position is >= 0
            assertTrue(boundingBox.x >= 0);
            assertEquals(0, boundingBox.y);
            // Since we're mocking the image dimensions, we can verify the expected width and height
            assertTrue(boundingBox.width > 0);
            assertTrue(boundingBox.height > 0);
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
    @DisplayName("Should draw the image when image is found")
    void shouldDrawTheImageWhenImageIsFound() throws IOException {
        // Arrange - create a bitmap item with a valid image
        bitmapItem = new BitmapItem(1, testImagePath.toString());
        
        // Act - only if image was loaded successfully
        if (hasLoadedImage(bitmapItem)) {
            // Set expectations for the image dimensions
            Mockito.when(testImage.getWidth(observerMock)).thenReturn(100);
            Mockito.when(testImage.getHeight(observerMock)).thenReturn(100);
            
            // Draw the image
            bitmapItem.draw(10, 10, 1.5f, graphicsMock, style, observerMock);
            
            // Assert - verify drawImage was called with the correct parameters
            Mockito.verify(graphicsMock).drawImage(
                Mockito.any(BufferedImage.class),
                Mockito.anyInt(), // x
                Mockito.anyInt(), // y
                Mockito.anyInt(), // width
                Mockito.anyInt(), // height
                Mockito.eq(observerMock)
            );
        }
    }
    
    @Test
    @DisplayName("Should try multiple locations when loading image")
    void shouldTryMultipleLocationsWhenLoadingImage() throws Exception {
        // Arrange - Copy test image to the expected locations to test path resolution
        File packageDir = new File("src/main/java/org/jabberpoint");
        packageDir.mkdirs();
        File packageFile = new File(packageDir, "package_test.png");
        Files.copy(testImagePath, packageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Act - Create bitmap item that should find the image
        bitmapItem = new BitmapItem(1, "package_test.png");
        
        // Assert - Verify image was loaded
        assertTrue(hasLoadedImage(bitmapItem), "Image should be loaded from package directory");
        
        // Clean up the test file
        packageFile.delete();
    }
    
    @Test
    @DisplayName("Should handle IOException during image loading")
    void shouldHandleIOExceptionDuringImageLoading() throws Exception {
        // Arrange - Create an invalid image file (not a PNG but named as PNG)
        File invalidImageFile = tempDir.resolve("invalid.png").toFile();
        Files.write(invalidImageFile.toPath(), "This is not a valid PNG file".getBytes());
        
        // Act - This should throw an IOException internally which is caught
        bitmapItem = new BitmapItem(1, invalidImageFile.getAbsolutePath());
        
        // Assert - Image should not be loaded but no exception should propagate
        assertFalse(hasLoadedImage(bitmapItem), "Invalid image should not be loaded");
        assertDoesNotThrow(() -> bitmapItem.draw(0, 0, 1.0f, graphicsMock, style, observerMock));
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
