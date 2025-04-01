package org.jabberpoint.test;

import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MenuController class
 */
class MenuControllerTest {

    private MenuController menuController;
    
    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        menuController = new MenuController(mockFrame, mockPresentation);
    }

    @Test
    @DisplayName("Should create menu item with shortcut")
    void mkMenuItemShouldCreateMenuItemWithShortcut() {
        // Act
        MenuItem menuItem = menuController.mkMenuItem("Test");
        
        // Assert
        assertEquals("Test", menuItem.getLabel(), "Menu item label should be 'Test'");
        assertNotNull(menuItem.getShortcut(), "Menu item should have a shortcut");
        assertEquals('T', menuItem.getShortcut().getKey(), "Menu shortcut should be 'T'");
    }
    
    @Test
    @DisplayName("Should initialize with correct menu structure")
    void constructorShouldInitializeCorrectMenuStructure() {
        // Act - the menuController is already created in setUp()
        
        // Assert
        assertEquals(3, menuController.getMenuCount(), "Should have 3 menus (File, View, Help)");
        
        Menu fileMenu = menuController.getMenu(0);
        Menu viewMenu = menuController.getMenu(1);
        
        assertEquals("File", fileMenu.getLabel(), "First menu should be 'File'");
        assertEquals("View", viewMenu.getLabel(), "Second menu should be 'View'");
        
        // Check File menu items
        assertEquals(5, fileMenu.getItemCount(), "File menu should have 5 items (including separator)");
        assertEquals("Open", fileMenu.getItem(0).getLabel(), "First item should be 'Open'");
        assertEquals("New", fileMenu.getItem(1).getLabel(), "Second item should be 'New'");
        assertEquals("Save", fileMenu.getItem(2).getLabel(), "Third item should be 'Save'");
        assertEquals("Exit", fileMenu.getItem(4).getLabel(), "Fifth item should be 'Exit'");
        
        // Check View menu items
        assertEquals(3, viewMenu.getItemCount(), "View menu should have 3 items");
        assertEquals("Next", viewMenu.getItem(0).getLabel(), "First item should be 'Next'");
        assertEquals("Prev", viewMenu.getItem(1).getLabel(), "Second item should be 'Prev'");
        assertEquals("Go to", viewMenu.getItem(2).getLabel(), "Third item should be 'Go to'");
    }
    
    @Test
    @DisplayName("File > New menu action should call appropriate methods")
    void newMenuActionShouldCallAppropriateMethod() throws Exception {
        // Arrange
        MenuItem newMenuItem = findMenuItemByLabel(menuController, "New");
        ActionEvent mockEvent = new ActionEvent(newMenuItem, ActionEvent.ACTION_PERFORMED, "new");
        
        // Act
        newMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert - Since we can't directly verify presentation.clear() because it's protected,
        // we can at least verify the frame was repainted
        verify(mockFrame, times(1)).repaint();
    }
    
    @Test
    @DisplayName("File > Exit menu action should exit the presentation")
    void exitMenuActionShouldExitPresentation() throws Exception {
        // Arrange
        MenuItem exitMenuItem = findMenuItemByLabel(menuController, "Exit");
        ActionEvent mockEvent = new ActionEvent(exitMenuItem, ActionEvent.ACTION_PERFORMED, "exit");
        
        // Act
        exitMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation, times(1)).exit(0);
    }
    
    @Test
    @DisplayName("View > Next menu action should call nextSlide")
    void nextMenuActionShouldCallNextSlide() throws Exception {
        // Arrange
        MenuItem nextMenuItem = findMenuItemByLabel(menuController, "Next");
        ActionEvent mockEvent = new ActionEvent(nextMenuItem, ActionEvent.ACTION_PERFORMED, "next");
        
        // Act
        nextMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation, times(1)).nextSlide();
    }
    
    @Test
    @DisplayName("View > Prev menu action should call prevSlide")
    void prevMenuActionShouldCallPrevSlide() throws Exception {
        // Arrange
        MenuItem prevMenuItem = findMenuItemByLabel(menuController, "Prev");
        ActionEvent mockEvent = new ActionEvent(prevMenuItem, ActionEvent.ACTION_PERFORMED, "prev");
        
        // Act
        prevMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation, times(1)).prevSlide();
    }
    
    // Helper method to find menu item by label
    private MenuItem findMenuItemByLabel(MenuController controller, String label) {
        for (int i = 0; i < controller.getMenuCount(); i++) {
            Menu menu = controller.getMenu(i);
            for (int j = 0; j < menu.getItemCount(); j++) {
                if (menu.getItem(j) instanceof MenuItem) {
                    MenuItem item = (MenuItem) menu.getItem(j);
                    if (label.equals(item.getLabel())) {
                        return item;
                    }
                }
            }
        }
        return null;
    }
}
