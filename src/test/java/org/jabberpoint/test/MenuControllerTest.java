package org.jabberpoint.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for MenuController class
 */
class MenuControllerTest {

    private Frame frameMock;
    private Presentation presentationMock;
    private MenuController menuController;
    
    @BeforeAll
    static void setUpHeadlessMode() {
        // Set headless mode for UI testing
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setUp() {
        frameMock = mock(Frame.class);
        presentationMock = mock(Presentation.class);
        menuController = new MenuController(frameMock, presentationMock);
    }

    @Test
    @DisplayName("Should create menu items correctly")
    void shouldCreateMenuItemsCorrectly() {
        // Act
        MenuItem item = menuController.mkMenuItem("Test");
        
        // Assert
        assertEquals("Test", item.getLabel());
        assertNotNull(item.getShortcut());
        assertEquals('T', item.getShortcut().getKey());
    }
    
    @Test
    @DisplayName("Should have correct menu structure")
    void shouldHaveCorrectMenuStructure() {
        // Menu order is File, View, Help (Help is set as help menu)
        
        // Check number of menus
        assertEquals(2, menuController.getMenuCount()); // File, View (Help is special)
        
        // Check file menu
        Menu fileMenu = menuController.getMenu(0);
        assertEquals("File", fileMenu.getLabel());
        assertEquals(5, fileMenu.getItemCount()); // Open, New, Save, separator, Exit
        
        assertEquals("Open", fileMenu.getItem(0).getLabel());
        assertEquals("New", fileMenu.getItem(1).getLabel());
        assertEquals("Save", fileMenu.getItem(2).getLabel());
        // Item 3 is a separator
        assertEquals("Exit", fileMenu.getItem(4).getLabel());
        
        // Check view menu
        Menu viewMenu = menuController.getMenu(1);
        assertEquals("View", viewMenu.getLabel());
        assertEquals(3, viewMenu.getItemCount()); // Next, Prev, Go to
        
        assertEquals("Next", viewMenu.getItem(0).getLabel());
        assertEquals("Prev", viewMenu.getItem(1).getLabel());
        assertEquals("Go to", viewMenu.getItem(2).getLabel());
        
        // Check help menu
        Menu helpMenu = menuController.getHelpMenu();
        assertNotNull(helpMenu);
        assertEquals("Help", helpMenu.getLabel());
        assertEquals(1, helpMenu.getItemCount()); // About
        assertEquals("About", helpMenu.getItem(0).getLabel());
    }
    
    @Test
    @DisplayName("Should call nextSlide when Next menu item is clicked")
    void shouldCallNextSlideWhenNextMenuItemIsClicked() throws Exception {
        // Arrange - get the Next menu item's ActionListener
        Menu viewMenu = menuController.getMenu(1); // View menu
        MenuItem nextMenuItem = viewMenu.getItem(0); // Next menu item
        
        // Create an action event
        ActionEvent event = new ActionEvent(nextMenuItem, ActionEvent.ACTION_PERFORMED, "Next");
        
        // Get the ActionListener from the menu item
        // We need to extract it via reflection since it's not directly accessible
        Field[] fields = MenuItem.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.getType().getName().contains("ActionListener")) {
                field.setAccessible(true);
                java.awt.event.ActionListener listener = (java.awt.event.ActionListener) field.get(nextMenuItem);
                listener.actionPerformed(event);
                break;
            }
        }
        
        // Verify
        verify(presentationMock, times(1)).nextSlide();
    }
    
    @Test
    @DisplayName("Should call prevSlide when Prev menu item is clicked")
    void shouldCallPrevSlideWhenPrevMenuItemIsClicked() throws Exception {
        // Arrange - get the Prev menu item's ActionListener
        Menu viewMenu = menuController.getMenu(1); // View menu
        MenuItem prevMenuItem = viewMenu.getItem(1); // Prev menu item
        
        // Create an action event
        ActionEvent event = new ActionEvent(prevMenuItem, ActionEvent.ACTION_PERFORMED, "Prev");
        
        // Get the ActionListener from the menu item
        // We need to extract it via reflection since it's not directly accessible
        Field[] fields = MenuItem.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.getType().getName().contains("ActionListener")) {
                field.setAccessible(true);
                java.awt.event.ActionListener listener = (java.awt.event.ActionListener) field.get(prevMenuItem);
                listener.actionPerformed(event);
                break;
            }
        }
        
        // Verify
        verify(presentationMock, times(1)).prevSlide();
    }
    
    @Test
    @DisplayName("Should call exit when Exit menu item is clicked")
    void shouldCallExitWhenExitMenuItemIsClicked() throws Exception {
        // Arrange - get the Exit menu item's ActionListener
        Menu fileMenu = menuController.getMenu(0); // File menu
        MenuItem exitMenuItem = fileMenu.getItem(4); // Exit menu item
        
        // Create an action event
        ActionEvent event = new ActionEvent(exitMenuItem, ActionEvent.ACTION_PERFORMED, "Exit");
        
        // Get the ActionListener from the menu item using reflection
        Field[] fields = MenuItem.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.getType().getName().contains("ActionListener")) {
                field.setAccessible(true);
                java.awt.event.ActionListener listener = (java.awt.event.ActionListener) field.get(exitMenuItem);
                listener.actionPerformed(event);
                break;
            }
        }
        
        // Verify
        verify(presentationMock, times(1)).exit(0);
    }
    
    @Test
    @DisplayName("Should trigger actions when New menu item is clicked")
    void shouldTriggerActionsWhenNewMenuItemIsClicked() throws Exception {
        // Arrange - get the New menu item's ActionListener
        Menu fileMenu = menuController.getMenu(0); // File menu
        MenuItem newMenuItem = fileMenu.getItem(1); // New menu item
        
        // Create an action event
        ActionEvent event = new ActionEvent(newMenuItem, ActionEvent.ACTION_PERFORMED, "New");
        
        // Get the ActionListener from the menu item using reflection
        Field[] fields = MenuItem.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.getType().getName().contains("ActionListener")) {
                field.setAccessible(true);
                java.awt.event.ActionListener listener = (java.awt.event.ActionListener) field.get(newMenuItem);
                listener.actionPerformed(event);
                break;
            }
        }
        
        // We cannot directly verify if clear() was called since it's not visible,
        // but we can verify that the frame was repainted which happens after clear
        verify(frameMock, times(1)).repaint();
    }
}
