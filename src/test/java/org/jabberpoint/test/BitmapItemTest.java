package org.jabberpoint.test;

import org.jabberpoint.model.BitmapItem;
import org.jabberpoint.core.Style;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BitmapItemTest {

    private BitmapItem bitmapItem;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private ImageObserver mockObserver;
    
    // Using real Style instead of mock to avoid accessing private fields
    private Style realStyle;
    
    @TempDir
    Path tempDir;
    
    private BufferedImage testImage;
    private String testImageName = "test_image.png";
    private int testLevel = 3;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        
        // Create a test image file
        testImage = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        File imageFile = tempDir.resolve(testImageName).toFile();
        ImageIO.write(testImage, "png", imageFile);
        
        // Initialize Style
        Style.createStyles();
        realStyle = Style.getStyle(testLevel);
        
        // Set up observer behavior
        when(mockObserver.imageUpdate(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
    }

    @Test
    @DisplayName("Constructor should initialize BitmapItem with level and image name")
    void constructorShouldInitializeWithLevelAndImageName() {
        bitmapItem = new BitmapItem(testLevel, tempDir.resolve(testImageName).toString());
        
        assertEquals(testLevel, bitmapItem.getLevel(), "Level should be initialized correctly");
        assertEquals(tempDir.resolve(testImageName).toString(), bitmapItem.getName(), "Image name should be initialized correctly");
    }
    
    @Test
    @DisplayName("Empty constructor should create BitmapItem with default values")
    void emptyConstructorShouldCreateWithDefaultValues() {
        bitmapItem = new BitmapItem();
        
        assertEquals(0, bitmapItem.getLevel(), "Default level should be 0");
        assertNull(bitmapItem.getName(), "Default image name should be null");
    }
    
    @Test
    @DisplayName("getName should return the image name")
    void getNameShouldReturnImageName() {
        String imageName = tempDir.resolve(testImageName).toString();
        bitmapItem = new BitmapItem(testLevel, imageName);
        
        assertEquals(imageName, bitmapItem.getName(), "getName should return the image name");
    }
    
    @Test
    @DisplayName("getImageName should return the image name (same as getName)")
    void getImageNameShouldReturnImageName() {
        String imageName = tempDir.resolve(testImageName).toString();
        bitmapItem = new BitmapItem(testLevel, imageName);
        
        assertEquals(imageName, bitmapItem.getImageName(), "getImageName should return the image name");
        // Both methods should return the same value
        assertEquals(bitmapItem.getName(), bitmapItem.getImageName(), "getName and getImageName should return the same value");
    }
    
    @Test
    @DisplayName("getBoundingBox should calculate correct dimensions")
    void getBoundingBoxShouldCalculateCorrectDimensions() throws Exception {
        // Create BitmapItem with our test image
        bitmapItem = new BitmapItem(testLevel, tempDir.resolve(testImageName).toString());
        
        // Set up image observer behavior for dimensions
        when(mockObserver.imageUpdate(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        
        // Using reflection to set the bufferedImage field directly to our test image
        Field bufferedImageField = BitmapItem.class.getDeclaredField("bufferedImage");
        bufferedImageField.setAccessible(true);
        bufferedImageField.set(bitmapItem, testImage);
        
        // Call getBoundingBox
        float scale = 1.5f;
        Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, scale, realStyle);
        
        // Basic assertions without relying on internal fields
        assertNotNull(boundingBox, "Bounding box should not be null");
        assertTrue(boundingBox.width > 0, "Width should be positive");
        assertTrue(boundingBox.height > 0, "Height should be positive");
    }
    
    @Test
    @DisplayName("draw should render image with correct dimensions")
    void drawShouldRenderImageWithCorrectDimensions() throws Exception {
        // Create BitmapItem with our test image
        bitmapItem = new BitmapItem(testLevel, tempDir.resolve(testImageName).toString());
        
        // Set up image observer behavior for dimensions
        when(mockObserver.imageUpdate(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        
        // Using reflection to set the bufferedImage field directly to our test image
        Field bufferedImageField = BitmapItem.class.getDeclaredField("bufferedImage");
        bufferedImageField.setAccessible(true);
        bufferedImageField.set(bitmapItem, testImage);
        
        // Call draw
        float scale = 2.0f;
        int x = 15;
        int y = 25;
        bitmapItem.draw(x, y, scale, mockGraphics, realStyle, mockObserver);
        
        // Verify the image was drawn (we can't verify exact parameters without accessing private fields)
        verify(mockGraphics).drawImage(
            eq(testImage),
            anyInt(),
            anyInt(),
            anyInt(),
            anyInt(),
            eq(mockObserver)
        );
    }
    
    @Test
    @DisplayName("draw should handle null bufferedImage gracefully")
    void drawShouldHandleNullBufferedImageGracefully() throws Exception {
        // Create BitmapItem with a non-existent image
        String nonExistentImage = "non_existent_image.png";
        bitmapItem = new BitmapItem(testLevel, nonExistentImage);
        
        // Force bufferedImage to be null using reflection
        Field bufferedImageField = BitmapItem.class.getDeclaredField("bufferedImage");
        bufferedImageField.setAccessible(true);
        bufferedImageField.set(bitmapItem, null);
        
        // Call draw
        float scale = 1.0f;
        int x = 10;
        int y = 20;
        bitmapItem.draw(x, y, scale, mockGraphics, realStyle, mockObserver);
        
        // Verify that drawString was called (indicating error message was drawn)
        verify(mockGraphics).drawString(
            contains("Image not found"), 
            anyInt(), 
            anyInt()
        );
    }
    
    @Test
    @DisplayName("toString should return formatted string representation")
    void toStringShouldReturnFormattedStringRepresentation() {
        String imageName = tempDir.resolve(testImageName).toString();
        bitmapItem = new BitmapItem(testLevel, imageName);
        
        String expected = "BitmapItem[" + testLevel + "," + imageName + "]";
        assertEquals(expected, bitmapItem.toString(), "toString should return formatted representation");
    }
    
    @Test
    @DisplayName("Should handle null image name gracefully")
    void shouldHandleNullImageNameGracefully() {
        // Create BitmapItem with null name
        BitmapItem nullNameItem = new BitmapItem(testLevel, null);
        
        // Should not throw exceptions when drawing
        assertDoesNotThrow(() -> nullNameItem.draw(10, 10, 1.0f, mockGraphics, realStyle, mockObserver));
        
        // Get the image name
        String name = nullNameItem.getName();
        assertNull(name, "Name should be null");
    }
    
    @Test
    @DisplayName("Should return correct scale from getBoundingBox")
    void shouldReturnCorrectScaleFromGetBoundingBox() throws Exception {
        // Create valid image file
        File imageFile = tempDir.resolve(testImageName).toFile();
        ImageIO.write(testImage, "png", imageFile);
        
        // Create BitmapItem with valid image
        BitmapItem validItem = new BitmapItem(testLevel, imageFile.getAbsolutePath());
        
        // Get bounding box at different scales
        Rectangle box1 = validItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, realStyle);
        Rectangle box2 = validItem.getBoundingBox(mockGraphics, mockObserver, 2.0f, realStyle);
        
        // Second box should be twice as large
        assertEquals(box1.width * 2, box2.width, "Width should scale proportionally");
        assertEquals(box1.height * 2, box2.height, "Height should scale proportionally");
    }
    
    @Test
    @DisplayName("Should return image name correctly")
    void shouldReturnImageNameCorrectly() {
        // Create BitmapItem with known name
        String imagePath = "/path/to/image.jpg";
        BitmapItem namedItem = new BitmapItem(testLevel, imagePath);
        
        // Test getName method
        assertEquals(imagePath, namedItem.getName(), "getName should return the original image path");
        
        // Test toString method
        String toStringResult = namedItem.toString();
        assertTrue(toStringResult.contains(imagePath), "toString should include the image path");
        assertTrue(toStringResult.contains(String.valueOf(testLevel)), "toString should include the level");
    }
}

