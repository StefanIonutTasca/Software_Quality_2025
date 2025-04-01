package org.jabberpoint.test;

import org.jabberpoint.src.AboutBox;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests for the ActionListeners within MenuController
 */
class MenuControllerActionListenerTest {

    private MenuController menuController;
    
    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;
    
    private MockedStatic<JOptionPane> mockedJOptionPane;
    private MockedStatic<AboutBox> mockedAboutBox;
    
    private List<MenuItem> menuItems = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Skip initialization in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        MockitoAnnotations.openMocks(this);
        mockedJOptionPane = Mockito.mockStatic(JOptionPane.class);
        mockedAboutBox = Mockito.mockStatic(AboutBox.class);
        
        // Set up JOptionPane mock to return "1" for showInputDialog (for Go to functionality)
        mockedJOptionPane.when(() -> JOptionPane.showInputDialog(any()))
            .thenReturn("1");
            
        menuController = new MenuController(mockFrame, mockPresentation);
        
        // Extract menu items to trigger their ActionListeners
        collectMenuItems();
    }
    
    @AfterEach
    void tearDown() {
        if (mockedJOptionPane != null) {
            mockedJOptionPane.close();
        }
        if (mockedAboutBox != null) {
            mockedAboutBox.close();
        }
    }
    
    /**
     * Helper method to collect all menu items from the MenuController
     */
    private void collectMenuItems() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        menuItems.clear();
        
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            for (int j = 0; j < menu.getItemCount(); j++) {
                MenuItem item = menu.getItem(j);
                if (item != null) {
                    menuItems.add(item);
                }
            }
        }
        
        // Add Help menu items
        Menu helpMenu = menuController.getHelpMenu();
        for (int i = 0; i < helpMenu.getItemCount(); i++) {
            MenuItem item = helpMenu.getItem(i);
            if (item != null) {
                menuItems.add(item);
            }
        }
    }
    
    /**
     * Helper method to find a menu item by its label
     */
    private MenuItem findMenuItem(String label) {
        for (MenuItem item : menuItems) {
            if (label.equals(item.getLabel())) {
                return item;
            }
        }
        return null;
    }
    
    @Test
    @DisplayName("Open menu item should load presentation from test.xml")
    void openMenuItemShouldLoadPresentationFromTestFile() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem openItem = findMenuItem("Open");
        assertNotNull(openItem, "Open menu item should exist");
        ActionEvent event = new ActionEvent(openItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        openItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        verify(mockPresentation, times(1)).clear();
        verify(mockPresentation, times(1)).setSlideNumber(0);
        verify(mockFrame, times(1)).repaint();
    }
    
    @Test
    @DisplayName("New menu item should clear presentation")
    void newMenuItemShouldClearPresentation() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem newItem = findMenuItem("New");
        assertNotNull(newItem, "New menu item should exist");
        ActionEvent event = new ActionEvent(newItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        newItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        verify(mockPresentation, times(1)).clear();
        verify(mockFrame, times(1)).repaint();
    }
    
    @Test
    @DisplayName("Save menu item should save presentation to dump.xml")
    void saveMenuItemShouldSavePresentationToDumpFile() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem saveItem = findMenuItem("Save");
        assertNotNull(saveItem, "Save menu item should exist");
        ActionEvent event = new ActionEvent(saveItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        saveItem.getActionListeners()[0].actionPerformed(event);
        
        // No easy way to verify XMLAccessor, but we can verify no errors were thrown
        // The test passes if no exception is thrown
    }
    
    @Test
    @DisplayName("Exit menu item should exit presentation")
    void exitMenuItemShouldExitPresentation() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem exitItem = findMenuItem("Exit");
        assertNotNull(exitItem, "Exit menu item should exist");
        ActionEvent event = new ActionEvent(exitItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        exitItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        verify(mockPresentation, times(1)).exit(0);
    }
    
    @Test
    @DisplayName("Next menu item should call nextSlide")
    void nextMenuItemShouldCallNextSlide() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem nextItem = findMenuItem("Next");
        assertNotNull(nextItem, "Next menu item should exist");
        ActionEvent event = new ActionEvent(nextItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        nextItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        verify(mockPresentation, times(1)).nextSlide();
    }
    
    @Test
    @DisplayName("Prev menu item should call prevSlide")
    void prevMenuItemShouldCallPrevSlide() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem prevItem = findMenuItem("Prev");
        assertNotNull(prevItem, "Prev menu item should exist");
        ActionEvent event = new ActionEvent(prevItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        prevItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        verify(mockPresentation, times(1)).prevSlide();
    }
    
    @Test
    @DisplayName("Go to menu item should prompt for slide number and set it")
    void gotoMenuItemShouldPromptForSlideNumberAndSetIt() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem gotoItem = findMenuItem("Go to");
        assertNotNull(gotoItem, "Go to menu item should exist");
        ActionEvent event = new ActionEvent(gotoItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Mock JOptionPane to return "1" for the slide number
        
        // Act - trigger the ActionListener
        gotoItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        mockedJOptionPane.verify(() -> JOptionPane.showInputDialog(any()), times(1));
        verify(mockPresentation, times(1)).setSlideNumber(0); // 0 is pageNumber-1
    }
    
    @Test
    @DisplayName("About menu item should show about box")
    void aboutMenuItemShouldShowAboutBox() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        // Arrange
        MenuItem aboutItem = findMenuItem("About");
        assertNotNull(aboutItem, "About menu item should exist");
        ActionEvent event = new ActionEvent(aboutItem, ActionEvent.ACTION_PERFORMED, "command");
        
        // Act - trigger the ActionListener
        aboutItem.getActionListeners()[0].actionPerformed(event);
        
        // Assert
        mockedAboutBox.verify(() -> AboutBox.show(mockFrame), times(1));
    }
}
