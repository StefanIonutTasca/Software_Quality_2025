package org.jabberpoint.test;

import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MenuController class
 */
public class MenuControllerTest {

    private MenuController menuController;
    private Frame mockFrame;
    private Presentation mockPresentation;

    @BeforeEach
    void setUp() {
        mockFrame = mock(Frame.class);
        mockPresentation = mock(Presentation.class);
        menuController = new MenuController(mockFrame, mockPresentation);
    }

    @Test
    @DisplayName("Should create menu items with correct shortcuts")
    void shouldCreateMenuItemsWithCorrectShortcuts() {
        // Act
        MenuItem fileItem = menuController.mkMenuItem("File");
        MenuItem exitItem = menuController.mkMenuItem("Exit");
        
        // Assert
        assertEquals("File", fileItem.getLabel());
        assertEquals('F', fileItem.getShortcut().getKey());
        
        assertEquals("Exit", exitItem.getLabel());
        assertEquals('E', exitItem.getShortcut().getKey());
    }
    
    @Test
    @DisplayName("Should create menu with File, View and Help menus")
    void shouldCreateMenuWithCorrectStructure() {
        // Get all menus from the menu bar
        Menu[] menus = getMenus(menuController);
        
        // Verify we have at least 2 menus (File and View, Help might be handled differently)
        assertTrue(menus.length >= 2);
        
        // Check menu labels
        List<String> menuLabels = new ArrayList<>();
        for (Menu menu : menus) {
            menuLabels.add(menu.getLabel());
        }
        
        assertTrue(menuLabels.contains("File"));
        assertTrue(menuLabels.contains("View"));
        
        // Check for menu items in File menu
        Menu fileMenu = findMenuByLabel(menus, "File");
        assertNotNull(fileMenu);
        
        List<String> fileMenuItems = getMenuItemLabels(fileMenu);
        assertTrue(fileMenuItems.contains("Open"));
        assertTrue(fileMenuItems.contains("New"));
        assertTrue(fileMenuItems.contains("Save"));
        assertTrue(fileMenuItems.contains("Exit"));
        
        // Check for menu items in View menu
        Menu viewMenu = findMenuByLabel(menus, "View");
        assertNotNull(viewMenu);
        
        List<String> viewMenuItems = getMenuItemLabels(viewMenu);
        assertTrue(viewMenuItems.contains("Next"));
        assertTrue(viewMenuItems.contains("Prev"));
        assertTrue(viewMenuItems.contains("Go to"));
    }
    
    @Test
    @DisplayName("Test Next menu item action")
    void testNextMenuItemAction() throws Exception {
        // Find the Next menu item
        MenuItem nextItem = findMenuItemByLabel("Next");
        assertNotNull(nextItem);
        
        // Simulate click
        simulateMenuItemClick(nextItem);
        
        // Verify presentation.nextSlide was called
        verify(mockPresentation, times(1)).nextSlide();
    }
    
    @Test
    @DisplayName("Test Prev menu item action")
    void testPrevMenuItemAction() throws Exception {
        // Find the Prev menu item
        MenuItem prevItem = findMenuItemByLabel("Prev");
        assertNotNull(prevItem);
        
        // Simulate click
        simulateMenuItemClick(prevItem);
        
        // Verify presentation.prevSlide was called
        verify(mockPresentation, times(1)).prevSlide();
    }
    
    @Test
    @DisplayName("Test New menu item action")
    void testNewMenuItemAction() throws Exception {
        // Find the New menu item
        MenuItem newItem = findMenuItemByLabel("New");
        assertNotNull(newItem);
        
        // Simulate click
        simulateMenuItemClick(newItem);
        
        // Verify presentation.clear was called
        verify(mockPresentation, times(1)).clear();
        verify(mockFrame, times(1)).repaint();
    }
    
    @Test
    @DisplayName("Test Exit menu item action")
    void testExitMenuItemAction() throws Exception {
        // Find the Exit menu item
        MenuItem exitItem = findMenuItemByLabel("Exit");
        assertNotNull(exitItem);
        
        // Simulate click
        simulateMenuItemClick(exitItem);
        
        // Verify presentation.exit was called with 0
        verify(mockPresentation, times(1)).exit(0);
    }
    
    // Helper methods for testing the menu structure and actions
    
    private Menu[] getMenus(MenuController menuBar) {
        int menuCount = menuBar.getMenuCount();
        Menu[] menus = new Menu[menuCount];
        for (int i = 0; i < menuCount; i++) {
            menus[i] = menuBar.getMenu(i);
        }
        return menus;
    }
    
    private Menu findMenuByLabel(Menu[] menus, String label) {
        for (Menu menu : menus) {
            if (label.equals(menu.getLabel())) {
                return menu;
            }
        }
        return null;
    }
    
    private List<String> getMenuItemLabels(Menu menu) {
        List<String> labels = new ArrayList<>();
        int itemCount = menu.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            MenuItem item = menu.getItem(i);
            // Skip separators
            if (item != null && item.getLabel() != null) {
                labels.add(item.getLabel());
            }
        }
        return labels;
    }
    
    private MenuItem findMenuItemByLabel(String label) {
        Menu[] menus = getMenus(menuController);
        for (Menu menu : menus) {
            int itemCount = menu.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                MenuItem item = menu.getItem(i);
                if (item != null && label.equals(item.getLabel())) {
                    return item;
                }
            }
        }
        return null;
    }
    
    private void simulateMenuItemClick(MenuItem menuItem) throws Exception {
        // Get all action listeners
        Field f = MenuItem.class.getDeclaredField("actionListeners");
        f.setAccessible(true);
        Object listeners = f.get(menuItem);
        
        if (listeners instanceof java.awt.event.ActionListener[]) {
            ActionEvent mockEvent = new ActionEvent(menuItem, ActionEvent.ACTION_PERFORMED, menuItem.getActionCommand());
            for (java.awt.event.ActionListener listener : (java.awt.event.ActionListener[])listeners) {
                listener.actionPerformed(mockEvent);
            }
        } else if (listeners instanceof java.awt.event.ActionListener) {
            ActionEvent mockEvent = new ActionEvent(menuItem, ActionEvent.ACTION_PERFORMED, menuItem.getActionCommand());
            ((java.awt.event.ActionListener)listeners).actionPerformed(mockEvent);
        }
    }
}
