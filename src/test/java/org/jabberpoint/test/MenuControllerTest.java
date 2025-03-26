package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;

import org.jabberpoint.src.AboutBox;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for MenuController class - simplified version
 * This test focuses on verifying that the MenuController creates menu items correctly
 * and that the shortcut keys are set up correctly.
 */
@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    private MenuController menuController;
    
    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;
    
    @BeforeEach
    void setUp() {
        menuController = new MenuController(mockFrame, mockPresentation);
    }

    @Test
    @DisplayName("Should create a menu item with shortcut")
    void shouldCreateMenuItemWithShortcut() {
        // Act
        MenuItem menuItem = menuController.mkMenuItem("Test");
        
        // Assert
        assertEquals("Test", menuItem.getLabel());
        assertNotNull(menuItem.getShortcut(), "Shortcut should be created");
        assertEquals('T', menuItem.getShortcut().getKey(), "Shortcut key should be first character of menu item");
    }
    
    @Test
    @DisplayName("Should have File menu with Open, New, Save items")
    void shouldHaveFileMenuWithCorrectItems() {
        // Arrange
        boolean hasFileMenu = false;
        Menu fileMenu = null;
        
        // Find the File menu
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            if ("File".equals(menu.getLabel())) {
                hasFileMenu = true;
                fileMenu = menu;
                break;
            }
        }
        
        // Assert
        assertTrue(hasFileMenu, "Should have File menu");
        assertNotNull(fileMenu, "File menu should not be null");
        
        // Check for required menu items
        boolean hasOpen = false;
        boolean hasNew = false;
        boolean hasSave = false;
        boolean hasExit = false;
        
        for (int i = 0; i < fileMenu.getItemCount(); i++) {
            MenuItem item = fileMenu.getItem(i);
            if (item != null) { // Skip separators which return null
                String label = item.getLabel();
                if ("Open".equals(label)) hasOpen = true;
                if ("New".equals(label)) hasNew = true;
                if ("Save".equals(label)) hasSave = true;
                if ("Exit".equals(label)) hasExit = true;
            }
        }
        
        assertTrue(hasOpen, "File menu should have Open item");
        assertTrue(hasNew, "File menu should have New item");
        assertTrue(hasSave, "File menu should have Save item");
        assertTrue(hasExit, "File menu should have Exit item");
    }
    
    @Test
    @DisplayName("Should have View menu with Next, Prev, Go to items")
    void shouldHaveViewMenuWithCorrectItems() {
        // Arrange
        boolean hasViewMenu = false;
        Menu viewMenu = null;
        
        // Find the View menu
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            if ("View".equals(menu.getLabel())) {
                hasViewMenu = true;
                viewMenu = menu;
                break;
            }
        }
        
        // Assert
        assertTrue(hasViewMenu, "Should have View menu");
        assertNotNull(viewMenu, "View menu should not be null");
        
        // Check for required menu items
        boolean hasNext = false;
        boolean hasPrev = false;
        boolean hasGoTo = false;
        
        for (int i = 0; i < viewMenu.getItemCount(); i++) {
            MenuItem item = viewMenu.getItem(i);
            if (item != null) { // Skip separators which return null
                String label = item.getLabel();
                if ("Next".equals(label)) hasNext = true;
                if ("Prev".equals(label)) hasPrev = true;
                if ("Go to".equals(label)) hasGoTo = true;
            }
        }
        
        assertTrue(hasNext, "View menu should have Next item");
        assertTrue(hasPrev, "View menu should have Prev item");
        assertTrue(hasGoTo, "View menu should have Go to item");
    }
    
    @Test
    @DisplayName("Should have Help menu with About item")
    void shouldHaveHelpMenuWithAboutItem() {
        // Check if the Help menu is set as the help menu
        Menu helpMenu = menuController.getHelpMenu();
        assertNotNull(helpMenu, "Help menu should be set");
        
        // Check that it contains the About item
        boolean hasAbout = false;
        for (int i = 0; i < helpMenu.getItemCount(); i++) {
            MenuItem item = helpMenu.getItem(i);
            if (item != null && "About".equals(item.getLabel())) {
                hasAbout = true;
                break;
            }
        }
        
        assertTrue(hasAbout, "Help menu should have About item");
    }
    
    @Test
    @DisplayName("Should trigger nextSlide when Next menu item is clicked")
    void shouldTriggerNextSlideWhenNextMenuItemIsClicked() {
        // Arrange
        Menu viewMenu = findMenuByLabel("View");
        MenuItem nextItem = findMenuItemByLabel(viewMenu, "Next");
        assertNotNull(nextItem, "Next menu item should exist");
        
        // Get the action listener
        assertEquals(1, nextItem.getActionListeners().length, "Should have one action listener");
        
        // Create a mock action event
        ActionEvent mockEvent = mock(ActionEvent.class);
        
        // Act - Trigger the action listener
        nextItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation).nextSlide();
    }
    
    @Test
    @DisplayName("Should trigger prevSlide when Prev menu item is clicked")
    void shouldTriggerPrevSlideWhenPrevMenuItemIsClicked() {
        // Arrange
        Menu viewMenu = findMenuByLabel("View");
        MenuItem prevItem = findMenuItemByLabel(viewMenu, "Prev");
        assertNotNull(prevItem, "Prev menu item should exist");
        
        // Get the action listener
        assertEquals(1, prevItem.getActionListeners().length, "Should have one action listener");
        
        // Create a mock action event
        ActionEvent mockEvent = mock(ActionEvent.class);
        
        // Act - Trigger the action listener
        prevItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation).prevSlide();
    }
    
    @Test
    @DisplayName("Should trigger exit when Exit menu item is clicked")
    void shouldTriggerExitWhenExitMenuItemIsClicked() {
        // Arrange
        Menu fileMenu = findMenuByLabel("File");
        MenuItem exitItem = findMenuItemByLabel(fileMenu, "Exit");
        assertNotNull(exitItem, "Exit menu item should exist");
        
        // Get the action listener
        assertEquals(1, exitItem.getActionListeners().length, "Should have one action listener");
        
        // Create a mock action event
        ActionEvent mockEvent = mock(ActionEvent.class);
        
        // Act - Trigger the action listener
        exitItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation).exit(0);
    }
    
    @Test
    @DisplayName("Should show About dialog when About menu item is clicked")
    void shouldShowAboutDialogWhenAboutMenuItemIsClicked() {
        // Arrange
        Menu helpMenu = menuController.getHelpMenu();
        MenuItem aboutItem = findMenuItemByLabel(helpMenu, "About");
        assertNotNull(aboutItem, "About menu item should exist");
        
        // Get the action listener
        assertEquals(1, aboutItem.getActionListeners().length, "Should have one action listener");
        
        // Create a mock action event
        ActionEvent mockEvent = mock(ActionEvent.class);
        
        // Act
        try (MockedStatic<AboutBox> mockedAboutBox = mockStatic(AboutBox.class)) {
            // Trigger the action
            aboutItem.getActionListeners()[0].actionPerformed(mockEvent);
            
            // Assert
            mockedAboutBox.verify(() -> AboutBox.show(mockFrame));
        }
    }
    
    /**
     * Helper method to find a menu by its label
     */
    private Menu findMenuByLabel(String label) {
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            if (label.equals(menu.getLabel())) {
                return menu;
            }
        }
        fail("Menu with label '" + label + "' not found");
        return null;
    }
    
    /**
     * Helper method to find a menu item by its label within a menu
     */
    private MenuItem findMenuItemByLabel(Menu menu, String label) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != null && label.equals(item.getLabel())) {
                return item;
            }
        }
        fail("Menu item with label '" + label + "' not found in menu '" + menu.getLabel() + "'");
        return null;
    }
}
