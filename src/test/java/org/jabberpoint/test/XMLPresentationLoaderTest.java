package org.jabberpoint.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.parsers.ParserConfigurationException;
import org.jabberpoint.src.model.BitmapItem;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;
import org.jabberpoint.src.model.SlideItem;
import org.jabberpoint.src.model.TextItem;
import org.jabberpoint.src.io.XMLPresentationLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XMLPresentationLoaderTest {

    private XMLPresentationLoader xmlLoader;
    private Presentation presentation;
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        xmlLoader = new XMLPresentationLoader();
        presentation = new Presentation();
        Style.createStyles(); // Initialize styles for tests
        
        // Redirect System.err to capture error messages
        System.setErr(new PrintStream(errContent));
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // Restore original System.err
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("loadPresentation should parse XML and populate presentation")
    void loadPresentationShouldParseXmlAndPopulatePresentation() throws IOException {
        // Create a test XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "<item kind=\"image\" level=\"2\">test_image.png</item>\n" +
                "</slide>\n" +
                "<slide>\n" +
                "<title>Slide 2</title>\n" +
                "<item kind=\"text\" level=\"3\">Text Item 2</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Path xmlFile = tempDir.resolve("test_presentation.xml");
        Files.writeString(xmlFile, xmlContent);
        
        // Load the XML file
        xmlLoader.loadPresentation(presentation, xmlFile.toString());
        
        // Verify presentation was populated correctly
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set");
        assertEquals(2, presentation.getSize(), "Presentation should have 2 slides");
        
        // Verify slide 1
        Slide slide1 = presentation.getSlide(0);
        assertEquals("Slide 1", slide1.getTitle(), "Slide 1 title should be set");
        assertEquals(2, slide1.getSize(), "Slide 1 should have 2 items");
        
        // Verify slide 1 items
        SlideItem item1 = slide1.getSlideItem(0);
        assertTrue(item1 instanceof TextItem, "First item should be a TextItem");
        assertEquals(1, item1.getLevel(), "First item should have level 1");
        assertEquals("Text Item 1", ((TextItem) item1).getText(), "First item text should be correct");
        
        SlideItem item2 = slide1.getSlideItem(1);
        assertTrue(item2 instanceof BitmapItem, "Second item should be a BitmapItem");
        assertEquals(2, item2.getLevel(), "Second item should have level 2");
        assertEquals("test_image.png", ((BitmapItem) item2).getName(), "Second item image name should be correct");
        
        // Verify slide 2
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Slide 2", slide2.getTitle(), "Slide 2 title should be set");
        assertEquals(1, slide2.getSize(), "Slide 2 should have 1 item");
        
        // Verify slide 2 item
        SlideItem item3 = slide2.getSlideItem(0);
        assertTrue(item3 instanceof TextItem, "Third item should be a TextItem");
        assertEquals(3, item3.getLevel(), "Third item should have level 3");
        assertEquals("Text Item 2", ((TextItem) item3).getText(), "Third item text should be correct");
    }
    
    @Test
    @DisplayName("loadPresentation should handle invalid level attribute gracefully")
    void loadPresentationShouldHandleInvalidLevelAttributeGracefully() throws IOException {
        // Create a test XML file with an invalid level attribute
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"text\" level=\"invalid\">Text Item with invalid level</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Path xmlFile = tempDir.resolve("test_invalid_level.xml");
        Files.writeString(xmlFile, xmlContent);
        
        // Clear the err stream
        errContent.reset();
        
        // Load the XML file
        xmlLoader.loadPresentation(presentation, xmlFile.toString());
        
        // Verify error message was logged
        assertTrue(errContent.toString().contains("Number format exception for level"), 
                "Error message for invalid level should be logged");
        
        // Verify presentation was populated and used default level (1)
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set");
        assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
        
        Slide slide = presentation.getSlide(0);
        SlideItem item = slide.getSlideItem(0);
        assertEquals(1, item.getLevel(), "Item should have default level 1 when level is invalid");
    }
    
    @Test
    @DisplayName("loadPresentation should handle unknown item kind gracefully")
    void loadPresentationShouldHandleUnknownItemKindGracefully() throws IOException {
        // Create a test XML file with an unknown item kind
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"unknown\" level=\"1\">Unknown item type</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Path xmlFile = tempDir.resolve("test_unknown_kind.xml");
        Files.writeString(xmlFile, xmlContent);
        
        // Clear the err stream
        errContent.reset();
        
        // Load the XML file - should not throw exception
        xmlLoader.loadPresentation(presentation, xmlFile.toString());
        
        // Verify error message was logged
        assertTrue(errContent.toString().contains("Unknown element type"), 
                "Error message for unknown element type should be logged");
        
        // Verify presentation was populated but the unknown item was not added
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set");
        assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
        
        Slide slide = presentation.getSlide(0);
        assertEquals(0, slide.getSize(), "Slide should have 0 items as the unknown kind was not added");
    }
    
    @Test
    @DisplayName("loadPresentation should throw IOException for file not found")
    void loadPresentationShouldThrowIOExceptionForFileNotFound() {
        // Try to load a non-existent file
        String nonExistentFile = tempDir.resolve("non_existent_file.xml").toString();
        
        // Should throw IOException
        IOException exception = assertThrows(IOException.class, 
                () -> xmlLoader.loadPresentation(presentation, nonExistentFile),
                "Should throw IOException for non-existent file");
        
        // Verify exception message
        assertTrue(exception.getMessage().contains("non_existent_file.xml") || 
                   exception.getMessage().contains("No such file") ||
                   exception.toString().contains("FileNotFoundException"),
                "Exception should contain file name or 'No such file' message");
        
        // Presentation should remain unchanged
        assertNull(presentation.getTitle(), "Presentation title should remain null");
        assertEquals(0, presentation.getSize(), "Presentation should have 0 slides");
    }
    
    @Test
    @DisplayName("loadPresentation should throw IOException for malformed XML")
    void loadPresentationShouldThrowIOExceptionForMalformedXml() throws IOException {
        // Create a malformed XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Malformed XML</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                // Missing closing tags
                "</presentation>";
        
        Path xmlFile = tempDir.resolve("malformed.xml");
        Files.writeString(xmlFile, xmlContent);
        
        // Should throw IOException with SAXException cause
        IOException exception = assertThrows(IOException.class, 
                () -> xmlLoader.loadPresentation(presentation, xmlFile.toString()),
                "Should throw IOException for malformed XML");
        
        // Verify exception message
        assertTrue(exception.getMessage().contains("Parse error"), 
                "Exception should contain 'Parse error' message");
    }
    
    @Test
    @DisplayName("savePresentation should write presentation to XML file")
    void savePresentationShouldWritePresentationToXmlFile() throws IOException {
        // Create a presentation with slides and items
        presentation.setTitle("Saved Presentation");
        
        // Add slide 1 with text and image items
        Slide slide1 = new Slide();
        slide1.setTitle("Saved Slide 1");
        slide1.append(new TextItem(1, "Saved Text Item 1"));
        slide1.append(new BitmapItem(2, "saved_image.png"));
        presentation.append(slide1);
        
        // Add slide 2 with a text item
        Slide slide2 = new Slide();
        slide2.setTitle("Saved Slide 2");
        slide2.append(new TextItem(3, "Saved Text Item 2"));
        presentation.append(slide2);
        
        // Save the presentation to a file
        String outputFile = tempDir.resolve("saved_presentation.xml").toString();
        xmlLoader.savePresentation(presentation, outputFile);
        
        // Verify the file was created
        File savedFile = new File(outputFile);
        assertTrue(savedFile.exists(), "Output file should exist");
        assertTrue(savedFile.length() > 0, "Output file should not be empty");
        
        // Read the file content
        String savedContent = Files.readString(savedFile.toPath());
        
        // Verify the XML structure
        assertTrue(savedContent.contains("<?xml version=\"1.0\"?>"), "XML declaration should be present");
        assertTrue(savedContent.contains("<presentation>"), "Root element should be present");
        assertTrue(savedContent.contains("<showtitle>Saved Presentation</showtitle>"), "Show title should be present");
        assertTrue(savedContent.contains("<slide>"), "Slide element should be present");
        assertTrue(savedContent.contains("<title>Saved Slide 1</title>"), "Slide 1 title should be present");
        assertTrue(savedContent.contains("<item kind=\"text\" level=\"1\">Saved Text Item 1</item>"), 
                "Text item should be present with correct attributes");
        assertTrue(savedContent.contains("<item kind=\"image\" level=\"2\">saved_image.png</item>"), 
                "Image item should be present with correct attributes");
        assertTrue(savedContent.contains("<title>Saved Slide 2</title>"), "Slide 2 title should be present");
        
        // Check that DOCTYPE declaration is not present (memory mentioned removing it)
        assertFalse(savedContent.contains("<!DOCTYPE"), 
                "DOCTYPE declaration should not be present in saved file");
        
        // Bonus: Load the saved file to verify it can be parsed back
        Presentation loadedPresentation = new Presentation();
        xmlLoader.loadPresentation(loadedPresentation, outputFile);
        
        assertEquals("Saved Presentation", loadedPresentation.getTitle(), "Loaded presentation title should match");
        assertEquals(2, loadedPresentation.getSize(), "Loaded presentation should have 2 slides");
    }
    
    @Test
    @DisplayName("savePresentation should handle unsupported SlideItem types")
    void savePresentationShouldHandleUnsupportedSlideItemTypes() throws IOException {
        // Create a presentation with a mock SlideItem that is neither TextItem nor BitmapItem
        presentation = new Presentation();
        presentation.setTitle("Mock Presentation");
        
        Slide slide = new Slide();
        slide.setTitle("Mock Slide");
        
        // Create a custom SlideItem implementation that is neither TextItem nor BitmapItem
        final SlideItem mockItem = new SlideItem() {
            @Override
            public String toString() {
                return "MockSlideItem";
            }
            
            @Override
            public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle) {
                return new Rectangle(0, 0, 50, 50);
            }
            
            @Override
            public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver observer) {
                // Do nothing
            }
            
            @Override
            public int getLevel() {
                return 1;
            }
        };
        
        slide.append(mockItem);
        presentation.append(slide);
        
        // Save the presentation to a file without checking error messages
        String outputFile = tempDir.resolve("mock_presentation.xml").toString();
        
        // Save first to generate the file
        xmlLoader.savePresentation(presentation, outputFile);
            
        // Verify the file was created
        File savedFile = new File(outputFile);
        assertTrue(savedFile.exists(), "Output file should exist");
        
        // Read the file content
        String savedContent = Files.readString(savedFile.toPath());
        
        // Verify the XML structure - the mock item should be ignored
        assertTrue(savedContent.contains("<presentation>"), "Root element should be present");
        assertTrue(savedContent.contains("<showtitle>Mock Presentation</showtitle>"), "Show title should be present");
        assertTrue(savedContent.contains("<slide>"), "Slide element should be present");
        assertTrue(savedContent.contains("<title>Mock Slide</title>"), "Slide title should be present");
        
        // The mock item should not appear as a regular item since it's neither TextItem nor BitmapItem
        assertFalse(savedContent.contains("<item kind=\"text\" level=\"1\">MockSlideItem</item>"), 
                "Unknown item types should not be included in output");
    }
    
    @Test
    @DisplayName("getTitle should extract title from XML element")
    void getTitleShouldExtractTitleFromXmlElement() throws Exception {
        // Create XML file with a title
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Test Title Extraction</showtitle>\n" +
                "</presentation>";
        
        Path xmlFile = tempDir.resolve("test_title.xml");
        Files.writeString(xmlFile, xmlContent);
        
        // Load the file to test the getTitle method indirectly
        xmlLoader.loadPresentation(presentation, xmlFile.toString());
        
        assertEquals("Test Title Extraction", presentation.getTitle(), 
                "getTitle method should extract title correctly from XML element");
    }
    
    @Test
    @DisplayName("loadSlideItem should create correct SlideItem based on type")
    void loadSlideItemShouldCreateCorrectSlideItemBasedOnType() throws IOException {
        // Create factory test cases for both text and image
        String textXml = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Factory Test</showtitle>\n" +
                "<slide>\n" +
                "<title>Factory Slide</title>\n" +
                "<item kind=\"text\" level=\"2\">Text from factory</item>\n" +
                "<item kind=\"image\" level=\"3\">image_from_factory.png</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Path xmlFile = tempDir.resolve("factory_test.xml");
        Files.writeString(xmlFile, textXml);
        
        // No need to mock SlideItemFactory as it uses static methods
        
        try {
            // Create and load the presentation
            xmlLoader.loadPresentation(presentation, xmlFile.toString());
            
            // Verify the items were created with correct types and levels
            assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
            
            Slide slide = presentation.getSlide(0);
            assertEquals(2, slide.getSize(), "Slide should have 2 items");
            
            SlideItem textItem = slide.getSlideItem(0);
            assertTrue(textItem instanceof TextItem, "First item should be a TextItem");
            assertEquals(2, textItem.getLevel(), "Text item should have level 2");
            
            SlideItem imageItem = slide.getSlideItem(1);
            assertTrue(imageItem instanceof BitmapItem, "Second item should be a BitmapItem");
            assertEquals(3, imageItem.getLevel(), "Image item should have level 3");
        } 
        finally {
            // No cleanup needed
        }
    }
}
