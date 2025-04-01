package org.jabberpoint.test;

import org.jabberpoint.src.AboutBox;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.jabberpoint.src.XMLAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;
    
    private MenuController menuController;
    
    @BeforeEach
    void setUp() {
        if (!GraphicsEnvironment.isHeadless()) {
            menuController = new MenuController(mockFrame, mockPresentation);
        }
    }
    
    @Test
    @DisplayName("MenuController should create menu items with expected names")
    void shouldCreateMenuItemsWithExpectedNames() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Check if menu items for file operations exist
        assertNotNull(findMenuItem("Open"), "Open menu item should exist");
        assertNotNull(findMenuItem("New"), "New menu item should exist");
        assertNotNull(findMenuItem("Save"), "Save menu item should exist");
        assertNotNull(findMenuItem("Exit"), "Exit menu item should exist");
        
        // Check if menu items for slide navigation exist
        assertNotNull(findMenuItem("Next"), "Next menu item should exist");
        assertNotNull(findMenuItem("Prev"), "Prev menu item should exist");
        assertNotNull(findMenuItem("Go to"), "Go to menu item should exist");
        
        // Check if menu item for about dialog exists
        assertNotNull(findMenuItem("About"), "About menu item should exist");
    }
    
    @Test
    @DisplayName("mkMenuItem should create a menu item with the given name")
    void mkMenuItemShouldCreateMenuItemWithGivenName() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        MenuItem menuItem = menuController.mkMenuItem("Test");
        assertEquals("Test", menuItem.getLabel(), "Menu item label should match the given name");
        assertNotNull(menuItem.getShortcut(), "Menu item should have a shortcut");
        assertEquals('T', menuItem.getShortcut().getKey(), "Menu item shortcut should be first character of the name");
    }
    
    @Test
    @DisplayName("Open menu item should load presentation from XML file")
    void openMenuItemShouldLoadPresentationFromXML() throws IOException {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Create mocks for XMLAccessor
        XMLAccessor mockXmlAccessor = mock(XMLAccessor.class);
        
        try (MockedStatic<XMLAccessor> mockedConstructor = mockStatic(XMLAccessor.class)) {
            // Set up constructor mocking to return our mock
            mockedConstructor.when(XMLAccessor::new).thenReturn(mockXmlAccessor);
            
            // Trigger the Open menu item's action
            MenuItem openItem = findMenuItem("Open");
            assertNotNull(openItem, "Open menu item should exist");
            
            // Create an ActionEvent
            ActionEvent event = new ActionEvent(openItem, ActionEvent.ACTION_PERFORMED, "command");
            
            // Call action listener directly
            openItem.getActionListeners()[0].actionPerformed(event);
            
            // We can't directly verify protected methods, but we can verify the public methods
            // that would be affected by the clear() call
            verify(mockXmlAccessor).loadFile(eq(mockPresentation), eq("test.xml"));
            verify(mockPresentation).setSlideNumber(0);
            verify(mockFrame).repaint();
        }
    }
    
    @Test
    @DisplayName("Open menu item should show error dialog when loading fails")
    void openMenuItemShouldShowErrorOnLoadFailure() throws IOException {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Create mocks for XMLAccessor and JOptionPane
        XMLAccessor mockXmlAccessor = mock(XMLAccessor.class);
        IOException testException = new IOException("Test error");
        
        // Set up XMLAccessor to throw an exception when loading
        doThrow(testException).when(mockXmlAccessor).loadFile(any(Presentation.class), anyString());
        
        try (MockedStatic<XMLAccessor> mockedAccessor = mockStatic(XMLAccessor.class);
             MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            
            // Set up mocking
            mockedAccessor.when(XMLAccessor::new).thenReturn(mockXmlAccessor);
            
            // Trigger the Open menu item's action
            MenuItem openItem = findMenuItem("Open");
            assertNotNull(openItem, "Open menu item should exist");
            
            // Create an ActionEvent
            ActionEvent event = new ActionEvent(openItem, ActionEvent.ACTION_PERFORMED, "command");
            
            // Call action listener directly
            openItem.getActionListeners()[0].actionPerformed(event);
            
            // Verify error dialog was shown
            mockedJOptionPane.verify(() -> 
                JOptionPane.showMessageDialog(
                    eq(mockFrame),
                    contains("IO Exception:"),
                    eq("Load Error"),
                    eq(JOptionPane.ERROR_MESSAGE)
                )
            );
            
            // Verify frame was repainted (can't verify protected clear() method)
            verify(mockFrame).repaint();
        }
    }
    
    @Test
    @DisplayName("New menu item should clear presentation")
    void newMenuItemShouldClearPresentation() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Find the New menu item
        MenuItem newItem = findMenuItem("New");
        assertNotNull(newItem, "New menu item should exist");
        
        // Create an ActionEvent
        ActionEvent event = new ActionEvent(newItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Call action listener directly
        newItem.getActionListeners()[0].actionPerformed(event);
        
        // Since clear() is protected, we can only verify the public repaint() was called
        verify(mockFrame).repaint();
    }
    
    @Test
    @DisplayName("Save menu item should save presentation to XML file")
    void saveMenuItemShouldSavePresentationToXML() throws IOException {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Create mock for XMLAccessor
        XMLAccessor mockXmlAccessor = mock(XMLAccessor.class);
        
        try (MockedStatic<XMLAccessor> mockedConstructor = mockStatic(XMLAccessor.class)) {
            // Set up constructor mocking to return our mock
            mockedConstructor.when(XMLAccessor::new).thenReturn(mockXmlAccessor);
            
            // Trigger the Save menu item's action
            MenuItem saveItem = findMenuItem("Save");
            assertNotNull(saveItem, "Save menu item should exist");
            
            // Create an ActionEvent
            ActionEvent event = new ActionEvent(saveItem, ActionEvent.ACTION_PERFORMED, "command");
            
            // Call action listener directly
            saveItem.getActionListeners()[0].actionPerformed(event);
            
            // Verify the presentation was saved
            verify(mockXmlAccessor).saveFile(eq(mockPresentation), eq("dump.xml"));
        }
    }
    
    @Test
    @DisplayName("Save menu item should show error dialog when saving fails")
    void saveMenuItemShouldShowErrorOnSaveFailure() throws IOException {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Create mocks for XMLAccessor and JOptionPane
        XMLAccessor mockXmlAccessor = mock(XMLAccessor.class);
        IOException testException = new IOException("Test error");
        
        // Set up XMLAccessor to throw an exception when saving
        doThrow(testException).when(mockXmlAccessor).saveFile(any(Presentation.class), anyString());
        
        try (MockedStatic<XMLAccessor> mockedAccessor = mockStatic(XMLAccessor.class);
             MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            
            // Set up mocking
            mockedAccessor.when(XMLAccessor::new).thenReturn(mockXmlAccessor);
            
            // Trigger the Save menu item's action
            MenuItem saveItem = findMenuItem("Save");
            assertNotNull(saveItem, "Save menu item should exist");
            
            // Create an ActionEvent
            ActionEvent event = new ActionEvent(saveItem, ActionEvent.ACTION_PERFORMED, "command");
            
            // Call action listener directly
            saveItem.getActionListeners()[0].actionPerformed(event);
            
            // Verify error dialog was shown
            mockedJOptionPane.verify(() -> 
                JOptionPane.showMessageDialog(
                    eq(mockFrame),
                    contains("IO Exception:"),
                    eq("Save Error"),
                    eq(JOptionPane.ERROR_MESSAGE)
                )
            );
        }
    }
    
    @Test
    @DisplayName("Exit menu item should exit the presentation")
    void exitMenuItemShouldExitPresentation() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Find the Exit menu item
        MenuItem exitItem = findMenuItem("Exit");
        assertNotNull(exitItem, "Exit menu item should exist");
        
        // Create an ActionEvent
        ActionEvent event = new ActionEvent(exitItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Call action listener directly
        exitItem.getActionListeners()[0].actionPerformed(event);
        
        // Verify the presentation exit method was called
        verify(mockPresentation).exit(0);
    }
    
    @Test
    @DisplayName("Next menu item should go to next slide")
    void nextMenuItemShouldGoToNextSlide() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Find the Next menu item
        MenuItem nextItem = findMenuItem("Next");
        assertNotNull(nextItem, "Next menu item should exist");
        
        // Create an ActionEvent
        ActionEvent event = new ActionEvent(nextItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Call action listener directly
        nextItem.getActionListeners()[0].actionPerformed(event);
        
        // Verify the nextSlide method was called
        verify(mockPresentation).nextSlide();
    }
    
    @Test
    @DisplayName("Prev menu item should go to previous slide")
    void prevMenuItemShouldGoToPreviousSlide() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Find the Prev menu item
        MenuItem prevItem = findMenuItem("Prev");
        assertNotNull(prevItem, "Prev menu item should exist");
        
        // Create an ActionEvent
        ActionEvent event = new ActionEvent(prevItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Call action listener directly
        prevItem.getActionListeners()[0].actionPerformed(event);
        
        // Verify the prevSlide method was called
        verify(mockPresentation).prevSlide();
    }
    
    @Test
    @DisplayName("Go to menu item should set slide number from user input")
    void gotoMenuItemShouldSetSlideNumberFromUserInput() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Find the Go to menu item
        MenuItem gotoItem = findMenuItem("Go to");
        assertNotNull(gotoItem, "Go to menu item should exist");
        
        try (MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class)) {
            // Mock the user input dialog to return "3"
            mockedJOptionPane.when(() -> JOptionPane.showInputDialog(any())).thenReturn("3");
            
            // Create an ActionEvent
            ActionEvent event = new ActionEvent(gotoItem, ActionEvent.ACTION_PERFORMED, "command");
            
            // Call action listener directly
            gotoItem.getActionListeners()[0].actionPerformed(event);
            
            // Verify the setSlideNumber method was called with the expected value (0-based index)
            verify(mockPresentation).setSlideNumber(2);
        }
    }
    
    @Test
    @DisplayName("About menu item should show About dialog")
    void aboutMenuItemShouldShowAboutDialog() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Find the About menu item
        MenuItem aboutItem = findMenuItem("About");
        assertNotNull(aboutItem, "About menu item should exist");
        
        try (MockedStatic<AboutBox> mockedAboutBox = mockStatic(AboutBox.class)) {
            // Create an ActionEvent
            ActionEvent event = new ActionEvent(aboutItem, ActionEvent.ACTION_PERFORMED, "command");
            
            // Call action listener directly
            aboutItem.getActionListeners()[0].actionPerformed(event);
            
            // Verify the AboutBox.show method was called
            mockedAboutBox.verify(() -> AboutBox.show(mockFrame));
        }
    }
    
    /**
     * Helper method to find a menu item in the MenuController by its label
     */
    private MenuItem findMenuItem(String label) {
        // Get all the menus from the MenuController
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            for (int j = 0; j < menu.getItemCount(); j++) {
                MenuItem item = menu.getItem(j);
                if (item != null && label.equals(item.getLabel())) {
                    return item;
                }
            }
        }
        
        // Try the help menu (handled differently in MenuBar)
        if (menuController.getHelpMenu() != null) {
            Menu helpMenu = menuController.getHelpMenu();
            for (int j = 0; j < helpMenu.getItemCount(); j++) {
                MenuItem item = helpMenu.getItem(j);
                if (item != null && label.equals(item.getLabel())) {
                    return item;
                }
            }
        }
        
        return null;
    }
}
