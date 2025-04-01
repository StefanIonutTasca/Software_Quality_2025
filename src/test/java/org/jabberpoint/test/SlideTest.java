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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SlideTest {

    private Slide slide;
    
    // Instead of using a Mock Graphics, use a real Graphics2D
    private Graphics2D mockGraphics2D;
    
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
        
        // Add a mock SlideItem to the slide
        slide.append(mockSlideItem);
        
        // Set up bounding box for the SlideItem
        Rectangle itemBoundingBox = new Rectangle(0, 0, 100, 50);
        when(mockSlideItem.getBoundingBox(any(Graphics.class), any(ImageObserver.class), anyFloat(), any(Style.class)))
            .thenReturn(itemBoundingBox);
        
        // Create a BufferedImage and get its Graphics2D
        BufferedImage image = new BufferedImage(
            500, 500, BufferedImage.TYPE_INT_ARGB);
        mockGraphics2D = image.createGraphics();
    }
    
    @Test
    @DisplayName("append should add a SlideItem to the slide")
    void appendShouldAddSlideItemToSlide() {
        // Slide already has one item from setup
        assertEquals(1, slide.getSize(), "Slide should have 1 item initially");
        
        // Create a new SlideItem
        SlideItem newItem = new TextItem(2, "Test Item");
        
        // Append the new item
        slide.append(newItem);
        
        // Check that the item was added
        assertEquals(2, slide.getSize(), "Slide should have 2 items after append");
        assertEquals(newItem, slide.getSlideItem(1), "The appended item should be at index 1");
    }
    
    @Test
    @DisplayName("getTitle and setTitle should manage the slide title")
    void getTitleAndSetTitleShouldManageSlideTitle() {
        // Default title should be empty
        assertEquals("", slide.getTitle(), "Default title should be empty string");
        
        // Set a new title
        String newTitle = "Test Slide Title";
        slide.setTitle(newTitle);
        
        // Check that the title was set
        assertEquals(newTitle, slide.getTitle(), "Title should be set to the new value");
    }
    
    @Test
    @DisplayName("getSlideItem should return the slide item at given index")
    void getSlideItemShouldReturnSlideItemAtGivenIndex() {
        // Add a second SlideItem
        SlideItem secondItem = new TextItem(3, "Second Test Item");
        slide.append(secondItem);
        
        // Check that we can get items by index
        assertEquals(mockSlideItem, slide.getSlideItem(0), "First item should be the mock item");
        assertEquals(secondItem, slide.getSlideItem(1), "Second item should be the newly added item");
    }
    
    @Test
    @DisplayName("getSlideItems should return the vector of slide items")
    void getSlideItemsShouldReturnVectorOfSlideItems() {
        Vector<SlideItem> items = slide.getSlideItems();
        
        assertNotNull(items, "SlideItems vector should not be null");
        assertEquals(1, items.size(), "SlideItems vector should have 1 item");
        assertEquals(mockSlideItem, items.get(0), "SlideItems should contain the mock item");
    }
    
    @Test
    @DisplayName("getSize should return the number of slide items")
    void getSizeShouldReturnNumberOfSlideItems() {
        // Initially has 1 item from setup
        assertEquals(1, slide.getSize(), "Size should be 1 initially");
        
        // Add a second item
        slide.append(new TextItem(2, "Test Item"));
        
        // Size should be updated
        assertEquals(2, slide.getSize(), "Size should be 2 after adding an item");
    }
    
    @Test
    @DisplayName("draw should render slide title and all items")
    void drawShouldRenderSlideTitleAndAllItems() {
        // Set a title for the slide
        slide.setTitle("Test Title");
        
        // Set up rectangle for drawing area
        Rectangle drawArea = new Rectangle(0, 0, 800, 600);
        
        // Call draw
        slide.draw(10, 20, 1.0f, mockGraphics2D, drawArea, mockObserver);
        
        // Verify that the mockSlideItem's draw method was called
        verify(mockSlideItem).draw(anyInt(), anyInt(), anyFloat(), eq(mockGraphics2D), any(Style.class), eq(mockObserver));
    }
    
    @Test
    @DisplayName("getScale should calculate correct scale based on area dimensions")
    void getScaleShouldCalculateCorrectScaleBasedOnAreaDimensions() {
        // Set up a mock rectangle for the slide bounding box that's larger than the area
        Rectangle boundingBox = new Rectangle(0, 0, 1000, 800);
        Rectangle area = new Rectangle(0, 0, 500, 400);
        
        // Add a second item to the slide
        slide.append(new TextItem(2, "Second Item"));
        
        // Set up mocks to return a large bounding box
        when(mockSlideItem.getBoundingBox(any(Graphics.class), any(ImageObserver.class), eq(1.0f), any(Style.class)))
            .thenReturn(boundingBox);
        
        // Calculate scale - should be reduced to fit area
        float scale = slide.getScale(mockGraphics2D, area);
        
        // Expect scale to be reduced
        assertTrue(scale < 1.0f, "Scale should be reduced for large slides");
        assertTrue(scale > 0.0f, "Scale should be positive");
    }
}
