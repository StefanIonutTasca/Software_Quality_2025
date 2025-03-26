package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.XMLAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for XMLAccessor class
 */
class XMLAccessorTest {

    private XMLAccessor accessor;
    private Presentation presentation;
    
    @TempDir
    Path tempDir;
    
    private Path testFile;

    @BeforeEach
    void setUp() {
        accessor = new XMLAccessor();
        presentation = new Presentation();
        
        // Create a test file path in temporary directory
        testFile = tempDir.resolve("test_presentation.xml");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test files
        try {
            Files.deleteIfExists(testFile);
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @DisplayName("Should save presentation to XML file")
    void shouldSavePresentationToXmlFile() throws IOException {
        // Arrange
        presentation.setTitle("Test Presentation");
        
        Slide slide1 = new Slide();
        slide1.setTitle("Slide 1 Title");
        slide1.append(new TextItem(1, "Text Item 1"));
        slide1.append(new TextItem(2, "Text Item 2"));
        presentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Slide 2 Title");
        slide2.append(new BitmapItem(1, "test.jpg"));
        presentation.append(slide2);
        
        // Act
        accessor.saveFile(presentation, testFile.toString());
        
        // Assert
        assertTrue(Files.exists(testFile));
        String content = Files.readString(testFile);
        assertTrue(content.contains("<showtitle>Test Presentation</showtitle>"));
        assertTrue(content.contains("<title>Slide 1 Title</title>"));
        assertTrue(content.contains("<item kind=\"text\" level=\"1\">Text Item 1</item>"));
        assertTrue(content.contains("<item kind=\"text\" level=\"2\">Text Item 2</item>"));
        assertTrue(content.contains("<title>Slide 2 Title</title>"));
        assertTrue(content.contains("<item kind=\"image\" level=\"1\">test.jpg</item>"));
    }

    @Test
    @DisplayName("Should load presentation from XML file")
    void shouldLoadPresentationFromXmlFile() throws IOException {
        // Arrange - create a sample XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1 Title</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "<item kind=\"text\" level=\"2\">Text Item 2</item>\n" +
                "</slide>\n" +
                "<slide>\n" +
                "<title>Slide 2 Title</title>\n" +
                "<item kind=\"image\" level=\"1\">test.jpg</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Files.writeString(testFile, xmlContent);
        
        // Act
        accessor.loadFile(presentation, testFile.toString());
        
        // Assert
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals(2, presentation.getSize());
        
        Slide slide1 = presentation.getSlide(0);
        assertEquals("Slide 1 Title", slide1.getTitle());
        assertEquals(2, slide1.getSize());
        assertTrue(slide1.getSlideItems().get(0) instanceof TextItem);
        assertEquals(1, slide1.getSlideItems().get(0).getLevel());
        assertEquals("Text Item 1", ((TextItem)slide1.getSlideItems().get(0)).getText());
        
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Slide 2 Title", slide2.getTitle());
        assertEquals(1, slide2.getSize());
        assertTrue(slide2.getSlideItems().get(0) instanceof BitmapItem);
        assertEquals(1, slide2.getSlideItems().get(0).getLevel());
        assertEquals("test.jpg", ((BitmapItem)slide2.getSlideItems().get(0)).getName());
    }
    
    @Test
    @DisplayName("Should handle malformed XML file gracefully")
    void shouldHandleMalformedXmlFileGracefully() throws IOException {
        // Arrange - create a malformed XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"text\" level=\"invalid\">Text Item</item>\n" + // Invalid level
                "</slide>\n" +
                "</presentation>";
        
        Files.writeString(testFile, xmlContent);
        
        // Act
        accessor.loadFile(presentation, testFile.toString());
        
        // Assert - should not throw exception and should load what it can
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals(1, presentation.getSize());
    }
    
    @Test
    @DisplayName("Should handle unknown item kind gracefully")
    void shouldHandleUnknownItemKindGracefully() throws IOException {
        // Arrange - create XML with unknown item kind
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"unknown\" level=\"1\">Unknown Item</item>\n" + // Unknown kind
                "</slide>\n" +
                "</presentation>";
        
        Files.writeString(testFile, xmlContent);
        
        // Act
        accessor.loadFile(presentation, testFile.toString());
        
        // Assert - should not throw exception and should load what it can
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals(1, presentation.getSize());
        assertEquals(0, presentation.getSlide(0).getSize()); // Item with unknown kind should be ignored
    }
    
    @Test
    @DisplayName("Should handle non-existent file gracefully")
    void shouldHandleNonExistentFileGracefully() throws IOException {
        // Act & Assert - should not throw exception
        accessor.loadFile(presentation, "non_existent_file.xml");
        
        // Nothing should be loaded
        assertNull(presentation.getTitle());
        assertEquals(0, presentation.getSize());
    }
}
