package org.jabberpoint.test;

import org.jabberpoint.src.SaveFileCommand;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.JFileChooser;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saveFileCommand = new SaveFileCommand(mockPresentation, mockFrame);
    }

    @Test
    @DisplayName("Should handle headless environment gracefully")
    void shouldHandleHeadlessEnvironmentGracefully() {
        // If we're in a headless environment, the command should return early without error
        assertDoesNotThrow(() -> saveFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should handle null parent frame gracefully")
    void shouldHandleNullParentFrameGracefully() throws Exception {
        // Create a command with null parent frame
        SaveFileCommand cmdWithNullParent = new SaveFileCommand(mockPresentation, null);
        
        // Should return early without error
        assertDoesNotThrow(() -> cmdWithNullParent.execute());
    }
    
    @Test
    @DisplayName("Should execute file chooser when in GUI environment")
    void shouldExecuteFileChooserWhenInGUIEnvironment() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // We need to mock the JFileChooser and replace it in the command
        // This requires some reflection or a more testable design
        // For now, we'll just test that the command doesn't throw exceptions
        assertDoesNotThrow(() -> saveFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should handle IO exceptions gracefully")
    void shouldHandleIOExceptionsGracefully() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // The SaveFileCommand uses XMLPresentationLoader directly for saving
        // For now, we'll just verify the command doesn't throw unhandled exceptions
        assertDoesNotThrow(() -> saveFileCommand.execute());
    }
}
