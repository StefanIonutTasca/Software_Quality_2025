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
        presentation.setTitle("Test Presentation");
        
        // Create slides with items
        Slide slide1 = new Slide();
        slide1.setTitle("First Slide");
        slide1.append(new TextItem(1, "Text Item 1"));
        slide1.append(new TextItem(2, "Text Item 2"));
        slide1.append(new BitmapItem(3, "test.jpg"));
        presentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Second Slide");
        slide2.append(new TextItem(2, "Another Text Item"));
        presentation.append(slide2);
        
        // Act - Save the presentation
        xmlAccessor.saveFile(presentation, testXmlFile.toString());
        
        // Assert - File should exist
        assertTrue(Files.exists(testXmlFile), "XML file should be created");
        
        // Act - Load the presentation into a new object
        Presentation loadedPresentation = new Presentation();
        xmlAccessor.loadFile(loadedPresentation, testXmlFile.toString());
        
        // Assert - Check if the loaded presentation matches the original
        assertEquals("Test Presentation", loadedPresentation.getTitle(), "Presentation title should match");
        assertEquals(2, loadedPresentation.getSize(), "Should have 2 slides");
        
        // Check slide 1
        Slide loadedSlide1 = loadedPresentation.getSlide(0);
        assertEquals("First Slide", loadedSlide1.getTitle(), "Slide 1 title should match");
        assertEquals(3, loadedSlide1.getSize(), "Slide 1 should have 3 items");
        
        // Check slide 2
        Slide loadedSlide2 = loadedPresentation.getSlide(1);
        assertEquals("Second Slide", loadedSlide2.getTitle(), "Slide 2 title should match");
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
    @DisplayName("Should handle malformed level attribute")
    void shouldHandleMalformedLevelAttribute() throws IOException {
        // Arrange - Create XML with malformed level attribute
        String xml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"not-a-number\">Text with invalid level</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Files.writeString(testXmlFile, xml);
        
        // Act
        Presentation p = new Presentation();
        xmlAccessor.loadFile(p, testXmlFile.toString());
        
        // Assert - Should still load with default level
        assertEquals(1, p.getSize(), "Should have 1 slide");
        Slide slide = p.getSlide(0);
        assertEquals(1, slide.getSize(), "Slide should have 1 item");
        // Default level should be used (1)
        assertEquals(1, slide.getSlideItems().get(0).getLevel(), "Default level should be used");
    }
    
    @Test
    @DisplayName("Should handle unknown item type")
    void shouldHandleUnknownItemType() throws IOException {
        // Arrange - Create XML with unknown item type
        String xml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"unknown\" level=\"1\">Unknown item type</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Files.writeString(testXmlFile, xml);
        
        // Act
        Presentation p = new Presentation();
        xmlAccessor.loadFile(p, testXmlFile.toString());
        
        // Assert - Should still load other items
        assertEquals(1, p.getSize(), "Should have 1 slide");
        Slide slide = p.getSlide(0);
        assertEquals(0, slide.getSize(), "Slide should have 0 items as unknown type is ignored");
    }
}
