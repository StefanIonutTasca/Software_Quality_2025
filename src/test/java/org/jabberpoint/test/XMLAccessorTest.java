package org.jabberpoint.test;

import org.jabberpoint.src.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XMLAccessorTest {

    private XMLAccessor xmlAccessor;
    private Presentation presentation;
    @TempDir
    Path tempDir;
    
    @BeforeAll
    static void setUpClass() {
        // Initialize Style singleton before any tests run
        Style.getInstance();
    }
    
    @BeforeEach
    void setUp() {
        xmlAccessor = new XMLAccessor();
        presentation = new Presentation();
    }
    
    @Test
    @DisplayName("loadFile should parse XML and populate presentation")
    void loadFileShouldParseXmlAndPopulatePresentation() throws IOException {
        // Create a test XML file
        String xmlContent = 
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <showtitle>Test Presentation</showtitle>\n" +
            "  <slide>\n" +
            "    <title>Slide 1</title>\n" +
            "    <item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
            "    <item kind=\"image\" level=\"2\">test_image.png</item>\n" +
            "  </slide>\n" +
            "  <slide>\n" +
            "    <title>Slide 2</title>\n" +
            "    <item kind=\"text\" level=\"3\">Text Item 2</item>\n" +
            "  </slide>\n" +
            "</presentation>";
        
        String filePath = tempDir.resolve("test.xml").toString();
        Files.writeString(Path.of(filePath), xmlContent);
        
        // Load the XML file
        xmlAccessor.loadFile(presentation, filePath);
        
        // Check presentation title
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set");
        
        // Check slide content
        assertEquals(2, presentation.getSize(), "Presentation should have 2 slides");
        Slide slide1 = presentation.getSlide(0);
        assertEquals("Slide 1", slide1.getTitle(), "Slide 1 title should be set");
        assertEquals(2, slide1.getSize(), "Slide 1 should have 2 items");
        
        // Check slide 1 items
        SlideItem item1 = slide1.getSlideItem(0);
        assertTrue(item1 instanceof TextItem, "First item should be a TextItem");
        assertEquals(1, item1.getLevel(), "First item level should be set to 1");
        assertEquals("Text Item 1", ((TextItem) item1).getText(), "First item text should be set");
        
        SlideItem item2 = slide1.getSlideItem(1);
        assertTrue(item2 instanceof BitmapItem, "Second item should be a BitmapItem");
        assertEquals(2, item2.getLevel(), "Second item level should be set to 2");
        assertEquals("test_image.png", ((BitmapItem) item2).getName(), "Second item image name should be set");
        
        // Check slide 2
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Slide 2", slide2.getTitle(), "Slide 2 title should be set");
        assertEquals(1, slide2.getSize(), "Slide 2 should have 1 item");
        
        // Check slide 2 item
        SlideItem item3 = slide2.getSlideItem(0);
        assertTrue(item3 instanceof TextItem, "Third item should be a TextItem");
        assertEquals(3, item3.getLevel(), "Third item level should be set to 3");
        assertEquals("Text Item 2", ((TextItem) item3).getText(), "Third item text should be set");
    }
    
    @Test
    @DisplayName("loadFile should handle invalid level attribute gracefully")
    void loadFileShouldHandleInvalidLevelAttributeGracefully() throws IOException {
        // Create a test XML file with invalid level
        String xmlContent = 
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <showtitle>Invalid Level Test</showtitle>\n" +
            "  <slide>\n" +
            "    <title>Test Slide</title>\n" +
            "    <item kind=\"text\" level=\"invalid\">Test Item</item>\n" +
            "  </slide>\n" +
            "</presentation>";
        
        String filePath = tempDir.resolve("invalid_level.xml").toString();
        Files.writeString(Path.of(filePath), xmlContent);
        
        // Capture standard error output
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        
        try {
            // Load the XML file
            xmlAccessor.loadFile(presentation, filePath);
            
            // Check that some error message was printed
            assertTrue(errContent.toString().contains("Number Format Exception"), 
                       "Should show NumberFormatException message");
            
            // Check presentation content (should still work with default level)
            assertEquals("Invalid Level Test", presentation.getTitle(), "Presentation title should be set");
            assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
            
            // The item should be added with default level (1)
            Slide slide = presentation.getSlide(0);
            assertEquals(1, slide.getSize(), "Slide should have 1 item");
            assertEquals(1, slide.getSlideItem(0).getLevel(), "Item should use default level 1");
        } finally {
            System.setErr(originalErr);
        }
    }
    
    @Test
    @DisplayName("loadFile should handle unknown item kind gracefully")
    void loadFileShouldHandleUnknownItemKindGracefully() throws IOException {
        // Create a test XML file with unknown item kind
        String xmlContent = 
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <showtitle>Unknown Kind Test</showtitle>\n" +
            "  <slide>\n" +
            "    <title>Test Slide</title>\n" +
            "    <item kind=\"unknown\" level=\"1\">Test Item</item>\n" +
            "  </slide>\n" +
            "</presentation>";
        
        String filePath = tempDir.resolve("unknown_kind.xml").toString();
        Files.writeString(Path.of(filePath), xmlContent);
        
        // Capture standard error output
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        
        try {
            // Load the XML file
            xmlAccessor.loadFile(presentation, filePath);
            
            // Check that an error about unknown type was printed
            assertTrue(errContent.toString().contains("Unknown Element type"), 
                       "Should show unknown element type message");
            
            // Check presentation content (slide should be created but item ignored)
            assertEquals("Unknown Kind Test", presentation.getTitle(), "Presentation title should be set");
            assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
            
            // The unknown item should be ignored
            Slide slide = presentation.getSlide(0);
            assertEquals(0, slide.getSize(), "Slide should have 0 items");
        } finally {
            System.setErr(originalErr);
        }
    }
    
    @Test
    @DisplayName("loadFile should handle file not found gracefully")
    void loadFileShouldHandleFileNotFoundGracefully() {
        // Use a non-existent file path
        String nonExistentFile = tempDir.resolve("non_existent.xml").toString();
        
        // Load should not throw an exception
        assertDoesNotThrow(() -> xmlAccessor.loadFile(presentation, nonExistentFile));
        
        // Presentation should remain unchanged
        assertNull(presentation.getTitle(), "Presentation title should remain null");
        assertEquals(0, presentation.getSize(), "Presentation should have 0 slides");
    }
    
    @Test
    @DisplayName("saveFile should write presentation to XML file")
    void saveFileShouldWritePresentationToXmlFile() throws IOException {
        // Create a presentation to save
        Presentation presentation = new Presentation();
        presentation.setTitle("Saved Presentation");
        
        // Add a slide with a text item
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(1, "Test Text Item");
        presentation.append(slide);
        
        // Save to a temporary file
        String outputFile = tempDir.resolve("test_save.xml").toString();
        
        // Save the presentation
        xmlAccessor.saveFile(presentation, outputFile);
        
        // Verify the file exists
        File savedFile = new File(outputFile);
        assertTrue(savedFile.exists(), "File should be created");
        
        // Verify content was saved
        String fileContent = Files.readString(savedFile.toPath());
        assertTrue(fileContent.contains("<presentation>"), "XML should have presentation tag");
        assertTrue(fileContent.contains("<showtitle>Saved Presentation</showtitle>"), "XML should contain presentation title");
        assertTrue(fileContent.contains("<slide>"), "XML should have slide tag");
        assertTrue(fileContent.contains("<title>Test Slide</title>"), "XML should contain slide title");
        assertTrue(fileContent.contains("<item kind=\"text\" level=\"1\">Test Text Item</item>"), "XML should contain text item");
        
        // Now try to load the file back to verify it's valid XML
        Presentation loadedPresentation = new Presentation();
        
        // Create a new instance of XMLAccessor for loading
        XMLAccessor loadingAccessor = new XMLAccessor();
        loadingAccessor.loadFile(loadedPresentation, outputFile);
        
        // Verify the loaded presentation has the same title and content
        assertEquals("Saved Presentation", loadedPresentation.getTitle(), "Loaded presentation title should match");
        assertEquals(1, loadedPresentation.getSize(), "Loaded presentation should have 1 slide");
        
        // Verify slide content
        Slide loadedSlide = loadedPresentation.getSlide(0);
        assertEquals("Test Slide", loadedSlide.getTitle(), "Loaded slide title should match");
        assertEquals(1, loadedSlide.getSize(), "Loaded slide should have 1 item");
        
        // Verify text item content
        SlideItem loadedItem = loadedSlide.getSlideItem(0);
        assertTrue(loadedItem instanceof TextItem, "Loaded item should be a TextItem");
        assertEquals("Test Text Item", ((TextItem)loadedItem).getText(), "Loaded text item content should match");
        assertEquals(1, loadedItem.getLevel(), "Loaded text item level should match");
    }
    
    @Test
    @DisplayName("saveFile should handle unsupported slide item types")
    void saveFileShouldHandleUnsupportedSlideItemTypes() throws IOException {
        // Create a test XML file with all the necessary elements
        String xmlContent = 
            "<?xml version=\"1.0\"?>\n" +
            "<presentation>\n" +
            "  <showtitle>Unsupported Items Test</showtitle>\n" +
            "  <slide>\n" +
            "    <title>Test Slide</title>\n" +
            "    <item kind=\"text\" level=\"1\">Normal Text Item</item>\n" +
            "  </slide>\n" +
            "</presentation>";
        
        // Write this to a file first
        String inputFilePath = tempDir.resolve("unsupported_input.xml").toString();
        Files.writeString(Path.of(inputFilePath), xmlContent);
        
        // Load the presentation from the file
        Presentation presentation = new Presentation();
        xmlAccessor.loadFile(presentation, inputFilePath);
        
        // Verify it loaded correctly
        assertEquals("Unsupported Items Test", presentation.getTitle(), "Presentation title should be set from XML");
        
        // Now add an unsupported item to the loaded presentation
        Slide slide = presentation.getSlide(0);
        SlideItem unsupportedItem = new SlideItem(2) {
            @Override
            public Rectangle getBoundingBox(Graphics g, ImageObserver o, float scale, Style s) {
                return new Rectangle();
            }
            
            @Override
            public void draw(int x, int y, float scale, Graphics g, Style s, ImageObserver o) {
                // Do nothing
            }
            
            @Override
            public String toString() {
                return "UnsupportedItem";
            }
        };
        slide.append(unsupportedItem);
        
        // Save to a new file
        String outputFile = tempDir.resolve("unsupported_output.xml").toString();
        
        // Capture standard error output
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        
        try {
            // Save the presentation
            xmlAccessor.saveFile(presentation, outputFile);
            
            // Verify error about unknown type was logged
            String errorOutput = errContent.toString().toLowerCase();
            assertTrue(errorOutput.contains("unknown") || errorOutput.contains("unsupported"), 
                       "Should show error about unknown item");
            
            // Now load back the saved file
            Presentation loadedPresentation = new Presentation();
            xmlAccessor.loadFile(loadedPresentation, outputFile);
            
            // Verify the loaded presentation has the correct title
            assertEquals("Unsupported Items Test", loadedPresentation.getTitle(),
                         "Loaded presentation title should match");
            
            // Verify the unsupported item was skipped
            assertEquals(1, loadedPresentation.getSlide(0).getSize(),
                         "Loaded slide should have only the text item");
        } finally {
            System.setErr(originalErr);
        }
    }
}
