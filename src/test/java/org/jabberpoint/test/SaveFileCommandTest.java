package org.jabberpoint.test;

import org.jabberpoint.src.SaveFileCommand;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.XMLPresentationWriter;
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
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SaveFileCommand class
 */
class SaveFileCommandTest {

    private SaveFileCommand saveFileCommand;
    
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
        saveFileCommand = new SaveFileCommand(mockFrame, mockPresentation);
    }

    @Test
    @DisplayName("Should execute file save dialog when executed")
    void shouldExecuteFileSaveDialogWhenExecuted() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Arrange - Prepare mock file dialog
        Field fileDialogField = SaveFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(saveFileCommand, mockFileDialog);
        
        // Setup mock behavior
        when(mockFileDialog.getDirectory()).thenReturn(tempDir.toString() + File.separator);
        when(mockFileDialog.getFile()).thenReturn("test.xml");
        
        // Act
        saveFileCommand.execute();
        
        // Assert
        verify(mockFileDialog, times(1)).setVisible(true);
        verify(mockPresentation, times(1)).savePresentation(any(XMLPresentationWriter.class));
    }
    
    @Test
    @DisplayName("Should handle case when no file is selected")
    void shouldHandleCaseWhenNoFileIsSelected() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Arrange - Setup mock file dialog to return null
        Field fileDialogField = SaveFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(saveFileCommand, mockFileDialog);
        
        when(mockFileDialog.getFile()).thenReturn(null);
        
        // Act
        saveFileCommand.execute();
        
        // Assert - Verify dialog was shown
        verify(mockFileDialog, times(1)).setVisible(true);
        
        // Verify savePresentation was NOT called
        verify(mockPresentation, never()).savePresentation(any(XMLPresentationWriter.class));
    }
    
    @Test
    @DisplayName("Should handle case when file dialog is null (headless environment)")
    void shouldHandleCaseWhenFileDialogIsNull() throws Exception {
        // This test simulates the headless environment case
        
        // Set fileDialog to null using reflection
        Field fileDialogField = SaveFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(saveFileCommand, null);
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> saveFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should handle exceptions during file saving")
    void shouldHandleExceptionsDuringFileSaving() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Arrange - Setup mock file dialog
        Field fileDialogField = SaveFileCommand.class.getDeclaredField("fileDialog");
        fileDialogField.setAccessible(true);
        fileDialogField.set(saveFileCommand, mockFileDialog);
        
        when(mockFileDialog.getDirectory()).thenReturn(tempDir.toString() + File.separator);
        when(mockFileDialog.getFile()).thenReturn("test.xml");
        
        // Make presentation throw exception during savePresentation
        doThrow(new RuntimeException("Save failed")).when(mockPresentation).savePresentation(any(XMLPresentationWriter.class));
        
        // Act & Assert - Should not throw exception outside of execute
        assertDoesNotThrow(() -> saveFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should get correct accessor name")
    void shouldGetCorrectAccessorName() {
        // Act
        String accessorName = saveFileCommand.getAccessorName();
        
        // Assert
        assertEquals("Save", accessorName);
    }
}
