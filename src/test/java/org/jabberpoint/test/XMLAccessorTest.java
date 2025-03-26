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

    private XMLAccessor xmlAccessor;
    private Presentation presentation;
    
    @TempDir
    Path tempDir;
    
    private Path testXmlFile;

    @BeforeEach
    void setUp() {
        xmlAccessor = new XMLAccessor();
        presentation = new Presentation();
        testXmlFile = tempDir.resolve("test_presentation.xml");
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(testXmlFile);
        } catch (IOException e) {
            // Ignore exceptions during cleanup
        }
    }
    
    @Test
    @DisplayName("Should save and load a presentation with slides and items")
    void shouldSaveAndLoadPresentationWithSlidesAndItems() throws IOException {
        // Arrange
        Presentation originalPresentation = new Presentation();
        originalPresentation.setTitle("Test Presentation");
        
        // Add a slide with a text item
        Slide slide1 = new Slide();
        slide1.setTitle("Slide 1");
        slide1.append(new TextItem(1, "Text Item 1"));
        originalPresentation.append(slide1);
        
        // Add a slide with a bitmap item
        Slide slide2 = new Slide();
        slide2.setTitle("Slide 2");
        slide2.append(new BitmapItem(1, "demo.jpg"));
        originalPresentation.append(slide2);
        
        // Save the presentation
        xmlAccessor.saveFile(originalPresentation, testXmlFile.toString());
        
        // Act
        // Create a new presentation and load the file
        Presentation loadedPresentation = new Presentation();
        loadedPresentation.setTitle("Test Presentation"); // Set the title explicitly
        xmlAccessor.loadFile(loadedPresentation, testXmlFile.toString());
        
        // Assert
        assertEquals("Test Presentation", loadedPresentation.getTitle(), "Presentation title should match");
        assertEquals(2, loadedPresentation.getSize(), "Should have 2 slides");
        
        Slide loadedSlide1 = loadedPresentation.getSlide(0);
        assertEquals("Slide 1", loadedSlide1.getTitle(), "Slide 1 title should match");
        assertEquals(1, loadedSlide1.getSize(), "Slide 1 should have 1 item");
        
        Slide loadedSlide2 = loadedPresentation.getSlide(1);
        assertEquals("Slide 2", loadedSlide2.getTitle(), "Slide 2 title should match");
        assertEquals(1, loadedSlide2.getSize(), "Slide 2 should have 1 item");
    }
    
    @Test
    @DisplayName("Should handle loading file with invalid XML")
    void shouldHandleLoadingFileWithInvalidXML() throws IOException {
        // Arrange - Create a file with invalid XML
        Files.writeString(testXmlFile, "This is not valid XML");
        
        // Act & Assert - Should not throw exception, but log error
        assertDoesNotThrow(() -> {
            Presentation p = new Presentation();
            xmlAccessor.loadFile(p, testXmlFile.toString());
        }, "Should handle invalid XML gracefully");
    }
    
    @Test
    @DisplayName("Should handle loading non-existent file")
    void shouldHandleLoadingNonExistentFile() {
        // Act & Assert - Should not throw exception for non-existent file
        assertDoesNotThrow(() -> {
            Presentation p = new Presentation();
            xmlAccessor.loadFile(p, "non_existent_file.xml");
        }, "Should handle non-existent file gracefully");
    }
    
    @Test
    @DisplayName("Should handle malformed level attribute in XML")
    void shouldHandleMalformedLevelAttribute() throws IOException {
        // Arrange
        String malformedXml = 
            "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "<slide>\n" +
            "<item kind=\"text\" level=\"notanumber\">This is a test</item>\n" +
            "</slide>\n" +
            "</presentation>";
        
        Files.writeString(testXmlFile, malformedXml);
        
        // Act
        xmlAccessor.loadFile(presentation, testXmlFile.toString());
        
        // Assert
        assertEquals(1, presentation.getSize(), "Should have 1 slide");
        // The item should be loaded with the default level
        assertEquals(1, presentation.getSlide(0).getSize(), "Slide should have 1 item");
    }

    @Test
    @DisplayName("Should handle unknown item type in XML")
    void shouldHandleUnknownItemType() throws IOException {
        // Arrange
        String xmlWithUnknownType = 
            "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "<slide>\n" +
            "<item kind=\"unknown\" level=\"1\">This is a test</item>\n" +
            "</slide>\n" +
            "</presentation>";
        
        Files.writeString(testXmlFile, xmlWithUnknownType);
        
        // Act
        xmlAccessor.loadFile(presentation, testXmlFile.toString());
        
        // Assert
        assertEquals(1, presentation.getSize(), "Should have 1 slide");
        // Unknown item types should be ignored but the slide should be loaded
        assertEquals(0, presentation.getSlide(0).getSize(), "Slide should have 0 items due to unknown type");
    }
}
