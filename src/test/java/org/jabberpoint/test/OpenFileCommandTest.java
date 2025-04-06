package org.jabberpoint.test;
import org.jabberpoint.src.command.OpenFileCommand;
import org.jabberpoint.src.app.JabberPoint;
import org.jabberpoint.src.command.Command;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.io.PresentationLoader;
import org.jabberpoint.src.io.PresentationLoaderFactory;
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
import java.io.FileWriter;
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
 * Unit tests for OpenFileCommand class
 */
@ExtendWith(MockitoExtension.class)
class OpenFileCommandTest {

    private OpenFileCommand openFileCommand;
    
    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;
    
    @Mock
    private PresentationLoader mockLoader;
    
    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        openFileCommand = new OpenFileCommand(mockPresentation, mockFrame);
    }

    @Test
    @DisplayName("Should handle headless environment gracefully")
    void shouldHandleHeadlessEnvironmentGracefully() {
        // If we're in a headless environment, the command should return early without error
        assertDoesNotThrow(() -> openFileCommand.execute());
    }
    
    @Test
    @DisplayName("Should handle null parent frame gracefully")
    void shouldHandleNullParentFrameGracefully() throws Exception {
        // Create a command with null parent frame
        OpenFileCommand cmdWithNullParent = new OpenFileCommand(mockPresentation, null);
        
        // Should return early without error
        assertDoesNotThrow(() -> cmdWithNullParent.execute());
    }
    
    @Test
    @DisplayName("Should execute file chooser when in GUI environment")
    void shouldExecuteFileChooserWhenInGUIEnvironment() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Create a test file
        File xmlFile = tempDir.resolve("test.xml").toFile();
        try (FileWriter writer = new FileWriter(xmlFile)) {
            writer.write("<presentation>\n" +
                         "  <slide>\n" +
                         "    <title>Test Slide</title>\n" +
                         "    <item kind=\"text\" level=\"1\">Test Item</item>\n" +
                         "  </slide>\n" +
                         "</presentation>");
        }
        
        // Mock JFileChooser
        try (MockedConstruction<JFileChooser> mockConstruction = mockConstruction(JFileChooser.class, 
                (mock, context) -> {
                    when(mock.showOpenDialog(any(Frame.class))).thenReturn(JFileChooser.APPROVE_OPTION);
                    when(mock.getSelectedFile()).thenReturn(xmlFile);
                })) {
            
            // Mock PresentationLoaderFactory
            try (MockedStatic<PresentationLoaderFactory> mockedFactory = mockStatic(PresentationLoaderFactory.class)) {
                mockedFactory.when(() -> PresentationLoaderFactory.createLoader("xml")).thenReturn(mockLoader);
                
                // Act
                openFileCommand.execute();
                
                // Assert
                // Verify that the presentation was loaded
                verify(mockLoader, times(1)).loadPresentation(eq(mockPresentation), eq(xmlFile.getPath()));
                verify(mockPresentation, times(1)).setSlideNumber(0);
            }
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
                    when(mock.showOpenDialog(any(Frame.class))).thenReturn(JFileChooser.CANCEL_OPTION);
                })) {
            
            // Act
            openFileCommand.execute();
            
            // Assert - verify loader was not called
            try (MockedStatic<PresentationLoaderFactory> mockedFactory = mockStatic(PresentationLoaderFactory.class)) {
                mockedFactory.verifyNoInteractions();
                verify(mockPresentation, never()).setSlideNumber(anyInt());
            }
        }
    }
    
    @Test
    @DisplayName("Should handle IO exceptions gracefully")
    void shouldHandleIOExceptionsGracefully() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI test in headless environment");
            
        // Create a test file
        File xmlFile = tempDir.resolve("test.xml").toFile();
        
        // Mock JFileChooser
        try (MockedConstruction<JFileChooser> mockConstruction = mockConstruction(JFileChooser.class, 
                (mock, context) -> {
                    when(mock.showOpenDialog(any(Frame.class))).thenReturn(JFileChooser.APPROVE_OPTION);
                    when(mock.getSelectedFile()).thenReturn(xmlFile);
                })) {
            
            // Mock PresentationLoaderFactory to throw IOException
            try (MockedStatic<PresentationLoaderFactory> mockedFactory = mockStatic(PresentationLoaderFactory.class);
                 MockedStatic<JOptionPane> mockedOptionPane = mockStatic(JOptionPane.class)) {
                
                // Set up the mock to throw exception
                doThrow(new IOException("Test exception"))
                    .when(mockLoader).loadPresentation(any(Presentation.class), anyString());
                mockedFactory.when(() -> PresentationLoaderFactory.createLoader("xml")).thenReturn(mockLoader);
                
                // Mock JOptionPane to avoid showing dialog
                mockedOptionPane.when(() -> JOptionPane.showMessageDialog(
                    any(), anyString(), anyString(), anyInt())).thenAnswer(invocation -> null);
                
                // Act & Assert - should not throw exception outside execute()
                assertDoesNotThrow(() -> openFileCommand.execute());
                
                // Verify error dialog was shown
                mockedOptionPane.verify(() -> JOptionPane.showMessageDialog(
                    eq(mockFrame), contains("IO Exception"), eq("Jabberpoint Error"), eq(JOptionPane.ERROR_MESSAGE)));
            }
        }
    }
}
