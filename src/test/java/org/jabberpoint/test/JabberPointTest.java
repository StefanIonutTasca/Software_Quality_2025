package org.jabberpoint.test;
import org.jabberpoint.src.model.Slide;

import org.jabberpoint.src.app.JabberPoint;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.io.PresentationLoader;
import org.jabberpoint.src.io.PresentationLoaderFactory;
import org.jabberpoint.src.ui.SlideViewerFrame;
import org.jabberpoint.src.model.Style;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

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

    private MockedStatic<Style> mockedStyle;
    private MockedStatic<PresentationLoaderFactory> mockedLoaderFactory;
    private MockedStatic<JOptionPane> mockedJOptionPane;
    
    private MockedConstruction<SlideViewerFrame> mockedFrame;
    private MockedConstruction<Presentation> mockedPresentation;
    
    private PresentationLoader mockLoader;
    
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // Only construct the mocks when not in headless mode
        if (!GraphicsEnvironment.isHeadless()) {
            MockitoAnnotations.openMocks(this);
            
            mockedStyle = Mockito.mockStatic(Style.class);
            mockedLoaderFactory = Mockito.mockStatic(PresentationLoaderFactory.class);
            mockedJOptionPane = Mockito.mockStatic(JOptionPane.class);
            
            mockLoader = mock(PresentationLoader.class);
            
            // Mock SlideViewerFrame constructor
            mockedFrame = Mockito.mockConstruction(SlideViewerFrame.class);
            
            // Mock Presentation constructor
            mockedPresentation = Mockito.mockConstruction(Presentation.class);
            
            // Mock Factory to return our loader
            mockedLoaderFactory.when(() -> PresentationLoaderFactory.createLoader(anyString()))
                .thenReturn(mockLoader);
        }
        
        // Redirect System.err for IO exception testing
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        // Close all static mocks if they were created
        if (!GraphicsEnvironment.isHeadless()) {
            if (mockedStyle != null) mockedStyle.close();
            if (mockedLoaderFactory != null) mockedLoaderFactory.close();
            if (mockedJOptionPane != null) mockedJOptionPane.close();
            if (mockedFrame != null) mockedFrame.close();
            if (mockedPresentation != null) mockedPresentation.close();
        }
        
        // Restore original System.err
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("main should initialize Style singleton")
    void mainShouldInitializeStyleSingleton() {
        if (GraphicsEnvironment.isHeadless()) {
            // Skip test in headless environment
            return;
        }
        
        // Act
        JabberPoint.main(new String[0]);
        
        // Verify Style singleton was accessed
        mockedStyle.verify(() -> Style.getInstance());
    }

    @Test
    @DisplayName("main should create presentation and frame")
    void mainShouldCreatePresentationAndFrame() {
        if (GraphicsEnvironment.isHeadless()) {
            // Skip test in headless environment
            return;
        }
        
        // Act
        JabberPoint.main(new String[0]);
        
        // Verify Presentation was constructed
        assertEquals(1, mockedPresentation.constructed().size(), "Should create one Presentation");
        
        // Verify SlideViewerFrame was constructed with correct title
        assertEquals(1, mockedFrame.constructed().size(), "Should create one SlideViewerFrame");
        verify(mockedFrame.constructed().get(0), times(1)).setupWindow(any(), any());
    }

    @Test
    @DisplayName("main with no args should load demo presentation")
    void mainWithNoArgsShouldLoadDemoPresentation() throws IOException {
        if (GraphicsEnvironment.isHeadless()) {
            // Skip test in headless environment
            return;
        }
        
        // Act
        JabberPoint.main(new String[0]);
        
        // Verify demo loader was created
        mockedLoaderFactory.verify(() -> PresentationLoaderFactory.createLoader("demo"));
        
        // Verify loadPresentation was called with empty string
        verify(mockLoader, times(1)).loadPresentation(any(Presentation.class), eq(""));
    }

    @Test
    @DisplayName("main with filename should load XML presentation")
    void mainWithFilenameShouldLoadXMLPresentation() throws IOException {
        if (GraphicsEnvironment.isHeadless()) {
            // Skip test in headless environment
            return;
        }
        
        // Act
        JabberPoint.main(new String[]{"test.xml"});
        
        // Verify XML loader was created
        mockedLoaderFactory.verify(() -> PresentationLoaderFactory.createLoader("xml"));
        
        // Verify loadPresentation was called with filename
        verify(mockLoader, times(1)).loadPresentation(any(Presentation.class), eq("test.xml"));
    }

    @Test
    @DisplayName("main should handle IO exceptions")
    void mainShouldHandleIOExceptions() throws IOException {
        if (GraphicsEnvironment.isHeadless()) {
            // Skip test in headless environment
            return;
        }
        
        // Arrange
        IOException testException = new IOException("Test exception");
        doThrow(testException).when(mockLoader).loadPresentation(any(Presentation.class), anyString());
        
        // Act
        JabberPoint.main(new String[0]);
        
        // Verify error dialog was shown with appropriate error message
        // Use string literals instead of accessing protected constants
        mockedJOptionPane.verify(() -> 
            JOptionPane.showMessageDialog(
                eq(null),
                contains("IO Error: " + testException),  // Use contains instead of exact match
                contains("Jabberpoint Error"),           // Use contains instead of exact match
                eq(JOptionPane.ERROR_MESSAGE)
            )
        );
    }

    @Test
    @DisplayName("main should set slide number to 0 after loading")
    void mainShouldSetSlideNumberToZeroAfterLoading() throws IOException {
        if (GraphicsEnvironment.isHeadless()) {
            // Skip test in headless environment
            return;
        }
        
        // Act
        JabberPoint.main(new String[0]);
        
        // Verify setSlideNumber was called with 0
        Presentation constructedPresentation = mockedPresentation.constructed().get(0);
        verify(constructedPresentation, times(1)).setSlideNumber(0);
    }

    // Test for main method
    @Test
    @DisplayName("JabberPoint should have a main method")
    void jabberPointShouldHaveMainMethod() throws NoSuchMethodException {
        assertNotNull(JabberPoint.class.getMethod("main", String[].class), 
                "JabberPoint should have a main method");
    }
}


