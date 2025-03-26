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

/**
 * Tests for XMLAccessor class
 */
public class XMLAccessorTest {

    private XMLAccessor xmlAccessor;
    private Presentation presentation;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        xmlAccessor = new XMLAccessor();
        presentation = new Presentation();
    }

    @Test
    @DisplayName("Should load presentation from XML file")
    void shouldLoadPresentationFromXmlFile() throws IOException {
        // Create a test XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"text\" level=\"1\">Text item 1</item>\n" +
                "<item kind=\"text\" level=\"2\">Text item 2</item>\n" +
                "</slide>\n" +
                "<slide>\n" +
                "<title>Slide 2</title>\n" +
                "<item kind=\"text\" level=\"1\">Text item 3</item>\n" +
                "<item kind=\"image\" level=\"2\">test-image.jpg</item>\n" +
                "</slide>\n" +
                "</presentation>";

        File xmlFile = tempDir.resolve("test-presentation.xml").toFile();
        Files.writeString(xmlFile.toPath(), xmlContent);

        // Act
        xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath());

        // Assert
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals(2, presentation.getSize());
        
        // Check slide 1
        Slide slide1 = presentation.getSlide(0);
        assertEquals("Slide 1", slide1.getTitle());
        Vector<SlideItem> items1 = slide1.getSlideItems();
        assertEquals(2, items1.size());
        
        SlideItem item1 = items1.elementAt(0);
        assertTrue(item1 instanceof TextItem);
        assertEquals(1, item1.getLevel());
        assertEquals("Text item 1", ((TextItem)item1).getText());
        
        // Check slide 2
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Slide 2", slide2.getTitle());
        Vector<SlideItem> items2 = slide2.getSlideItems();
        assertEquals(2, items2.size());
        
        SlideItem item3 = items2.elementAt(0);
        assertTrue(item3 instanceof TextItem);
        assertEquals(1, item3.getLevel());
        assertEquals("Text item 3", ((TextItem)item3).getText());
        
        SlideItem item4 = items2.elementAt(1);
        assertTrue(item4 instanceof BitmapItem);
        assertEquals(2, item4.getLevel());
        assertEquals("test-image.jpg", ((BitmapItem)item4).getName());
    }

    @Test
    @DisplayName("Should save presentation to XML file")
    void shouldSavePresentationToXmlFile() throws IOException {
        // Prepare a presentation with slides and items
        presentation.setTitle("Test Presentation");
        
        Slide slide1 = new Slide();
        slide1.setTitle("Slide 1");
        slide1.append(new TextItem(1, "Text item 1"));
        slide1.append(new TextItem(2, "Text item 2"));
        presentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Slide 2");
        slide2.append(new TextItem(1, "Text item 3"));
        slide2.append(new BitmapItem(2, "test-image.jpg"));
        presentation.append(slide2);
        
        // Act
        File xmlFile = tempDir.resolve("output-presentation.xml").toFile();
        xmlAccessor.saveFile(presentation, xmlFile.getAbsolutePath());
        
        // Assert
        assertTrue(xmlFile.exists());
        String content = Files.readString(xmlFile.toPath());
        
        // Check basic structure
        assertTrue(content.contains("<showtitle>Test Presentation</showtitle>"));
        assertTrue(content.contains("<title>Slide 1</title>"));
        assertTrue(content.contains("<title>Slide 2</title>"));
        assertTrue(content.contains("<item kind=\"text\" level=\"1\">Text item 1</item>"));
        assertTrue(content.contains("<item kind=\"text\" level=\"2\">Text item 2</item>"));
        assertTrue(content.contains("<item kind=\"text\" level=\"1\">Text item 3</item>"));
        assertTrue(content.contains("<item kind=\"image\" level=\"2\">test-image.jpg</item>"));
    }
    
    @Test
    @DisplayName("Should handle malformed XML file")
    void shouldHandleMalformedXmlFile() throws IOException {
        // Create a malformed XML file
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "<showtitle>Malformed Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Broken Slide</title>\n" +
                "<item kind=\"text\" level=\"invalid\">This has an invalid level</item>\n" +
                "</slide>\n" +
                "</presentation>";

        File xmlFile = tempDir.resolve("malformed.xml").toFile();
        Files.writeString(xmlFile.toPath(), xmlContent);

        // Act - should not throw exception
        assertDoesNotThrow(() -> xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath()));
        
        // Verify title was still loaded
        assertEquals("Malformed Presentation", presentation.getTitle());
    }
    
    @Test
    @DisplayName("Should handle unknown item type")
    void shouldHandleUnknownItemType() throws IOException {
        // Create XML with unknown item type
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"unknown\" level=\"1\">Unknown item</item>\n" +
                "</slide>\n" +
                "</presentation>";

        File xmlFile = tempDir.resolve("unknown-type.xml").toFile();
        Files.writeString(xmlFile.toPath(), xmlContent);

        // Act - should not throw exception
        assertDoesNotThrow(() -> xmlAccessor.loadFile(presentation, xmlFile.getAbsolutePath()));
        
        // Verify slide was created but empty (unknown item type ignored)
        assertEquals(1, presentation.getSize());
        Slide slide = presentation.getSlide(0);
        assertEquals("Test Slide", slide.getTitle());
        assertEquals(0, slide.getSlideItems().size());
    }
}
