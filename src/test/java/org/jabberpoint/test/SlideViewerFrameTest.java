package org.jabberpoint.test;

import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.SlideViewerFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.awt.Component;
import java.awt.Container;
import java.awt.MenuBar;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for SlideViewerFrame class
 * Note: This test is designed with Mockito to avoid creating actual GUI components
 */
public class SlideViewerFrameTest {

    private Presentation mockPresentation;

    @BeforeEach
    void setUp() {
        mockPresentation = mock(Presentation.class);
    }

    @Test
    @DisplayName("Should create frame with correct title and size")
    void shouldCreateFrameWithCorrectTitleAndSize() {
        // We'll use a subclass for testing to avoid creating actual window
        SlideViewerFrame frame = spy(new SlideViewerFrame("Test Frame", mockPresentation) {
            // Override methods that would create actual GUI components
            @Override
            public void setVisible(boolean visible) {
                // Don't actually make visible
            }
            
            @Override
            public void addWindowListener(WindowListener l) {
                // Store listener but don't actually add it
            }
        });
        
        // Verify the frame was set up correctly
        verify(frame).setTitle("Jabberpoint 1.6 - OU");
        verify(frame).setSize(anyInt(), anyInt());
    }
    
    @Test
    @DisplayName("Should add key listener")
    void shouldAddKeyListener() {
        // We'll use a spy to count the number of listeners added
        SlideViewerFrame frame = spy(new SlideViewerFrame("Test Frame", mockPresentation) {
            private int keyListenerCount = 0;
            
            @Override
            public void setVisible(boolean visible) {
                // Don't actually make visible
            }
            
            @Override
            public void addKeyListener(KeyListener l) {
                keyListenerCount++;
                super.addKeyListener(l);
            }
            
            @Override
            public int getKeyListeners().length {
                return keyListenerCount;
            }
        });
        
        // Verify a key listener was added (indirectly through our spy)
        verify(frame).addKeyListener(any());
    }
    
    @Test
    @DisplayName("Should set menu bar")
    void shouldSetMenuBar() {
        // Use a spy to verify the menu bar is set
        SlideViewerFrame frame = spy(new SlideViewerFrame("Test Frame", mockPresentation) {
            @Override
            public void setVisible(boolean visible) {
                // Don't actually make visible
            }
        });
        
        // Verify menu bar was set
        verify(frame).setMenuBar(any(MenuBar.class));
    }
    
    @Test
    @DisplayName("Should add slideViewerComponent to content pane")
    void shouldAddSlideViewerComponentToContentPane() {
        // Create a spy with a mock content pane
        Container mockContainer = mock(Container.class);
        
        SlideViewerFrame frame = spy(new SlideViewerFrame("Test Frame", mockPresentation) {
            @Override
            public Container getContentPane() {
                return mockContainer;
            }
            
            @Override
            public void setVisible(boolean visible) {
                // Don't actually make visible
            }
        });
        
        // Verify component was added to content pane
        verify(mockContainer).add(any(Component.class));
    }
    
    @Test
    @DisplayName("Should create frame with default width and height")
    void shouldCreateFrameWithDefaultWidthAndHeight() {
        // Create a spy to capture the dimension values
        SlideViewerFrame frame = spy(new SlideViewerFrame("Test Frame", mockPresentation) {
            @Override
            public void setVisible(boolean visible) {
                // Don't actually make visible
            }
        });
        
        // Verify size was set with expected dimensions
        verify(frame).setSize(SlideViewerFrame.WIDTH, SlideViewerFrame.HEIGHT);
        assertEquals(1200, SlideViewerFrame.WIDTH);
        assertEquals(800, SlideViewerFrame.HEIGHT);
    }
}
