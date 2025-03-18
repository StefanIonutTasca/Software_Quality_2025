package org.jabberpoint.src;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class XMLPresentationLoaderTest {

    private XMLPresentationLoader loader;
    private Presentation presentation;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        loader = new XMLPresentationLoader();
        presentation = new Presentation();
    }
    
    @AfterEach
    public void tearDown() {
        loader = null;
        presentation = null;
    }

    @Test
    @DisplayName("Test saving and loading a presentation")
    public void testSaveAndLoadPresentation() throws IOException {
        // Create a test presentation
        presentation.setTitle("Test Presentation");
        
        // Add a slide with text items
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(1, "Level 1 text");
        slide.append(2, "Level 2 text");
        presentation.append(slide);
        
        // Save the presentation
        String filePath = tempDir.resolve("test.xml").toString();
        loader.savePresentation(presentation, filePath);
        
        // Load the saved presentation into a new presentation object
        Presentation loadedPresentation = new Presentation();
        loader.loadPresentation(loadedPresentation, filePath);
        
        // Verify the loaded presentation matches the original
        assertEquals("Test Presentation", loadedPresentation.getTitle());
        assertEquals(1, loadedPresentation.getSize());
        
        Slide loadedSlide = loadedPresentation.getSlide(0);
        assertEquals("Test Slide", loadedSlide.getTitle());
        assertEquals(2, loadedSlide.getSize());
    }
    
    @Test
    @DisplayName("Test handling a non-existent file")
    public void testNonExistentFile() {
        String nonExistentFile = tempDir.resolve("doesnotexist.xml").toString();
        
        Exception exception = assertThrows(IOException.class, () -> {
            loader.loadPresentation(presentation, nonExistentFile);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    @DisplayName("Test handling malformed XML")
    public void testMalformedXML() throws IOException {
        // Create a malformed XML file
        String filePath = tempDir.resolve("malformed.xml").toString();
        Files.write(Paths.get(filePath), "<this is not valid XML>".getBytes());
        
        Exception exception = assertThrows(IOException.class, () -> {
            loader.loadPresentation(presentation, filePath);
        });
        
        assertNotNull(exception.getMessage());
    }
    
    @Test
    @DisplayName("Test loading XML with invalid level attribute")
    public void testInvalidLevelAttribute() throws IOException {
        // Create XML with invalid level attribute
        String filePath = tempDir.resolve("invalid_level.xml").toString();
        String xmlContent = 
            "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "  <showtitle>Test Presentation</showtitle>\n" +
            "  <slide>\n" +
            "    <title>Test Slide</title>\n" +
            "    <item kind=\"text\" level=\"invalid\">Invalid level text</item>\n" +
            "  </slide>\n" +
            "</presentation>\n";
        Files.write(Paths.get(filePath), xmlContent.getBytes());
        
        // This should not throw an exception but handle the invalid level gracefully
        // by using the default level value
        loader.loadPresentation(presentation, filePath);
        
        // Verify the presentation was loaded despite the invalid level
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals(1, presentation.getSize());
    }
    
    @Test
    @DisplayName("Test saving and loading presentation with both text and image items")
    public void testMixedItemTypes() throws IOException {
        // Create a test presentation with text and image items
        presentation.setTitle("Mixed Items Test");
        
        Slide slide = new Slide();
        slide.setTitle("Mixed Items Slide");
        slide.append(1, "Text item");
        slide.append(new BitmapItem(2, "test-image.jpg"));
        presentation.append(slide);
        
        // Save the presentation
        String filePath = tempDir.resolve("mixed_items.xml").toString();
        loader.savePresentation(presentation, filePath);
        
        // Load the saved presentation
        Presentation loadedPresentation = new Presentation();
        loader.loadPresentation(loadedPresentation, filePath);
        
        // Verify the loaded presentation
        assertEquals("Mixed Items Test", loadedPresentation.getTitle());
        assertEquals(1, loadedPresentation.getSize());
        
        Slide loadedSlide = loadedPresentation.getSlide(0);
        assertEquals("Mixed Items Slide", loadedSlide.getTitle());
        assertEquals(2, loadedSlide.getSize());
        
        // Verify item types
        SlideItem item1 = loadedSlide.getSlideItem(0);
        assertTrue(item1 instanceof TextItem);
        
        SlideItem item2 = loadedSlide.getSlideItem(1);
        assertTrue(item2 instanceof BitmapItem);
    }
}
