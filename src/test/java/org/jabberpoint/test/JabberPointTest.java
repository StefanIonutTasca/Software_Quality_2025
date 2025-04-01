package org.jabberpoint.test;

import org.jabberpoint.src.JabberPoint;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.PresentationLoader;
import org.jabberpoint.src.PresentationLoaderFactory;
import org.jabberpoint.src.SlideViewerFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JabberPoint main class
 */
@ExtendWith(MockitoExtension.class)
class JabberPointTest {

    @Mock
    private PresentationLoader mockLoader;
    
    @Mock
    private Presentation mockPresentation;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    private MockedStatic<PresentationLoaderFactory> mockedFactory;
    private MockedStatic<JOptionPane> mockedOptionPane;
    private MockedStatic<SlideViewerFrame> mockedFrame;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
        
        // Skip tests in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping JabberPoint tests in headless environment");
            
        // Setup static mocks
        mockedFactory = Mockito.mockStatic(PresentationLoaderFactory.class);
        mockedOptionPane = Mockito.mockStatic(JOptionPane.class);
        mockedFrame = Mockito.mockStatic(SlideViewerFrame.class);
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        
        // Close static mocks
        if (mockedFactory != null) {
            mockedFactory.close();
        }
        if (mockedOptionPane != null) {
            mockedOptionPane.close();
        }
        if (mockedFrame != null) {
            mockedFrame.close();
        }
    }

    @Test
    @DisplayName("Main method should load demo presentation when no arguments provided")
    void mainShouldLoadDemoPresentationWhenNoArgumentsProvided() throws Exception {
        // Arrange
        String[] noArgs = new String[0];
        
        // Mock the factory to return our mock loader
        mockedFactory.when(() -> PresentationLoaderFactory.createLoader("demo"))
            .thenReturn(mockLoader);
            
        // Act
        JabberPoint.main(noArgs);
        
        // Assert
        // Verify demo loader was created
        mockedFactory.verify(() -> PresentationLoaderFactory.createLoader("demo"));
        
        // Verify loadPresentation was called with empty string
        verify(mockLoader).loadPresentation(any(Presentation.class), eq(""));
    }
    
    @Test
    @DisplayName("Main method should load XML presentation when file argument provided")
    void mainShouldLoadXMLPresentationWhenFileArgumentProvided() throws Exception {
        // Arrange
        String[] args = new String[] {"test.xml"};
        
        // Mock the factory to return our mock loader
        mockedFactory.when(() -> PresentationLoaderFactory.createLoader("xml"))
            .thenReturn(mockLoader);
            
        // Act
        JabberPoint.main(args);
        
        // Assert
        // Verify XML loader was created
        mockedFactory.verify(() -> PresentationLoaderFactory.createLoader("xml"));
        
        // Verify loadPresentation was called with the filename
        verify(mockLoader).loadPresentation(any(Presentation.class), eq("test.xml"));
    }
    
    @Test
    @DisplayName("Main method should show error dialog when IOException occurs")
    void mainShouldShowErrorDialogWhenIOExceptionOccurs() throws Exception {
        // Arrange
        String[] args = new String[] {"test.xml"};
        IOException testException = new IOException("Test exception");
        
        // Mock the factory to return our mock loader
        mockedFactory.when(() -> PresentationLoaderFactory.createLoader("xml"))
            .thenReturn(mockLoader);
            
        // Make the loader throw an IOException
        doThrow(testException).when(mockLoader).loadPresentation(any(Presentation.class), anyString());
        
        // Act
        JabberPoint.main(args);
        
        // Assert
        // Verify error dialog was shown
        mockedOptionPane.verify(() -> 
            JOptionPane.showMessageDialog(
                isNull(), 
                contains("IO Error: " + testException), 
                eq("Jabberpoint Error "), 
                eq(JOptionPane.ERROR_MESSAGE)
            )
        );
    }
    
    @Test
    @DisplayName("JabberPoint class should have expected constants")
    void jabberPointClassShouldHaveExpectedConstants() throws Exception {
        // Use reflection to access protected constants
        Field ioErrField = JabberPoint.class.getDeclaredField("IOERR");
        Field jabErrField = JabberPoint.class.getDeclaredField("JABERR");
        Field versionField = JabberPoint.class.getDeclaredField("JABVERSION");
        
        ioErrField.setAccessible(true);
        jabErrField.setAccessible(true);
        versionField.setAccessible(true);
        
        // Assert the constants have expected values
        assertEquals("IO Error: ", ioErrField.get(null), "IOERR constant should have expected value");
        assertEquals("Jabberpoint Error ", jabErrField.get(null), "JABERR constant should have expected value");
        assertEquals("Jabberpoint 1.6 - OU version", versionField.get(null), "JABVERSION constant should have expected value");
    }
}
