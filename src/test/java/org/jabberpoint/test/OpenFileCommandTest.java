package org.jabberpoint.test;

import org.jabberpoint.src.OpenFileCommand;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.XMLPresentationLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OpenFileCommand class
 */
class OpenFileCommandTest {

    private OpenFileCommand openFileCommand;
    
    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;
    
    @Mock
    private FileDialog mockFileDialog;
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        openFileCommand = new OpenFileCommand(mockFrame, mockPresentation);
    }

    @Test
    @DisplayName("Should execute file open dialog when executed")
    void shouldExecuteFileOpenDialogWhenExecuted() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Arrange - Setup a test XML file
        File xmlFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("<presentation>\n" +
                         "  <slide>\n" +
                         "    <title>Test Slide</title>\n" +
                         "    <item kind=\"text\" level=\"1\">Test Item</item>\n" +
                         "  </slide>\n" +
                         "</presentation>");
        }
        
        // Modify the FileDialog behavior using reflection
        Field fileDialogField = OpenFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(openFileCommand, mockFileDialog);
        
        // Setup mock behavior
        when(mockFileDialog.getDirectory()).thenReturn(tempDir.toString() + File.separator);
        when(mockFileDialog.getFile()).thenReturn("test.xml");
        
        // Act
        openFileCommand.execute();
        
        // Assert - Verify file dialog was shown
        verify(mockFileDialog, times(1)).setVisible(true);
        
        // Verify presentation was loaded
        verify(mockPresentation, times(1)).loadPresentation(any(XMLPresentationLoader.class));
        verify(mockFrame, times(1)).repaint();
    }
    
    @Test
    @DisplayName("Should handle case when no file is selected")
    void shouldHandleCaseWhenNoFileIsSelected() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Arrange - Setup mock file dialog to return null
        Field fileDialogField = OpenFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(openFileCommand, mockFileDialog);
        
        when(mockFileDialog.getFile()).thenReturn(null);
        
        // Act
        openFileCommand.execute();
        
        // Assert - Verify dialog was shown
        verify(mockFileDialog, times(1)).setVisible(true);
        
        // Verify loadPresentation was NOT called
        verify(mockPresentation, never()).loadPresentation(any(XMLPresentationLoader.class));
    }
    
    @Test
    @DisplayName("Should handle case when file dialog is null (headless environment)")
    void shouldHandleCaseWhenFileDialogIsNull() throws Exception {
        // This test simulates the headless environment case where fileDialog is null
        
        // Set fileDialog to null using reflection
        Field fileDialogField = OpenFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(openFileCommand, null);
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> openFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should handle file not found exception when loading file")
    void shouldHandleFileNotFoundExceptionWhenLoadingFile() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Arrange - Setup a non-existent file
        Field fileDialogField = OpenFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(openFileCommand, mockFileDialog);
        
        // Setup mock behavior for non-existent file
        when(mockFileDialog.getDirectory()).thenReturn(tempDir.toString() + File.separator);
        when(mockFileDialog.getFile()).thenReturn("non_existent.xml");
        
        // Make the presentation throw exception when loadPresentation is called
        doThrow(new RuntimeException("File not found")).when(mockPresentation).loadPresentation(any(XMLPresentationLoader.class));
        
        // Act & Assert - Should not throw exception outside of execute
        assertDoesNotThrow(() -> openFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should get correct accessor name")
    void shouldGetCorrectAccessorName() {
        // Act
        String accessorName = openFileCommand.getAccessorName();
        
        // Assert
        assertEquals("Open", accessorName);
    }
}
