package org.jabberpoint.test;

import org.jabberpoint.src.KeyController;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.SlideViewerFrame;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SlideViewerFrame class
 */
class SlideViewerFrameTest {

    @Mock
    private Presentation mockPresentation;
    
    @Mock
    private SlideViewerComponent mockSlideViewerComponent;
    
    @Mock
    private Container mockContainer;

    private SlideViewerFrame frame;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Skip tests in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping SlideViewerFrame tests in headless environment");
    }
    
    @Test
    @DisplayName("Constructor should set up window with expected properties")
    void constructorShouldSetupWindowWithExpectedProperties() {
        // Arrange & Act
        frame = new SlideViewerFrame("Test Title", mockPresentation);
        
        // Assert
        assertEquals("Jabberpoint 1.6 - OU", frame.getTitle(), "Title should be set to JABTITLE constant");
        assertEquals(new Dimension(SlideViewerFrame.WIDTH, SlideViewerFrame.HEIGHT), frame.getSize(), 
                "Frame size should match WIDTH and HEIGHT constants");
        assertTrue(frame.isVisible(), "Frame should be visible");
        
        // Verify window listeners were added (for window closing)
        WindowListener[] listeners = frame.getWindowListeners();
        assertTrue(listeners.length > 0, "Should have at least one window listener");
        
        // Verify key listener was added (should be KeyController)
        assertEquals(1, frame.getKeyListeners().length, "Should have one key listener");
        assertTrue(frame.getKeyListeners()[0] instanceof KeyController, 
                "Key listener should be instance of KeyController");
        
        // Verify menu bar was added (should be MenuController)
        assertNotNull(frame.getMenuBar(), "Menu bar should not be null");
        assertTrue(frame.getMenuBar() instanceof MenuController, 
                "Menu bar should be instance of MenuController");
        
        // Clean up
        frame.dispose();
    }
    
    @Test
    @DisplayName("setupWindow should add SlideViewerComponent to content pane")
    void setupWindowShouldAddSlideViewerComponentToContentPane() {
        // Arrange
        frame = spy(new SlideViewerFrame("Test Title", mockPresentation));
        
        // Mock the container
        doReturn(mockContainer).when(frame).getContentPane();
        
        // Act
        frame.setupWindow(mockSlideViewerComponent, mockPresentation);
        
        // Assert
        verify(mockContainer).add(mockSlideViewerComponent);
        
        // Clean up
        frame.dispose();
    }
    
    @Test
    @DisplayName("setupWindow should add KeyController to frame")
    void setupWindowShouldAddKeyControllerToFrame() {
        // Arrange
        frame = spy(new SlideViewerFrame("Test Title", mockPresentation));
        
        // Mock the container
        doReturn(mockContainer).when(frame).getContentPane();
        
        // Act
        frame.setupWindow(mockSlideViewerComponent, mockPresentation);
        
        // Assert
        verify(frame).addKeyListener(any(KeyController.class));
        
        // Clean up
        frame.dispose();
    }
    
    @Test
    @DisplayName("setupWindow should set MenuController as menu bar")
    void setupWindowShouldSetMenuControllerAsMenuBar() {
        // Arrange
        frame = spy(new SlideViewerFrame("Test Title", mockPresentation));
        
        // Mock the container
        doReturn(mockContainer).when(frame).getContentPane();
        
        // Act
        frame.setupWindow(mockSlideViewerComponent, mockPresentation);
        
        // Assert
        verify(frame).setMenuBar(any(MenuController.class));
        
        // Clean up
        frame.dispose();
    }
}
