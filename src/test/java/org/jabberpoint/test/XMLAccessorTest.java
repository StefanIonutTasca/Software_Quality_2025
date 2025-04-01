package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.XMLAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for XMLAccessor class
 */
class XMLAccessorTest {

    private XMLAccessor xmlAccessor;
    
    @Mock
    private Presentation mockPresentation;
    
    @Mock
    private Slide mockSlide;
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        xmlAccessor = new XMLAccessor();
    }

    @Test
    @DisplayName("Should load presentation from XML file")
    void shouldLoadPresentationFromXMLFile() throws Exception {
        // Create a test XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "  <showtitle>Test Presentation</showtitle>\n" +
                "  <slide>\n" +
                "    <title>Slide 1</title>\n" +
                "    <item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "    <item kind=\"image\" level=\"2\">test.jpg</item>\n" +
                "  </slide>\n" +
                "  <slide>\n" +
                "    <title>Slide 2</title>\n" +
                "    <item kind=\"text\" level=\"1\">Text Item 2</item>\n" +
                "  </slide>\n" +
                "</presentation>";
        
        File xmlFile = tempDir.resolve("test.xml").toFile();
        Files.writeString(xmlFile.toPath(), xmlContent);
        
        // Setup a real presentation to capture the loaded content
        Presentation presentation = new Presentation();
        
        // Act
        xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath());
        
        // Assert
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set correctly");
        assertEquals(2, presentation.getSize(), "Presentation should have 2 slides");
        
        // Verify first slide
        Slide slide1 = presentation.getSlide(0);
        assertEquals("Slide 1", slide1.getTitle(), "First slide title should be set correctly");
        assertEquals(2, slide1.getSlideItems().size(), "First slide should have 2 items");
        
        // Verify second slide
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Slide 2", slide2.getTitle(), "Second slide title should be set correctly");
        assertEquals(1, slide2.getSlideItems().size(), "Second slide should have 1 item");
    }
    
    @Test
    @DisplayName("Should handle malformed XML when loading file")
    void shouldHandleMalformedXMLWhenLoadingFile() throws Exception {
        // Create a malformed XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "  <showtitle>Malformed XML</showtitle>\n" +
                "  <slide>\n" +
                "    <title>Slide With Error</title>\n" +
                "    <item kind=\"text\" level=\"invalid\">Text Item</item>\n" +
                "  </slide>\n" +
                "</presentation>";
        
        File xmlFile = tempDir.resolve("malformed.xml").toFile();
        Files.writeString(xmlFile.toPath(), xmlContent);
        
        // Setup a real presentation
        Presentation presentation = new Presentation();
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath()));
        
        // Verify some content was still loaded
        assertEquals("Malformed XML", presentation.getTitle(), "Presentation title should still be loaded");
    }
    
    @Test
    @DisplayName("Should handle non-existent file when loading")
    void shouldHandleNonExistentFileWhenLoading() {
        // Setup
        Presentation presentation = new Presentation();
        
        // Act & Assert - Should not throw unhandled exception
        assertDoesNotThrow(() -> xmlAccessor.loadFile(presentation, "non_existent_file.xml"));
    }
    
    @Test
    @DisplayName("Should save presentation to XML file")
    void shouldSavePresentationToXMLFile() throws Exception {
        // Setup
        File xmlFile = tempDir.resolve("saved.xml").toFile();
        
        // Create a presentation with some content
        Presentation presentation = new Presentation();
        presentation.setTitle("Saved Presentation");
        
        // Add slides with items
        Slide slide1 = new Slide();
        slide1.setTitle("First Slide");
        slide1.append(new TextItem(1, "Text on first slide"));
        slide1.append(new BitmapItem(2, "image1.jpg"));
        presentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Second Slide");
        slide2.append(new TextItem(1, "Text on second slide"));
        presentation.append(slide2);
        
        // Act
        xmlAccessor.saveFile(presentation, xmlFile.getAbsolutePath());
        
        // Assert
        assertTrue(xmlFile.exists(), "XML file should have been created");
        String savedContent = Files.readString(xmlFile.toPath());
        
        // Check for expected content
        assertTrue(savedContent.contains("<showtitle>Saved Presentation</showtitle>"), "Saved XML should contain presentation title");
        assertTrue(savedContent.contains("<title>First Slide</title>"), "Saved XML should contain first slide title");
        assertTrue(savedContent.contains("<title>Second Slide</title>"), "Saved XML should contain second slide title");
        assertTrue(savedContent.contains("<item kind=\"text\" level=\"1\">Text on first slide</item>"), "Saved XML should contain text item");
        assertTrue(savedContent.contains("<item kind=\"image\" level=\"2\">image1.jpg</item>"), "Saved XML should contain image item");
    }
    
    @Test
    @DisplayName("Should handle unknown item types when loading slide items")
    void shouldHandleUnknownItemTypesWhenLoadingSlideItems() throws Exception {
        // Create XML with unknown item type
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "  <showtitle>Test Presentation</showtitle>\n" +
                "  <slide>\n" +
                "    <title>Test Slide</title>\n" +
                "    <item kind=\"unknown\" level=\"1\">Unknown Item</item>\n" +
                "  </slide>\n" +
                "</presentation>";
        
        File xmlFile = tempDir.resolve("unknown_type.xml").toFile();
        Files.writeString(xmlFile.toPath(), xmlContent);
        
        // Setup
        Presentation presentation = new Presentation();
        
        // Act
        xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath());
        
        // Assert - Should load the presentation and slide but ignore the unknown item
        assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
        Slide slide = presentation.getSlide(0);
        assertEquals("Test Slide", slide.getTitle(), "Slide title should be set correctly");
        assertEquals(0, slide.getSlideItems().size(), "Slide should have no items since the unknown item should be ignored");
    }
    
    @Test
    @DisplayName("Should handle IO errors when saving file")
    void shouldHandleIOErrorsWhenSavingFile() {
        // Setup - Use a directory as the target file to cause an IO error
        File directory = tempDir.resolve("directory").toFile();
        directory.mkdir();
        
        // Act & Assert
        assertThrows(java.io.IOException.class, 
            () -> xmlAccessor.saveFile(mockPresentation, directory.getAbsolutePath()),
            "Should throw IOException when saving to a directory");
    }
    
    @Test
    @DisplayName("Should load text and image slide items correctly")
    void shouldLoadTextAndImageSlideItemsCorrectly() throws Exception {
        // Setup - Create mock slide
        Slide slide = new Slide();
        
        // Create a test XML document with slide items
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        
        // Create a text item element
        Element textItemElement = document.createElement("item");
        textItemElement.setAttribute("kind", "text");
        textItemElement.setAttribute("level", "2");
        textItemElement.setTextContent("Test Text");
        
        // Create an image item element
        Element imageItemElement = document.createElement("item");
        imageItemElement.setAttribute("kind", "image");
        imageItemElement.setAttribute("level", "3");
        imageItemElement.setTextContent("test.jpg");
        
        // Act - Load both items
        xmlAccessor.loadSlideItem(slide, textItemElement);
        xmlAccessor.loadSlideItem(slide, imageItemElement);
        
        // Assert
        Vector<org.jabberpoint.src.SlideItem> items = slide.getSlideItems();
        assertEquals(2, items.size(), "Slide should have 2 items");
        
        // Verify text item
        assertTrue(items.get(0) instanceof TextItem, "First item should be a TextItem");
        TextItem textItem = (TextItem) items.get(0);
        assertEquals(2, textItem.getLevel(), "Text item should have level 2");
        assertEquals("Test Text", textItem.getText(), "Text item should have correct text");
        
        // Verify image item
        assertTrue(items.get(1) instanceof BitmapItem, "Second item should be a BitmapItem");
        BitmapItem bitmapItem = (BitmapItem) items.get(1);
        assertEquals(3, bitmapItem.getLevel(), "Image item should have level 3");
        assertEquals("test.jpg", bitmapItem.getName(), "Image item should have correct name");
    }
}
