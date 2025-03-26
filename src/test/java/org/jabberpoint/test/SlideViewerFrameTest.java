package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.MenuBar;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.jabberpoint.src.KeyController;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.SlideViewerComponent;
import org.jabberpoint.src.SlideViewerFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SlideViewerFrame class
 */
class SlideViewerFrameTest {

    private Presentation presentationMock;
    private SlideViewerComponent componentMock;
    private SlideViewerFrame frame;
    
    @BeforeAll
    static void setUpHeadlessMode() {
        // Set headless mode for UI testing
        System.setProperty("java.awt.headless", "true");
    }
    
    @BeforeEach
    void setUp() {
        presentationMock = mock(Presentation.class);
        componentMock = mock(SlideViewerComponent.class);
    }
    
    @Test
    @DisplayName("Should set up window correctly")
    void shouldSetUpWindowCorrectly() {
        // Create a test frame using a spy to avoid actually showing the window
        // but still allow method verification
        SlideViewerFrame frameSpy = spy(new SlideViewerFrame("Test Title", presentationMock));
        doNothing().when(frameSpy).setVisible(anyBoolean());
        
        // Call the method being tested
        frameSpy.setupWindow(componentMock, presentationMock);
        
        // Verify window setup
        assertEquals("Jabberpoint 1.6 - OU", frameSpy.getTitle());
        
        // Verify window listener was added
        WindowListener[] windowListeners = frameSpy.getWindowListeners();
        assertEquals(1, windowListeners.length);
        
        // Verify component was added to content pane
        Component[] components = frameSpy.getContentPane().getComponents();
        assertSame(componentMock, components[0]);
        
        // Verify key listener was added (should be a KeyController)
        KeyListener[] keyListeners = frameSpy.getKeyListeners();
        assertEquals(1, keyListeners.length);
        assertTrue(keyListeners[0] instanceof KeyController);
        
        // Verify menu bar was set (should be a MenuController)
        MenuBar menuBar = frameSpy.getMenuBar();
        assertTrue(menuBar instanceof MenuController);
        
        // Verify size
        assertEquals(SlideViewerFrame.WIDTH, frameSpy.getSize().width);
        assertEquals(SlideViewerFrame.HEIGHT, frameSpy.getSize().height);
        
        // Verify setVisible was called
        verify(frameSpy).setVisible(true);
    }
    
    @Test
    @DisplayName("Should construct frame with given title and presentation")
    void shouldConstructFrameWithGivenTitleAndPresentation() {
        // Use a spy to avoid actually showing the window
        SlideViewerFrame frameSpy = spy(new SlideViewerFrame("Custom Title", presentationMock));
        doNothing().when(frameSpy).setVisible(anyBoolean());
        
        // Note: Most of the verification is already done in setupWindow test
        // Just verify the initial title is set correctly
        assertEquals("Jabberpoint 1.6 - OU", frameSpy.getTitle());
        
        // Verify the content pane has a SlideViewerComponent
        Component[] components = frameSpy.getContentPane().getComponents();
        assertEquals(1, components.length);
        assertTrue(components[0] instanceof SlideViewerComponent);
    }
}
