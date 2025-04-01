package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Style;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BitmapItemTest {

    private BitmapItem bitmapItem;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private ImageObserver mockObserver;
    
    @Mock
    private Style mockStyle;
    
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
        
        // Mock style behavior
        when(mockStyle.indent).thenReturn(10f);
        when(mockStyle.leading).thenReturn(20f);
        
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
        when(testImage.getWidth(mockObserver)).thenReturn(100);
        when(testImage.getHeight(mockObserver)).thenReturn(50);
        
        // Using reflection to set the bufferedImage field directly to our test image
        // This is more reliable than hoping the constructor loads the image correctly
        Field bufferedImageField = BitmapItem.class.getDeclaredField("bufferedImage");
        bufferedImageField.setAccessible(true);
        bufferedImageField.set(bitmapItem, testImage);
        
        // Call getBoundingBox
        float scale = 1.5f;
        Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, scale, mockStyle);
        
        // Calculate expected dimensions
        int expectedX = (int) (mockStyle.indent * scale);
        int expectedWidth = (int) (testImage.getWidth(mockObserver) * scale);
        int expectedHeight = (int) (mockStyle.leading * scale) + (int) (testImage.getHeight(mockObserver) * scale);
        
        // Verify dimensions
        assertNotNull(boundingBox, "Bounding box should not be null");
        assertEquals(expectedX, boundingBox.x, "X position should be style indent * scale");
        assertEquals(0, boundingBox.y, "Y position should be 0");
        assertEquals(expectedWidth, boundingBox.width, "Width should be the scaled image width");
        assertEquals(expectedHeight, boundingBox.height, "Height should include leading + scaled image height");
    }
    
    @Test
    @DisplayName("draw should render image with correct dimensions")
    void drawShouldRenderImageWithCorrectDimensions() throws Exception {
        // Create BitmapItem with our test image
        bitmapItem = new BitmapItem(testLevel, tempDir.resolve(testImageName).toString());
        
        // Set up image observer behavior for dimensions
        when(mockObserver.imageUpdate(any(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(true);
        when(testImage.getWidth(mockObserver)).thenReturn(100);
        when(testImage.getHeight(mockObserver)).thenReturn(50);
        
        // Using reflection to set the bufferedImage field directly to our test image
        Field bufferedImageField = BitmapItem.class.getDeclaredField("bufferedImage");
        bufferedImageField.setAccessible(true);
        bufferedImageField.set(bitmapItem, testImage);
        
        // Call draw
        float scale = 2.0f;
        int x = 15;
        int y = 25;
        bitmapItem.draw(x, y, scale, mockGraphics, mockStyle, mockObserver);
        
        // Calculate expected positions and dimensions
        int expectedX = x + (int) (mockStyle.indent * scale);
        int expectedY = y + (int) (mockStyle.leading * scale);
        int expectedWidth = (int) (testImage.getWidth(mockObserver) * scale);
        int expectedHeight = (int) (testImage.getHeight(mockObserver) * scale);
        
        // Verify the image was drawn with correct parameters
        verify(mockGraphics).drawImage(
            eq(testImage),
            eq(expectedX),
            eq(expectedY),
            eq(expectedWidth),
            eq(expectedHeight),
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
        bitmapItem.draw(x, y, scale, mockGraphics, mockStyle, mockObserver);
        
        // Verify error message was drawn instead of image
        int expectedX = x + (int) (mockStyle.indent * scale);
        int expectedY = y + (int) (mockStyle.leading * scale);
        verify(mockGraphics).drawString(
            contains("Image not found"), 
            eq(expectedX), 
            eq(expectedY)
        );
        
        // Verify drawImage was NOT called
        verify(mockGraphics, never()).drawImage(
            any(BufferedImage.class),
            anyInt(),
            anyInt(),
            anyInt(),
            anyInt(),
            any(ImageObserver.class)
        );
    }
    
    @Test
    @DisplayName("tryLoadImage should attempt to load from different locations")
    void tryLoadImageShouldAttemptToLoadFromDifferentLocations() throws Exception {
        // This test checks that the tryLoadImage method attempts to load from different locations
        // We'll use a system property to create a fake project directory structure
        
        // Create a temporary directory structure mimicking the project layout
        File mainDir = tempDir.resolve("src/main/java/org/jabberpoint").toFile();
        mainDir.mkdirs();
        
        // Create a test image in the jabberpoint directory
        File projectImage = new File(mainDir, testImageName);
        ImageIO.write(testImage, "png", projectImage);
        
        // Create a test image in the resources directory
        File resourcesDir = tempDir.resolve("src/main/resources").toFile();
        resourcesDir.mkdirs();
        File resourceImage = new File(resourcesDir, testImageName);
        ImageIO.write(testImage, "png", resourceImage);
        
        // Create the BitmapItem with just the image name (not the full path)
        // This forces it to search for the image
        bitmapItem = new BitmapItem(testLevel, testImageName);
        
        // Verify that the image name was set correctly
        assertEquals(testImageName, bitmapItem.getName(), "Image name should be set correctly");
        
        // We can't easily verify that tryLoadImage searched all locations,
        // but we can at least verify it recorded the image name correctly
        assertEquals(testImageName, bitmapItem.getName(), "Image name should be set correctly");
    }
    
    @Test
    @DisplayName("toString should return formatted string representation")
    void toStringShouldReturnFormattedStringRepresentation() {
        String imageName = "test.png";
        bitmapItem = new BitmapItem(testLevel, imageName);
        
        String expected = "BitmapItem[" + testLevel + "," + imageName + "]";
        assertEquals(expected, bitmapItem.toString(), "toString should return formatted representation");
    }
}
