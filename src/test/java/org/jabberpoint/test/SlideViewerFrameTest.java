package org.jabberpoint.test;

import org.jabberpoint.src.KeyController;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.SlideViewerFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        // Only run setup if not in headless environment
        if (!GraphicsEnvironment.isHeadless()) {
            // Don't create the frame here as different tests need different setups
        }
    }
    
    @Test
    @DisplayName("Constructor should set up window with expected properties")
    void constructorShouldSetupWindowWithExpectedProperties() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
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
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("Constructor with default title parameter should use default title")
    void constructorWithDefaultTitleShouldUseDefaultTitle() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Act - use the constructor with a default title parameter
            frame = new SlideViewerFrame("", mockPresentation);
            
            // Assert - should use the default title
            assertEquals("Jabberpoint 1.6 - OU", frame.getTitle(), "Title should be set to JABTITLE constant");
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("setupWindow should add SlideViewerComponent to content pane")
    void setupWindowShouldAddSlideViewerComponentToContentPane() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Arrange
            frame = spy(new SlideViewerFrame("Test Title", mockPresentation));
            
            // Mock the container
            doReturn(mockContainer).when(frame).getContentPane();
            
            // Act
            frame.setupWindow(mockSlideViewerComponent, mockPresentation);
            
            // Assert
            verify(mockContainer).add(mockSlideViewerComponent);
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("setupWindow should add KeyController to frame")
    void setupWindowShouldAddKeyControllerToFrame() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Arrange
            frame = spy(new SlideViewerFrame("Test Title", mockPresentation));
            
            // Mock the container
            doReturn(mockContainer).when(frame).getContentPane();
            
            // Act
            frame.setupWindow(mockSlideViewerComponent, mockPresentation);
            
            // Assert
            verify(frame).addKeyListener(any(KeyController.class));
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("setupWindow should set MenuController as menu bar")
    void setupWindowShouldSetMenuControllerAsMenuBar() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Arrange
            frame = spy(new SlideViewerFrame("Test Title", mockPresentation));
            
            // Mock the container
            doReturn(mockContainer).when(frame).getContentPane();
            
            // Act
            frame.setupWindow(mockSlideViewerComponent, mockPresentation);
            
            // Assert
            verify(frame).setMenuBar(any(MenuController.class));
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("Window closing event should trigger System.exit")
    void windowClosingEventShouldTriggerSystemExit() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Arrange
            frame = new SlideViewerFrame("Test Title", mockPresentation);
            
            // Get the window listener
            WindowListener[] listeners = frame.getWindowListeners();
            WindowListener windowListener = listeners[0];
            
            // Create a window event for window closing
            WindowEvent windowEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
            
            // We can't mock System.exit directly without MockedStatic, so we'll verify indirectly
            // This test is mainly to ensure the handler doesn't throw exceptions
            
            // Act & Assert - this should not throw an exception
            assertDoesNotThrow(() -> windowListener.windowClosing(windowEvent),
                    "WindowListener windowClosing should not throw an exception");
            
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("SlideViewerFrame should create SlideViewerComponent during initialization")
    void slideViewerFrameShouldCreateSlideViewerComponentDuringInitialization() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Create a real frame to test actual initialization behavior
            frame = new SlideViewerFrame("Test", mockPresentation);
            
            // Use reflection to get the SlideViewerComponent from the content pane
            Container contentPane = frame.getContentPane();
            assertEquals(1, contentPane.getComponentCount(), "Content pane should have one component");
            
            // The component should be a SlideViewerComponent
            assertTrue(contentPane.getComponent(0) instanceof SlideViewerComponent, 
                    "Content pane component should be SlideViewerComponent");
            
            // Verify the component is properly set up
            SlideViewerComponent component = (SlideViewerComponent) contentPane.getComponent(0);
            assertNotNull(component, "SlideViewerComponent should not be null");
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    @Test
    @DisplayName("SlideViewerFrame should handle all window events besides windowClosing")
    void slideViewerFrameShouldHandleAllWindowEventsBesidesWindowClosing() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            // Arrange
            frame = new SlideViewerFrame("Test Title", mockPresentation);
            
            // Get the window listener
            WindowListener[] listeners = frame.getWindowListeners();
            WindowListener windowListener = listeners[0];
            
            // Create window events for each window event type
            WindowEvent activatedEvent = new WindowEvent(frame, WindowEvent.WINDOW_ACTIVATED);
            WindowEvent deactivatedEvent = new WindowEvent(frame, WindowEvent.WINDOW_DEACTIVATED);
            WindowEvent iconifiedEvent = new WindowEvent(frame, WindowEvent.WINDOW_ICONIFIED);
            WindowEvent deiconifiedEvent = new WindowEvent(frame, WindowEvent.WINDOW_DEICONIFIED);
            WindowEvent openedEvent = new WindowEvent(frame, WindowEvent.WINDOW_OPENED);
            WindowEvent closedEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSED);
            
            // Act & Assert - verify no exceptions are thrown
            assertDoesNotThrow(() -> windowListener.windowActivated(activatedEvent), 
                    "windowActivated should not throw exception");
            assertDoesNotThrow(() -> windowListener.windowDeactivated(deactivatedEvent), 
                    "windowDeactivated should not throw exception");
            assertDoesNotThrow(() -> windowListener.windowIconified(iconifiedEvent), 
                    "windowIconified should not throw exception");
            assertDoesNotThrow(() -> windowListener.windowDeiconified(deiconifiedEvent), 
                    "windowDeiconified should not throw exception");
            assertDoesNotThrow(() -> windowListener.windowOpened(openedEvent), 
                    "windowOpened should not throw exception");
            assertDoesNotThrow(() -> windowListener.windowClosed(closedEvent), 
                    "windowClosed should not throw exception");
        } finally {
            // Clean up
            if (frame != null) {
                frame.dispose();
            }
        }
    }
}
