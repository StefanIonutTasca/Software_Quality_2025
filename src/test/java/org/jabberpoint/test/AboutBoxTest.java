package org.jabberpoint.test;

import org.jabberpoint.ui.AboutBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import javax.swing.JOptionPane;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * Unit tests for AboutBox class
 */
class AboutBoxTest {

    private MockedStatic<JOptionPane> mockedJOptionPane;
    private Frame mockFrame;

    @BeforeEach
    void setUp() {
        // Skip initialization in headless environment
        if (!GraphicsEnvironment.isHeadless()) {
            mockedJOptionPane = Mockito.mockStatic(JOptionPane.class);
            mockFrame = mock(Frame.class);
        }
    }

    @AfterEach
    void tearDown() {
        if (mockedJOptionPane != null) {
            mockedJOptionPane.close();
        }
    }

    @Test
    @DisplayName("show method should display a message dialog with about information")
    void showShouldDisplayMessageDialogWithAboutInformation() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        // Act
        AboutBox.show(mockFrame);

        // Verify JOptionPane.showMessageDialog was called with correct parameters
        mockedJOptionPane.verify(() ->
            JOptionPane.showMessageDialog(
                eq(mockFrame),
                contains("JabberPoint is a primitive slide-show program in Java(tm)"),
                eq("About JabberPoint"),
                eq(JOptionPane.INFORMATION_MESSAGE)
            ), times(1)
        );
    }
    
    @Test
    @DisplayName("show method should work with null frame")
    void showShouldWorkWithNullFrame() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        // Act
        AboutBox.show(null);

        // Verify JOptionPane.showMessageDialog was called with null frame
        mockedJOptionPane.verify(() ->
            JOptionPane.showMessageDialog(
                eq(null),
                any(),
                eq("About JabberPoint"),
                eq(JOptionPane.INFORMATION_MESSAGE)
            ), times(1)
        );
    }
}

