package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.XMLAccessor;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for XMLAccessor class
 */
class XMLAccessorTest {

    private XMLAccessor xmlAccessor;
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        xmlAccessor = new XMLAccessor();
    }

    @Test
    @DisplayName("Should load presentation from XML file")
    void shouldLoadPresentationFromXMLFile() throws Exception {
        // Create a test XML file
        File xmlFile = tempDir.resolve("test.xml").toFile();
        Files.writeString(xmlFile.toPath(),
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <slide>\n" +
            "    <title>Test Title</title>\n" +
            "    <item kind=\"text\" level=\"1\">Test Item 1</item>\n" +
            "    <item kind=\"text\" level=\"2\">Test Item 2</item>\n" +
            "  </slide>\n" +
            "  <slide>\n" +
            "    <title>Second Slide</title>\n" +
            "    <item kind=\"text\" level=\"1\">Another Item</item>\n" +
            "  </slide>\n" +
            "</presentation>"
        );
        
        // Create a presentation
        Presentation presentation = new Presentation();
        
        // Act - Load the XML file
        xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath());
        
        // Assert
        assertEquals(2, presentation.getSize(), "Presentation should have 2 slides");
        
        // Test first slide
        Slide firstSlide = presentation.getSlide(0);
        assertEquals("Test Title", firstSlide.getTitle(), "First slide should have the correct title");
        assertEquals(2, firstSlide.getSlideItems().size(), "First slide should have 2 items");
        
        // Test second slide
        Slide secondSlide = presentation.getSlide(1);
        assertEquals("Second Slide", secondSlide.getTitle(), "Second slide should have the correct title");
        assertEquals(1, secondSlide.getSlideItems().size(), "Second slide should have 1 item");
    }
    
    @Test
    @DisplayName("Should throw IOException for malformed XML")
    void shouldThrowIOExceptionForMalformedXML() throws Exception {
        // Create a malformed XML file
        File xmlFile = tempDir.resolve("malformed.xml").toFile();
        Files.writeString(xmlFile.toPath(),
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <slide>\n" +
            "    <title>Test Title</title>\n" +
            "    <item kind=\"text\" level=\"1\">Test Item 1</item>\n" +
            "  <!-- Missing closing tags -->"
        );
        
        // Create a presentation
        Presentation presentation = new Presentation();
        
        // Act & Assert
        assertThrows(Exception.class, () -> 
            xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath()),
            "Should throw exception for malformed XML"
        );
    }
    
    @Test
    @DisplayName("Should save presentation to XML file")
    void shouldSavePresentationToXMLFile() throws Exception {
        // Create a presentation with slides and items
        Presentation presentation = new Presentation();
        
        // Add a slide with title and items
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Item 1"));
        slide.append(new TextItem(2, "Test Item 2"));
        presentation.append(slide);
        
        // Add another slide
        Slide slide2 = new Slide();
        slide2.setTitle("Another Slide");
        slide2.append(new TextItem(1, "Another Item"));
        presentation.append(slide2);
        
        // Create a file to save to
        File savedFile = tempDir.resolve("saved.xml").toFile();
        
        // Act - Save the presentation
        xmlAccessor.saveFile(presentation, savedFile.getAbsolutePath());
        
        // Assert - File should exist and contain the XML
        assertTrue(savedFile.exists(), "File should be created");
        String content = Files.readString(savedFile.toPath());
        
        // Basic content checks
        assertTrue(content.contains("<presentation>"), "XML should have presentation tag");
        assertTrue(content.contains("<slide>"), "XML should have slide tag");
        assertTrue(content.contains("<title>Test Slide</title>"), "XML should have first slide title");
        assertTrue(content.contains("<title>Another Slide</title>"), "XML should have second slide title");
        assertTrue(content.contains("Test Item 1"), "XML should contain text from items");
    }
    
    @Test
    @DisplayName("Should load and save cycle correctly")
    void shouldLoadAndSaveCycleCorrectly() throws Exception {
        // Create a presentation with slides and items
        Presentation originalPresentation = new Presentation();
        
        // Add a slide with title and items
        Slide slide = new Slide();
        slide.setTitle("Original Slide");
        slide.append(new TextItem(1, "Original Item 1"));
        slide.append(new TextItem(2, "Original Item 2"));
        originalPresentation.append(slide);
        
        // Save the presentation
        File savedFile = tempDir.resolve("cycle-test.xml").toFile();
        xmlAccessor.saveFile(originalPresentation, savedFile.getAbsolutePath());
        
        // Load the presentation into a new object
        Presentation loadedPresentation = new Presentation();
        xmlAccessor.loadFile(loadedPresentation, savedFile.getAbsolutePath());
        
        // Assert - loaded presentation should match original
        assertEquals(originalPresentation.getSize(), loadedPresentation.getSize(),
                "Loaded presentation should have same number of slides");
        
        Slide originalSlide = originalPresentation.getSlide(0);
        Slide loadedSlide = loadedPresentation.getSlide(0);
        
        assertEquals(originalSlide.getTitle(), loadedSlide.getTitle(),
                "Loaded slide should have same title");
        assertEquals(originalSlide.getSlideItems().size(), loadedSlide.getSlideItems().size(),
                "Loaded slide should have same number of items");
    }
    
    @Test
    @DisplayName("Should handle text and image slide items")
    void shouldHandleTextAndImageSlideItems() throws Exception {
        // Skip test in headless environment for image loading
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping image loading test in headless environment");
            
        // Arrange
        Document document = createEmptyDocument();
        Slide slide = new Slide();
        
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
        
        // Act - Need to access protected method via reflection
        Method loadSlideItemMethod = XMLAccessor.class.getDeclaredMethod("loadSlideItem", Slide.class, Element.class);
        loadSlideItemMethod.setAccessible(true);
        loadSlideItemMethod.invoke(xmlAccessor, slide, textItemElement);
        loadSlideItemMethod.invoke(xmlAccessor, slide, imageItemElement);
        
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

    private Document createEmptyDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}
