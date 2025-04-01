package org.jabberpoint.test;

import org.jabberpoint.src.XMLPresentationWriter;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.Slide;
import org.jabberpoint.src.TextItem;
import org.jabberpoint.src.BitmapItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for XMLPresentationWriter class
 */
class XMLPresentationWriterTest {

    private XMLPresentationWriter writer;
    
    @Mock
    private Presentation mockPresentation;
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should write presentation to XML file")
    void shouldWritePresentationToXMLFile() throws Exception {
        // Arrange
        File outputFile = tempDir.resolve("test_output.xml").toFile();
        writer = new XMLPresentationWriter(outputFile.getAbsolutePath());
        
        // Create a mock presentation structure
        when(mockPresentation.getTitle()).thenReturn("Test Presentation");
        when(mockPresentation.getSize()).thenReturn(2);
        
        // Create mock slides
        List<Slide> slides = new ArrayList<>();
        
        // Slide 1
        Slide slide1 = new Slide();
        slide1.setTitle("First Slide");
        slide1.append(new TextItem(1, "Text Item 1"));
        slide1.append(new BitmapItem(2, "image1.jpg"));
        slides.add(slide1);
        
        // Slide 2
        Slide slide2 = new Slide();
        slide2.setTitle("Second Slide");
        slide2.append(new TextItem(1, "Text Item 2"));
        slides.add(slide2);
        
        // Setup mock to return the slides
        when(mockPresentation.getSlide(0)).thenReturn(slide1);
        when(mockPresentation.getSlide(1)).thenReturn(slide2);
        
        // Act
        writer.savePresentation(mockPresentation);
        
        // Assert
        assertTrue(outputFile.exists(), "Output file should exist");
        String content = Files.readString(outputFile.toPath());
        
        // Verify XML structure
        assertTrue(content.contains("<presentation>"), "Should contain presentation tag");
        assertTrue(content.contains("<slide>"), "Should contain slide tags");
        assertTrue(content.contains("<title>First Slide</title>"), "Should contain first slide title");
        assertTrue(content.contains("<title>Second Slide</title>"), "Should contain second slide title");
        assertTrue(content.contains("kind=\"text\""), "Should contain text items");
        assertTrue(content.contains("kind=\"image\""), "Should contain image items");
        assertTrue(content.contains("level=\"1\""), "Should contain level attribute");
        assertTrue(content.contains("level=\"2\""), "Should contain level attribute");
        assertTrue(content.contains("Text Item 1"), "Should contain text content");
        assertTrue(content.contains("image1.jpg"), "Should contain image filename");
    }
    
    @Test
    @DisplayName("Should handle exceptions when writing file")
    void shouldHandleExceptionsWhenWritingFile() {
        // Arrange - Invalid file path should cause an exception during write
        writer = new XMLPresentationWriter("/invalid/path/that/does/not/exist/test.xml");
        
        // Act & Assert - Should throw exception
        assertThrows(RuntimeException.class, 
            () -> writer.savePresentation(mockPresentation),
            "Should throw RuntimeException for invalid file path");
    }
    
    @Test
    @DisplayName("Should handle empty presentation")
    void shouldHandleEmptyPresentation() throws Exception {
        // Arrange
        File outputFile = tempDir.resolve("empty_presentation.xml").toFile();
        writer = new XMLPresentationWriter(outputFile.getAbsolutePath());
        
        // Empty presentation
        when(mockPresentation.getTitle()).thenReturn("");
        when(mockPresentation.getSize()).thenReturn(0);
        
        // Act
        writer.savePresentation(mockPresentation);
        
        // Assert
        assertTrue(outputFile.exists(), "Output file should exist even for empty presentation");
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("<presentation>"), "Should contain presentation tag");
        assertFalse(content.contains("<slide>"), "Should not contain slide tags");
    }
    
    @Test
    @DisplayName("Should handle slide with no items")
    void shouldHandleSlideWithNoItems() throws Exception {
        // Arrange
        File outputFile = tempDir.resolve("empty_slide.xml").toFile();
        writer = new XMLPresentationWriter(outputFile.getAbsolutePath());
        
        // Create presentation with one empty slide
        when(mockPresentation.getTitle()).thenReturn("Test Presentation");
        when(mockPresentation.getSize()).thenReturn(1);
        
        // Empty slide
        Slide emptySlide = new Slide();
        emptySlide.setTitle("Empty Slide");
        when(mockPresentation.getSlide(0)).thenReturn(emptySlide);
        
        // Act
        writer.savePresentation(mockPresentation);
        
        // Assert
        assertTrue(outputFile.exists(), "Output file should exist");
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("<slide>"), "Should contain slide tag");
        assertTrue(content.contains("<title>Empty Slide</title>"), "Should contain slide title");
        assertFalse(content.contains("<item"), "Should not contain any item tags");
    }
}
