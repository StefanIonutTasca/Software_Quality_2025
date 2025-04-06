package org.jabberpoint.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jabberpoint.src.command.NextSlideCommand;
import org.jabberpoint.src.command.PrevSlideCommand;
import org.jabberpoint.src.io.XMLAccessor;
import org.jabberpoint.src.model.BitmapItem;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;
import org.jabberpoint.src.model.TextItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration test suite for Jabberpoint. This class contains integration tests that verify the
 * interaction between different components of the Jabberpoint application.
 */
@Tag("integration")
public class IntegrationTestSuite {

  @TempDir
  Path tempDir;
  
  private Presentation presentation;
  
  @BeforeEach
  public void setUp() {
    // Create a real presentation with test data
    presentation = new Presentation();
    presentation.setTitle("Integration Test Presentation");
    
    // Create slides with content
    Slide slide1 = new Slide();
    slide1.setTitle("First Slide");
    slide1.append(new TextItem(1, "This is the first slide"));
    
    Slide slide2 = new Slide();
    slide2.setTitle("Second Slide");
    slide2.append(new TextItem(1, "This is the second slide"));
    
    // Add slides to presentation
    presentation.append(slide1);
    presentation.append(slide2);
  }
  
  @AfterEach
  public void tearDown() {
    presentation = null;
  }

  /**
   * Integration test that verifies the navigation through slides works correctly,
   * testing the interaction between the Presentation model and slide navigation.
   */
  @Test
  public void testPresentationNavigation() {
    // Initial state
    assertEquals(0, presentation.getSlideNumber());
    
    // Navigate to next slide
    presentation.nextSlide();
    assertEquals(1, presentation.getSlideNumber());
    
    // Navigate to next slide (should stay at last slide)
    presentation.nextSlide();
    assertEquals(1, presentation.getSlideNumber());
    
    // Navigate to previous slide
    presentation.prevSlide();
    assertEquals(0, presentation.getSlideNumber());
    
    // Navigate to previous slide (should stay at first slide)
    presentation.prevSlide();
    assertEquals(0, presentation.getSlideNumber());
    
    // Test getting current slide
    Slide currentSlide = presentation.getCurrentSlide();
    assertEquals("First Slide", currentSlide.getTitle());
  }

  /**
   * Integration test that verifies the Command pattern works correctly with the Presentation model,
   * testing that commands properly update the presentation state.
   */
  @Test
  public void testCommandIntegration() {
    // Set up commands with the presentation
    NextSlideCommand nextCommand = new NextSlideCommand(presentation);
    PrevSlideCommand prevCommand = new PrevSlideCommand(presentation);
    
    // Initial state
    assertEquals(0, presentation.getSlideNumber());
    
    // Execute next slide command
    nextCommand.execute();
    assertEquals(1, presentation.getSlideNumber());
    
    // Execute previous slide command
    prevCommand.execute();
    assertEquals(0, presentation.getSlideNumber());
  }

  /**
   * Integration test that verifies XML file loading and saving works correctly,
   * testing the interaction between XMLAccessor and the Presentation model.
   */
  @Test
  public void testXMLFileIntegration() throws IOException {
    // Create a temporary XML file
    File tempFile = tempDir.resolve("test-presentation.xml").toFile();
    
    // Create XML content
    String xmlContent = 
        "<?xml version=\"1.0\"?>\n" +
        "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
        "<presentation>\n" +
        "<showtitle>XML Test Presentation</showtitle>\n" +
        "<slide>\n" +
        "<title>XML Test Slide</title>\n" +
        "<item kind=\"text\" level=\"1\">XML Test Item</item>\n" +
        "</slide>\n" +
        "</presentation>";
    
    // Write XML content to file
    Files.writeString(tempFile.toPath(), xmlContent);
    
    // Create a new presentation for loading
    Presentation loadedPresentation = new Presentation();
    
    // Use XMLAccessor to load the file
    XMLAccessor accessor = new XMLAccessor();
    accessor.loadFile(loadedPresentation, tempFile.getAbsolutePath());
    
    // Verify the loaded presentation
    assertEquals("XML Test Presentation", loadedPresentation.getTitle());
    assertEquals(1, loadedPresentation.getSize());
    
    Slide loadedSlide = loadedPresentation.getSlide(0);
    assertEquals("XML Test Slide", loadedSlide.getTitle());
    
    // Now test saving
    File savedFile = tempDir.resolve("saved-presentation.xml").toFile();
    accessor.saveFile(presentation, savedFile.getAbsolutePath());
    
    // Verify the file was created
    assertTrue(savedFile.exists());
    
    // Load the saved file into a new presentation to verify it was saved correctly
    Presentation reloadedPresentation = new Presentation();
    accessor.loadFile(reloadedPresentation, savedFile.getAbsolutePath());
    
    // Verify the reloaded presentation matches the original
    assertEquals(presentation.getTitle(), reloadedPresentation.getTitle());
    assertEquals(presentation.getSize(), reloadedPresentation.getSize());
  }
}
