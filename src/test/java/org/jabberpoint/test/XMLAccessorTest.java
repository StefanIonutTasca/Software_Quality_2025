package org.jabberpoint.test;

import org.jabberpoint.src.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XMLAccessorTest {

    private XMLAccessor xmlAccessor;
    private Presentation presentation;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        xmlAccessor = new XMLAccessor();
        presentation = new Presentation();
        Style.createStyles(); // Initialize styles for tests
    }

    @Test
    @DisplayName("loadFile should parse XML and populate presentation")
    void loadFileShouldParseXmlAndPopulatePresentation() throws IOException {
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
        xmlAccessor.loadFile(presentation, xmlFile.toString());
        
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
    @DisplayName("loadFile should handle invalid level attribute gracefully")
    void loadFileShouldHandleInvalidLevelAttributeGracefully() throws IOException {
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
        
        // Load the XML file
        xmlAccessor.loadFile(presentation, xmlFile.toString());
        
        // Verify presentation was populated and used default level (1)
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set");
        assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
        
        Slide slide = presentation.getSlide(0);
        SlideItem item = slide.getSlideItem(0);
        assertEquals(1, item.getLevel(), "Item should have default level 1 when level is invalid");
    }
    
    @Test
    @DisplayName("loadFile should handle unknown item kind gracefully")
    void loadFileShouldHandleUnknownItemKindGracefully() throws IOException {
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
        
        // Load the XML file - should not throw exception
        xmlAccessor.loadFile(presentation, xmlFile.toString());
        
        // Verify presentation was populated but the unknown item was not added
        assertEquals("Test Presentation", presentation.getTitle(), "Presentation title should be set");
        assertEquals(1, presentation.getSize(), "Presentation should have 1 slide");
        
        Slide slide = presentation.getSlide(0);
        assertEquals(0, slide.getSize(), "Slide should have 0 items as the unknown kind was not added");
    }
    
    @Test
    @DisplayName("loadFile should handle file not found gracefully")
    void loadFileShouldHandleFileNotFoundGracefully() throws IOException {
        // Try to load a non-existent file
        String nonExistentFile = tempDir.resolve("non_existent_file.xml").toString();
        
        // Should not throw exception
        assertDoesNotThrow(() -> xmlAccessor.loadFile(presentation, nonExistentFile));
        
        // Presentation should remain unchanged
        assertNull(presentation.getTitle(), "Presentation title should remain null");
        assertEquals(0, presentation.getSize(), "Presentation should have 0 slides");
    }
    
    @Test
    @DisplayName("saveFile should write presentation to XML file")
    void saveFileShouldWritePresentationToXmlFile() throws IOException {
        // Create a presentation with slides and items
        Presentation savePresentation = new Presentation();
        savePresentation.setTitle("Saved Presentation");
        
        // Add two slides
        Slide slide1 = new Slide();
        slide1.setTitle("Saved Slide 1");
        TextItem textItem = new TextItem(1, "Saved text item");
        BitmapItem imageItem = new BitmapItem(2, "saved_image.png");
        slide1.append(textItem);
        slide1.append(imageItem);
        savePresentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Saved Slide 2");
        savePresentation.append(slide2);
        
        // Save the presentation to a file
        String outputFile = tempDir.resolve("test_output.xml").toString();
        xmlAccessor.saveFile(savePresentation, outputFile);
        
        // Verify the file was created
        File savedFile = new File(outputFile);
        assertTrue(savedFile.exists(), "Output file should exist");
        
        // Read the file content
        String savedContent = Files.readString(savedFile.toPath());
        
        // Verify the XML structure
        assertTrue(savedContent.contains("<presentation>"), "Root element should be present");
        assertTrue(savedContent.contains("<showtitle>Saved Presentation</showtitle>"), "Show title should be present");
        assertTrue(savedContent.contains("<slide>"), "Slide element should be present");
        assertTrue(savedContent.contains("<title>Saved Slide 1</title>"), "Slide 1 title should be set");
        assertTrue(savedContent.contains("<item kind=\"text\" level=\"1\">Saved text item</item>"), 
                "Text item should be present with correct attributes");
        assertTrue(savedContent.contains("<item kind=\"image\" level=\"2\">saved_image.png</item>"), 
                "Image item should be present with correct attributes");
        assertTrue(savedContent.contains("<title>Saved Slide 2</title>"), "Slide 2 title should be present");
        
        // Bonus: Load the saved file to verify it can be parsed back
        Presentation loadedPresentation = new Presentation();
        XMLAccessor loadAccessor = new XMLAccessor(); // Create a new instance to ensure clean state
        loadAccessor.loadFile(loadedPresentation, outputFile);
        
        assertEquals("Saved Presentation", loadedPresentation.getTitle(), "Loaded presentation title should match");
        assertEquals(2, loadedPresentation.getSize(), "Loaded presentation should have 2 slides");
    }
    
    @Test
    @DisplayName("saveFile should handle unsupported SlideItem types")
    void saveFileShouldHandleUnsupportedSlideItemTypes() throws IOException {
        // Create a mock unsupported SlideItem type
        SlideItem mockItem = mock(SlideItem.class);
        when(mockItem.getLevel()).thenReturn(1);
        
        // Create a slide with the mock item
        Slide slide = new Slide();
        slide.setTitle("Mock Slide");
        
        // Add the mock item to the slide's vector using reflection
        Vector<SlideItem> items = slide.getSlideItems();
        items.add(mockItem);
        
        // Add the slide to the presentation
        presentation.setTitle("Mock Presentation");
        presentation.append(slide);
        
        // Save the presentation to a file
        String outputFile = tempDir.resolve("mock_presentation.xml").toString();
        
        // Should not throw exception
        assertDoesNotThrow(() -> xmlAccessor.saveFile(presentation, outputFile));
        
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
        assertFalse(savedContent.contains("<item kind=\"text\" level=\"1\">"), "Mock item should not be saved as text");
        assertFalse(savedContent.contains("<item kind=\"image\" level=\"1\">"), "Mock item should not be saved as image");
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
        
        // Create a helper method to invoke the private getTitle method
        // We'll do this by loading the file and checking the presentation title
        xmlAccessor.loadFile(presentation, xmlFile.toString());
        
        assertEquals("Test Title Extraction", presentation.getTitle(), 
                "getTitle method should extract title correctly from XML element");
    }
}
