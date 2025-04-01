package org.jabberpoint.test;

import org.jabberpoint.src.Slide;
import org.jabberpoint.src.SlideItem;
import org.jabberpoint.src.Style;
import org.jabberpoint.src.TextItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SlideTest {

    private Slide slide;
    
    @Mock
    private Graphics mockGraphics;
    
    @Mock
    private ImageObserver mockObserver;
    
    @Mock
    private SlideItem mockSlideItem;
    
    @Mock
    private Rectangle mockRectangle;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize Style singleton
        Style.createStyles();
        
        // Create a new slide
        slide = new Slide();
        
        // Set up common mocks
        mockRectangle = new Rectangle(0, 0, Slide.WIDTH, Slide.HEIGHT);
        when(mockSlideItem.getLevel()).thenReturn(1);
        
        // Set up bounding box behavior for slide items
        Rectangle itemBoundingBox = new Rectangle(0, 0, 100, 50);
        when(mockSlideItem.getBoundingBox(any(Graphics.class), any(ImageObserver.class), anyFloat(), any(Style.class)))
            .thenReturn(itemBoundingBox);
    }

    @Test
    @DisplayName("Constructor should initialize slide with empty items vector")
    void constructorShouldInitializeSlideWithEmptyItemsVector() {
        // Verify the slide was initialized with empty items vector
        assertNotNull(slide.getSlideItems(), "Slide items vector should not be null");
        assertEquals(0, slide.getSize(), "Slide should start with 0 items");
    }
    
    @Test
    @DisplayName("append should add SlideItem to the slide")
    void appendShouldAddSlideItemToSlide() {
        // Add a slide item
        slide.append(mockSlideItem);
        
        // Verify item was added
        assertEquals(1, slide.getSize(), "Slide should have 1 item after append");
        assertSame(mockSlideItem, slide.getSlideItem(0), "The item at index 0 should be the one we added");
    }
    
    @Test
    @DisplayName("append with level and message should create and add TextItem")
    void appendWithLevelAndMessageShouldCreateAndAddTextItem() {
        // Add a text item using the level and message overload
        int level = 2;
        String message = "Test Slide Text";
        slide.append(level, message);
        
        // Verify item was added
        assertEquals(1, slide.getSize(), "Slide should have 1 item after append");
        
        // Verify it's a TextItem with the right properties
        SlideItem addedItem = slide.getSlideItem(0);
        assertTrue(addedItem instanceof TextItem, "Added item should be a TextItem");
        assertEquals(level, addedItem.getLevel(), "TextItem should have the specified level");
        assertEquals(message, ((TextItem)addedItem).getText(), "TextItem should have the specified text");
    }
    
    @Test
    @DisplayName("getTitle and setTitle should manage slide title")
    void getTitleAndSetTitleShouldManageSlideTitle() {
        // Initially title should be null
        assertNull(slide.getTitle(), "Initial title should be null");
        
        // Set title
        String title = "Test Slide Title";
        slide.setTitle(title);
        
        // Verify title was set
        assertEquals(title, slide.getTitle(), "Title should be set correctly");
    }
    
    @Test
    @DisplayName("getSlideItem should return item at specified index")
    void getSlideItemShouldReturnItemAtSpecifiedIndex() {
        // Add multiple items
        SlideItem item1 = new TextItem(1, "Item 1");
        SlideItem item2 = new TextItem(2, "Item 2");
        SlideItem item3 = new TextItem(3, "Item 3");
        
        slide.append(item1);
        slide.append(item2);
        slide.append(item3);
        
        // Verify each item can be retrieved by index
        assertSame(item1, slide.getSlideItem(0), "Should return item1 at index 0");
        assertSame(item2, slide.getSlideItem(1), "Should return item2 at index 1");
        assertSame(item3, slide.getSlideItem(2), "Should return item3 at index 2");
    }
    
    @Test
    @DisplayName("getSlideItems should return all items in a Vector")
    void getSlideItemsShouldReturnAllItemsInVector() {
        // Add multiple items
        SlideItem item1 = new TextItem(1, "Item 1");
        SlideItem item2 = new TextItem(2, "Item 2");
        
        slide.append(item1);
        slide.append(item2);
        
        // Get the vector
        Vector<SlideItem> items = slide.getSlideItems();
        
        // Verify vector contains the items
        assertNotNull(items, "Items vector should not be null");
        assertEquals(2, items.size(), "Vector should contain 2 items");
        assertTrue(items.contains(item1), "Vector should contain item1");
        assertTrue(items.contains(item2), "Vector should contain item2");
    }
    
    @Test
    @DisplayName("getSize should return the number of items in the slide")
    void getSizeShouldReturnNumberOfItemsInSlide() {
        // Initially should be 0
        assertEquals(0, slide.getSize(), "Initial size should be 0");
        
        // Add items
        slide.append(new TextItem(1, "Item 1"));
        assertEquals(1, slide.getSize(), "Size should be 1 after adding one item");
        
        slide.append(new TextItem(2, "Item 2"));
        assertEquals(2, slide.getSize(), "Size should be 2 after adding second item");
        
        slide.append(new TextItem(3, "Item 3"));
        assertEquals(3, slide.getSize(), "Size should be 3 after adding third item");
    }
    
    @Test
    @DisplayName("draw should render slide title and all items")
    void drawShouldRenderSlideTitleAndAllItems() {
        // Set up slide with title and items
        String title = "Test Slide";
        slide.setTitle(title);
        
        SlideItem item1 = spy(new TextItem(1, "Item 1"));
        SlideItem item2 = spy(new TextItem(2, "Item 2"));
        
        slide.append(item1);
        slide.append(item2);
        
        // Set up bounding boxes to simulate layout
        Rectangle titleBox = new Rectangle(0, 0, 200, 50);
        Rectangle item1Box = new Rectangle(0, 50, 200, 40);
        Rectangle item2Box = new Rectangle(0, 90, 200, 30);
        
        // Configure mocks/spies
        doReturn(titleBox).when(item1).getBoundingBox(any(), any(), anyFloat(), any());
        doReturn(item1Box).when(item1).getBoundingBox(any(), any(), anyFloat(), any());
        doReturn(item2Box).when(item2).getBoundingBox(any(), any(), anyFloat(), any());
        
        // Call draw
        Rectangle area = new Rectangle(0, 10, Slide.WIDTH, Slide.HEIGHT);
        slide.draw(mockGraphics, area, mockObserver);
        
        // Verify each item was drawn
        // For title
        verify(item1, atLeastOnce()).getBoundingBox(any(), any(), anyFloat(), any());
        verify(item1, atLeastOnce()).draw(anyInt(), anyInt(), anyFloat(), any(), any(), any());
        
        // For first item
        verify(item1, atLeastOnce()).getBoundingBox(any(), any(), anyFloat(), any());
        verify(item1, atLeastOnce()).draw(anyInt(), anyInt(), anyFloat(), any(), any(), any());
        
        // For second item
        verify(item2, atLeastOnce()).getBoundingBox(any(), any(), anyFloat(), any());
        verify(item2, atLeastOnce()).draw(anyInt(), anyInt(), anyFloat(), any(), any(), any());
    }
    
    @Test
    @DisplayName("getScale should calculate correct scale based on area dimensions")
    void getScaleShouldCalculateCorrectScaleBasedOnAreaDimensions() {
        // We'll test the private getScale method indirectly by verifying the scale passed to draw
        
        // Create a spy to capture the scale
        SlideItem spyItem = spy(new TextItem(1, "Test"));
        slide.append(spyItem);
        slide.setTitle("Title");
        
        // Test with different area dimensions
        
        // Case 1: Area exactly matches slide dimensions
        Rectangle fullArea = new Rectangle(0, 0, Slide.WIDTH, Slide.HEIGHT);
        slide.draw(mockGraphics, fullArea, mockObserver);
        // Scale should be 1.0 in this case
        verify(spyItem).draw(anyInt(), anyInt(), eq(1.0f), any(), any(), any());
        
        // Reset spy for next test
        reset(spyItem);
        
        // Case 2: Area is half the width, height remains the same
        Rectangle halfWidthArea = new Rectangle(0, 0, Slide.WIDTH / 2, Slide.HEIGHT);
        slide.draw(mockGraphics, halfWidthArea, mockObserver);
        // Scale should be 0.5 in this case
        verify(spyItem).draw(anyInt(), anyInt(), eq(0.5f), any(), any(), any());
        
        // Reset spy for next test
        reset(spyItem);
        
        // Case 3: Area is half the height, width remains the same
        Rectangle halfHeightArea = new Rectangle(0, 0, Slide.WIDTH, Slide.HEIGHT / 2);
        slide.draw(mockGraphics, halfHeightArea, mockObserver);
        // Scale should be 0.5 in this case
        verify(spyItem).draw(anyInt(), anyInt(), eq(0.5f), any(), any(), any());
    }
}
