package org.jabberpoint.test;

import org.jabberpoint.src.SaveFileCommand;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.XMLPresentationLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SaveFileCommand class
 */
@ExtendWith(MockitoExtension.class)
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
            
        // Create a temp file
        File tempFile = tempDir.resolve("save.xml").toFile();
        
        // Mock JFileChooser
        try (MockedConstruction<JFileChooser> mockConstruction = mockConstruction(JFileChooser.class, 
                (mock, context) -> {
                    when(mock.showSaveDialog(any(Frame.class))).thenReturn(JFileChooser.APPROVE_OPTION);
                    when(mock.getSelectedFile()).thenReturn(tempFile);
                });
             MockedConstruction<XMLPresentationLoader> mockedLoader = mockConstruction(XMLPresentationLoader.class,
                (mock, context) -> {
                    // No need to do anything with this mock
                })) {
            
            // Act
            saveFileCommand.execute();
            
            // Assert
            assertEquals(1, mockedLoader.constructed().size(), "XMLPresentationLoader should be constructed");
            
            // Verify savePresentation was called once on the XMLPresentationLoader instance
            verify(mockedLoader.constructed().get(0), times(1))
                .savePresentation(eq(mockPresentation), eq(tempFile.getPath()));
        }
    }
    
    @Test
    @DisplayName("Should handle when user cancels file chooser")
    void shouldHandleWhenUserCancelsFileChooser() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Mock JFileChooser with CANCEL_OPTION
        try (MockedConstruction<JFileChooser> mockConstruction = mockConstruction(JFileChooser.class, 
                (mock, context) -> {
                    when(mock.showSaveDialog(any(Frame.class))).thenReturn(JFileChooser.CANCEL_OPTION);
                });
             MockedConstruction<XMLPresentationLoader> mockedLoader = mockConstruction(XMLPresentationLoader.class)) {
            
            // Act
            saveFileCommand.execute();
            
            // Assert
            assertTrue(mockedLoader.constructed().isEmpty(), "XMLPresentationLoader should not be constructed");
        }
    }
    
    @Test
    @DisplayName("Should handle IO exceptions gracefully")
    void shouldHandleIOExceptionsGracefully() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Setup temp file
        File tempFile = tempDir.resolve("error.xml").toFile();
        
        // Mock JFileChooser
        try (MockedConstruction<JFileChooser> mockConstruction = mockConstruction(JFileChooser.class, 
                (mock, context) -> {
                    when(mock.showSaveDialog(any(Frame.class))).thenReturn(JFileChooser.APPROVE_OPTION);
                    when(mock.getSelectedFile()).thenReturn(tempFile);
                });
             MockedConstruction<XMLPresentationLoader> mockedLoader = mockConstruction(XMLPresentationLoader.class,
                (mock, context) -> {
                    doThrow(new IOException("Test exception"))
                        .when(mock).savePresentation(any(Presentation.class), anyString());
                });
             MockedStatic<JOptionPane> mockedOptionPane = mockStatic(JOptionPane.class)) {
            
            // Mock JOptionPane to avoid showing dialog
            mockedOptionPane.when(() -> JOptionPane.showMessageDialog(
                any(), anyString(), anyString(), anyInt())).thenAnswer(invocation -> null);
            
            // Act
            saveFileCommand.execute();
            
            // Assert error dialog was shown
            mockedOptionPane.verify(() -> JOptionPane.showMessageDialog(
                eq(mockFrame), contains("IO Exception"), eq("Jabberpoint Error"), eq(JOptionPane.ERROR_MESSAGE)));
        }
    }
}
