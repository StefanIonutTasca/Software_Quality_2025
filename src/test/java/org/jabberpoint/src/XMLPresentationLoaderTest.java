package org.jabberpoint.src;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for XMLPresentationLoader
 */
class XMLPresentationLoaderTest {

    private XMLPresentationLoader loader;
    private Presentation presentation;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new XMLPresentationLoader();
        presentation = new Presentation();
    }

    @Test
    @DisplayName("Should throw IOException when file doesn't exist")
    void loadPresentationShouldThrowIOExceptionWhenFileDoesntExist() {
        // Arrange
        String nonExistentFile = "non_existent_file.xml";
        
        // Act & Assert
        assertThrows(IOException.class, 
                () -> loader.loadPresentation(presentation, nonExistentFile),
                "Should throw IOException when file doesn't exist");
    }
    
    @Test
    @DisplayName("Should throw IOException when XML is invalid")
    void loadPresentationShouldThrowIOExceptionWhenXMLIsInvalid() throws IOException {
        // Arrange - create an invalid XML file
        File invalidXmlFile = tempDir.resolve("invalid.xml").toFile();
        Files.writeString(invalidXmlFile.toPath(), "<invalid>This is not valid XML");
        
        // Act & Assert
        assertThrows(IOException.class, 
                () -> loader.loadPresentation(presentation, invalidXmlFile.getAbsolutePath()),
                "Should throw IOException when XML is invalid");
    }
    
    @Test
    @DisplayName("Should load presentation from valid XML file")
    void loadPresentationShouldLoadFromValidXMLFile() throws IOException {
        // Arrange - create a valid XML file
        File validXmlFile = tempDir.resolve("valid.xml").toFile();
        String validXml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "  <showtitle>Test Presentation</showtitle>\n" +
                "  <slide>\n" +
                "    <title>Test Slide</title>\n" +
                "    <item kind=\"text\" level=\"1\">Test Item 1</item>\n" +
                "    <item kind=\"text\" level=\"2\">Test Item 2</item>\n" +
                "  </slide>\n" +
                "</presentation>";
        Files.writeString(validXmlFile.toPath(), validXml);
        
        // Act
        loader.loadPresentation(presentation, validXmlFile.getAbsolutePath());
        
        // Assert
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be 'Test Presentation'");
        assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
        
        Slide slide = presentation.getSlide(0);
        assertNotNull(slide, "Slide should exist");
        assertEquals("Test Slide", slide.getTitle(), "Slide title should be 'Test Slide'");
        assertEquals(2, slide.getSize(), "Slide should have 2 items");
    }
    
    @Test
    @DisplayName("Should save presentation to XML file")
    void savePresentationShouldSaveToXMLFile() throws IOException {
        // Arrange - create a simple presentation
        presentation.setTitle("Save Test");
        Slide slide = new Slide();
        slide.setTitle("Save Test Slide");
        slide.append(new TextItem(1, "Test Text Item"));
        presentation.append(slide);
        
        // Create file path for saving
        File saveFile = tempDir.resolve("saved.xml").toFile();
        
        // Act
        loader.savePresentation(presentation, saveFile.getAbsolutePath());
        
        // Assert
        assertTrue(saveFile.exists(), "File should exist after saving");
        assertTrue(saveFile.length() > 0, "File should not be empty");
        
        // Try loading the saved file
        Presentation loadedPresentation = new Presentation();
        loader.loadPresentation(loadedPresentation, saveFile.getAbsolutePath());
        
        assertEquals("Save Test", loadedPresentation.getTitle(), "Loaded presentation should have correct title");
        assertEquals(1, loadedPresentation.getSize(), "Loaded presentation should have 1 slide");
        assertEquals("Save Test Slide", loadedPresentation.getSlide(0).getTitle(), 
                "Loaded slide should have correct title");
    }
    
    @Test
    @DisplayName("Should handle various types of slide items correctly")
    void loadPresentationShouldHandleVariousSlideItemTypes() throws IOException {
        // Arrange - create XML with text and image items
        File xmlFile = tempDir.resolve("items.xml").toFile();
        String xml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "  <showtitle>Items Test</showtitle>\n" +
                "  <slide>\n" +
                "    <title>Items Slide</title>\n" +
                "    <item kind=\"text\" level=\"1\">Text Item</item>\n" +
                "    <item kind=\"image\" level=\"2\">test.jpg</item>\n" +
                "    <item kind=\"unknown\" level=\"3\">Unknown Item</item>\n" +
                "  </slide>\n" +
                "</presentation>";
        Files.writeString(xmlFile.toPath(), xml);
        
        // Act
        loader.loadPresentation(presentation, xmlFile.getAbsolutePath());
        
        // Assert
        assertEquals("Items Test", presentation.getTitle());
        assertEquals(1, presentation.getSize());
        
        Slide slide = presentation.getSlide(0);
        assertEquals("Items Slide", slide.getTitle());
        
        // Should have 2 items (text and image), unknown type should be ignored
        assertEquals(2, slide.getSize(), "Should have 2 valid items (text and image)");
        
        // Check first item is TextItem
        assertTrue(slide.getSlideItem(0) instanceof TextItem, "First item should be a TextItem");
        TextItem textItem = (TextItem) slide.getSlideItem(0);
        assertEquals(1, textItem.getLevel(), "Text item should have level 1");
        assertEquals("Text Item", textItem.getText(), "Text item should have correct text");
        
        // Check second item is BitmapItem
        assertTrue(slide.getSlideItem(1) instanceof BitmapItem, "Second item should be a BitmapItem");
        BitmapItem bitmapItem = (BitmapItem) slide.getSlideItem(1);
        assertEquals(2, bitmapItem.getLevel(), "Image item should have level 2");
        assertEquals("test.jpg", bitmapItem.getImageName(), "Image item should have correct name");
    }
    
    @Test
    @DisplayName("Should handle malformed level attribute")
    void loadPresentationShouldHandleMalformedLevelAttribute() throws IOException {
        // Arrange - create XML with malformed level attribute
        File xmlFile = tempDir.resolve("malformed.xml").toFile();
        String xml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "  <showtitle>Malformed Test</showtitle>\n" +
                "  <slide>\n" +
                "    <title>Malformed Slide</title>\n" +
                "    <item kind=\"text\" level=\"not-a-number\">Malformed Level</item>\n" +
                "  </slide>\n" +
                "</presentation>";
        Files.writeString(xmlFile.toPath(), xml);
        
        // Act - this should not throw exception, but use default level
        loader.loadPresentation(presentation, xmlFile.getAbsolutePath());
        
        // Assert
        assertEquals("Malformed Test", presentation.getTitle());
        assertEquals(1, presentation.getSize());
        
        Slide slide = presentation.getSlide(0);
        assertEquals(1, slide.getSize(), "Should have 1 item despite malformed level");
        
        // The item should have the default level (1)
        assertEquals(1, slide.getSlideItem(0).getLevel(), "Item should have default level 1");
    }
}
