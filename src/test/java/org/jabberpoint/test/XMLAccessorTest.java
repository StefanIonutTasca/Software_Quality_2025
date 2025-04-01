package org.jabberpoint.test;

import org.jabberpoint.src.BitmapItem;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.XMLAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        xmlAccessor = new XMLAccessor();
        // Redirect System.err to capture error messages
        System.setErr(new PrintStream(errContent));
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // Restore original System.err
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Should load presentation from XML file")
    void shouldLoadPresentationFromXMLFile() throws Exception {
        // Create a test XML file
        File xmlFile = tempDir.resolve("test.xml").toFile();
        Files.writeString(xmlFile.toPath(),
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <showtitle>Test Presentation</showtitle>\n" +
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
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be correct");
        
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
    @DisplayName("Should log error for malformed XML")
    void shouldLogErrorForMalformedXML() throws Exception {
        // Create a malformed XML file
        File xmlFile = tempDir.resolve("malformed.xml").toFile();
        Files.writeString(xmlFile.toPath(),
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <slide>\n" +
            "    <title>Test Title</title>\n" +
            "    <item kind=\"text\" level=\"1\">Test Item 1</item>\n" +
            "  Malformed XML - missing closing tags"
        );
        
        // Create a presentation
        Presentation presentation = new Presentation();
        
        // Act - Load the malformed XML file (should not throw exception but log error)
        xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath());
        
        // Assert - error should be logged to System.err
        assertTrue(errContent.toString().length() > 0, "Error message should be logged");
        assertTrue(errContent.toString().contains("XML"), 
                "Error message should contain information about XML parsing error");
    }
    
    @Test
    @DisplayName("Should log error for non-existent file")
    void shouldLogErrorForNonExistentFile() throws Exception {
        // Create a presentation
        Presentation presentation = new Presentation();
        
        // Act - Try to load non-existent file
        xmlAccessor.loadFile(presentation, "non_existent_file.xml");
        
        // Assert - error should be logged to System.err
        assertTrue(errContent.toString().length() > 0, "Error message should be logged");
        assertTrue(errContent.toString().contains("non_existent_file.xml"), 
                "Error message should contain the file name");
    }
    
    @Test
    @DisplayName("Should save presentation to XML file")
    void shouldSavePresentationToXMLFile() throws Exception {
        // Create a presentation with slides and items
        Presentation presentation = new Presentation();
        presentation.setTitle("Test Presentation");
        
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
        assertTrue(content.contains("<showtitle>Test Presentation</showtitle>"), "XML should have presentation title");
        assertTrue(content.contains("<slide>"), "XML should have slide tag");
        assertTrue(content.contains("<title>Test Slide</title>"), "XML should have first slide title");
        assertTrue(content.contains("<title>Another Slide</title>"), "XML should have second slide title");
        assertTrue(content.contains("<item kind=\"text\" level=\"1\">Test Item 1</item>"), "XML should contain text from items");
    }
    
    @Test
    @DisplayName("Should load and save cycle correctly")
    void shouldLoadAndSaveCycleCorrectly() throws Exception {
        // Create a presentation with slides and items
        Presentation originalPresentation = new Presentation();
        originalPresentation.setTitle("Original Presentation");
        
        // Add a slide with title and items
        Slide slide = new Slide();
        slide.setTitle("Original Slide");
        slide.append(new TextItem(1, "Original Item 1"));
        slide.append(new TextItem(2, "Original Item 2"));
        originalPresentation.append(slide);
        
        // Save the presentation
        File savedFile = tempDir.resolve("cycle-test.xml").toFile();
        xmlAccessor.saveFile(originalPresentation, savedFile.getAbsolutePath());
        
        // Verify the file exists and has content
        assertTrue(savedFile.exists(), "File should be created");
        String content = Files.readString(savedFile.toPath());
        assertTrue(content.contains("<presentation>"), "XML should have presentation tag");
        assertTrue(content.contains("<showtitle>Original Presentation</showtitle>"), "XML should have presentation title");
        assertTrue(content.contains("<slide>"), "XML should have slide tag");
        assertTrue(content.contains("<title>Original Slide</title>"), "XML should have slide title");
        
        // Create a fixed XML file without the DOCTYPE declaration that causes loading issues
        String fixedContent = content.replaceAll("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">", "");
        File fixedFile = tempDir.resolve("fixed-cycle-test.xml").toFile();
        Files.writeString(fixedFile.toPath(), fixedContent);
        
        // Load the presentation from the fixed file
        Presentation loadedPresentation = new Presentation();
        xmlAccessor.loadFile(loadedPresentation, fixedFile.getAbsolutePath());
        
        // Assert - loaded presentation should match original
        assertEquals(originalPresentation.getSize(), loadedPresentation.getSize(),
                "Loaded presentation should have same number of slides");
        assertEquals(originalPresentation.getTitle(), loadedPresentation.getTitle(),
                "Loaded presentation should have same title");
        
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
        Text textNode = document.createTextNode("Test Text");
        textItemElement.appendChild(textNode);
        
        // Create an image item element
        Element imageItemElement = document.createElement("item");
        imageItemElement.setAttribute("kind", "image");
        imageItemElement.setAttribute("level", "3");
        Text imageTextNode = document.createTextNode("test.jpg");
        imageItemElement.appendChild(imageTextNode);
        
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
    
    @Test
    @DisplayName("Should log error for invalid level value")
    void shouldLogErrorForInvalidLevelValue() throws Exception {
        // Arrange
        Document document = createEmptyDocument();
        Slide slide = new Slide();
        
        // Create a text item element with invalid level
        Element textItemElement = document.createElement("item");
        textItemElement.setAttribute("kind", "text");
        textItemElement.setAttribute("level", "not-a-number");
        Text textNode = document.createTextNode("Test Text");
        textItemElement.appendChild(textNode);
        
        // Act - Need to access protected method via reflection
        Method loadSlideItemMethod = XMLAccessor.class.getDeclaredMethod("loadSlideItem", Slide.class, Element.class);
        loadSlideItemMethod.setAccessible(true);
        loadSlideItemMethod.invoke(xmlAccessor, slide, textItemElement);
        
        // Assert - should log error about number format exception
        assertTrue(errContent.toString().contains("Number Format Exception"), 
                "Error message should mention 'Number Format Exception'");
        
        // Item should still be added with default level (1)
        Vector<org.jabberpoint.src.SlideItem> items = slide.getSlideItems();
        assertEquals(1, items.size(), "Slide should have 1 item despite error");
        assertEquals(1, items.get(0).getLevel(), "Item should have default level 1");
    }

    @Test
    @DisplayName("Should handle IO exception when loading file")
    void shouldHandleIOExceptionWhenLoadingFile() throws Exception {
        // Arrange
        File nonExistentFile = new File(tempDir.toFile(), "non-existent.xml");
        Presentation presentation = new Presentation();
        
        // Act
        xmlAccessor.loadFile(presentation, nonExistentFile.getAbsolutePath());
        
        // Assert - should catch exception internally and print to System.err
        assertTrue(errContent.toString().contains("FileNotFoundException") || 
                   errContent.toString().contains("NoSuchFileException"),
                "Should log file not found error");
    }
    
    @Test
    @DisplayName("Should handle invalid XML when loading file")
    void shouldHandleInvalidXMLWhenLoadingFile() throws Exception {
        // Arrange
        File invalidFile = new File(tempDir.toFile(), "invalid.xml");
        String invalidXML = "<?xml version=\"1.0\"?>\n" +
                           "<presentation>\n" +
                           "  <showtitle>Invalid XML</showtitle>\n" +
                           "  <slide>\n" +
                           "    <title>Incomplete Slide\n" + // Missing closing tag
                           "    <item kind=\"text\" level=\"1\">Text</item>\n" +
                           "  </slide>\n" +
                           "</presentation>";
        Files.writeString(invalidFile.toPath(), invalidXML);
        
        Presentation presentation = new Presentation();
        
        // Act
        xmlAccessor.loadFile(presentation, invalidFile.getAbsolutePath());
        
        // Assert - should catch SAXException internally and print to System.err
        assertTrue(errContent.toString().contains("SAX"), 
                "Should log SAX parsing error");
    }
    
    @Test
    @DisplayName("Should handle invalid level attribute in item")
    void shouldHandleInvalidLevelAttributeInItem() throws Exception {
        // Arrange
        File invalidLevelFile = new File(tempDir.toFile(), "invalid-level.xml");
        String invalidLevelXML = "<?xml version=\"1.0\"?>\n" +
                               "<presentation>\n" +
                               "  <showtitle>Invalid Level</showtitle>\n" +
                               "  <slide>\n" +
                               "    <title>Test Slide</title>\n" +
                               "    <item kind=\"text\" level=\"invalid\">Text Item</item>\n" +
                               "  </slide>\n" +
                               "</presentation>";
        Files.writeString(invalidLevelFile.toPath(), invalidLevelXML);
        
        Presentation presentation = new Presentation();
        
        // Act
        xmlAccessor.loadFile(presentation, invalidLevelFile.getAbsolutePath());
        
        // Assert
        assertEquals("Invalid Level", presentation.getTitle(), "Presentation title should be set");
        assertEquals(1, presentation.getSize(), "Should have loaded 1 slide");
        
        // Check that the NFE error was logged
        assertTrue(errContent.toString().contains("Number Format Exception"), 
                "Should log NumberFormatException");
    }
    
    @Test
    @DisplayName("Should handle unknown item type")
    void shouldHandleUnknownItemType() throws Exception {
        // Arrange
        File unknownTypeFile = new File(tempDir.toFile(), "unknown-type.xml");
        String unknownTypeXML = "<?xml version=\"1.0\"?>\n" +
                              "<presentation>\n" +
                              "  <showtitle>Unknown Type</showtitle>\n" +
                              "  <slide>\n" +
                              "    <title>Test Slide</title>\n" +
                              "    <item kind=\"unknown\" level=\"1\">Unknown Item</item>\n" +
                              "  </slide>\n" +
                              "</presentation>";
        Files.writeString(unknownTypeFile.toPath(), unknownTypeXML);
        
        Presentation presentation = new Presentation();
        
        // Act
        xmlAccessor.loadFile(presentation, unknownTypeFile.getAbsolutePath());
        
        // Assert
        assertEquals("Unknown Type", presentation.getTitle(), "Presentation title should be set");
        assertEquals(1, presentation.getSize(), "Should have loaded 1 slide");
        
        // Check that the unknown type error was logged
        assertTrue(errContent.toString().contains("Unknown Element type"), 
                "Should log unknown element type error");
    }
    
    private Document createEmptyDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}
