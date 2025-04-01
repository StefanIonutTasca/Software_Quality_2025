package org.jabberpoint.test;

import org.jabberpoint.src.AboutBox;
import org.jabberpoint.src.MenuController;
import org.jabberpoint.src.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Unit tests for MenuController class
 */
@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    private MenuController menuController;
    
    @Mock
    private Frame mockFrame;
    
    @Mock
    private Presentation mockPresentation;
    
    // Define our own test file path
    private static final String TEST_FILE_PATH = "./test-presentation.xml";

    @BeforeEach
    void setUp() {
        // Skip tests in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(), 
            "Skipping GUI tests in headless environment");
            
        MockitoAnnotations.openMocks(this);
        menuController = new MenuController(mockFrame, mockPresentation);
    }

    @Test
    @DisplayName("Should create menu item with shortcut")
    void mkMenuItemShouldCreateMenuItemWithShortcut() {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
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
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // Act - the menuController is already created in setUp()
        
        // Assert
        assertEquals(3, menuController.getMenuCount(), "Should have 3 menus (File, View, Help)");
        
        Menu fileMenu = menuController.getMenu(0);
        Menu viewMenu = menuController.getMenu(1);
        Menu helpMenu = menuController.getHelpMenu();
        
        assertEquals("File", fileMenu.getLabel(), "First menu should be 'File'");
        assertEquals("View", viewMenu.getLabel(), "Second menu should be 'View'");
        assertEquals("Help", helpMenu.getLabel(), "Help menu should be 'Help'");
        
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
        
        // Check Help menu items
        assertEquals(1, helpMenu.getItemCount(), "Help menu should have 1 item");
        assertEquals("About", helpMenu.getItem(0).getLabel(), "First item should be 'About'");
    }
    
    @Test
    @DisplayName("File > Open menu action should load presentation")
    void openMenuActionShouldLoadPresentation() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // Arrange - Create test file
        File testFile = new File(TEST_FILE_PATH);
        boolean fileCreated = testFile.createNewFile();
        try {
            // Setup
            MenuItem openMenuItem = findMenuItemByLabel(menuController, "Open");
            ActionEvent mockEvent = new ActionEvent(openMenuItem, ActionEvent.ACTION_PERFORMED, "open");
            
            // Act
            openMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
            
            // We cannot verify protected clear() method directly, but we can verify setSlideNumber and repaint
            verify(mockPresentation, times(1)).setSlideNumber(0);
            verify(mockFrame, times(1)).repaint();
        } finally {
            // Cleanup
            if (fileCreated) {
                testFile.delete();
            }
        }
    }
    
    @Test
    @DisplayName("File > New menu action should clear presentation")
    void newMenuActionShouldClearPresentation() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // Arrange
        MenuItem newMenuItem = findMenuItemByLabel(menuController, "New");
        ActionEvent mockEvent = new ActionEvent(newMenuItem, ActionEvent.ACTION_PERFORMED, "new");
        
        // Act
        newMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // We cannot verify protected clear() method directly, but we can verify repaint
        verify(mockFrame, times(1)).repaint();
    }
    
    @Test
    @DisplayName("File > Save menu action should save presentation")
    void saveMenuActionShouldSavePresentation() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // Setup
        MenuItem saveMenuItem = findMenuItemByLabel(menuController, "Save");
        ActionEvent mockEvent = new ActionEvent(saveMenuItem, ActionEvent.ACTION_PERFORMED, "save");
        
        // Act
        saveMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // No simple way to verify XMLAccessor was used properly without extensive mocking
        // At least verify no exceptions were thrown
        assertDoesNotThrow(() -> saveMenuItem.getActionListeners()[0].actionPerformed(mockEvent));
    }
    
    @Test
    @DisplayName("File > Exit menu action should exit the presentation")
    void exitMenuActionShouldExitPresentation() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
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
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
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
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // Arrange
        MenuItem prevMenuItem = findMenuItemByLabel(menuController, "Prev");
        ActionEvent mockEvent = new ActionEvent(prevMenuItem, ActionEvent.ACTION_PERFORMED, "prev");
        
        // Act
        prevMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
        
        // Assert
        verify(mockPresentation, times(1)).prevSlide();
    }
    
    @Test
    @DisplayName("View > Go to menu action should set slide number")
    void gotoMenuActionShouldSetSlideNumber() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // This test is challenging because it involves JOptionPane.showInputDialog
        // Using try-with-resources with a MockedStatic
        try (MockedStatic<JOptionPane> mockedStatic = mockStatic(JOptionPane.class)) {
            // Arrange
            mockedStatic.when(() -> JOptionPane.showInputDialog(any())).thenReturn("3");
            
            MenuItem gotoMenuItem = findMenuItemByLabel(menuController, "Go to");
            ActionEvent mockEvent = new ActionEvent(gotoMenuItem, ActionEvent.ACTION_PERFORMED, "goto");
            
            // Act
            gotoMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
            
            // Assert
            verify(mockPresentation, times(1)).setSlideNumber(2); // 3-1=2, zero-based index
        }
    }
    
    @Test
    @DisplayName("Help > About menu action should show about box")
    void aboutMenuActionShouldShowAboutBox() throws Exception {
        // Skip test in headless environment
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        
        // This test requires mocking static method AboutBox.show
        try (MockedStatic<AboutBox> mockedStatic = mockStatic(AboutBox.class)) {
            // Arrange
            MenuItem aboutMenuItem = findMenuItemByLabel(menuController, "About");
            ActionEvent mockEvent = new ActionEvent(aboutMenuItem, ActionEvent.ACTION_PERFORMED, "about");
            
            // Act
            aboutMenuItem.getActionListeners()[0].actionPerformed(mockEvent);
            
            // Assert
            mockedStatic.verify(() -> AboutBox.show(mockFrame), times(1));
        }
    }
    
    // Helper method to find menu item by label
    private MenuItem findMenuItemByLabel(MenuController controller, String label) {
        // First check regular menus
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
        
        // Then check help menu separately
        Menu helpMenu = controller.getHelpMenu();
        if (helpMenu != null) {
            for (int j = 0; j < helpMenu.getItemCount(); j++) {
                if (helpMenu.getItem(j) instanceof MenuItem) {
                    MenuItem item = (MenuItem) helpMenu.getItem(j);
                    if (label.equals(item.getLabel())) {
                        return item;
                    }
                }
            }
        }
        
        return null;
    }
}
