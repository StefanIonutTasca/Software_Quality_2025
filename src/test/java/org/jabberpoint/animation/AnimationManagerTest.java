package org.jabberpoint.animation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.JPanel;

import org.jabberpoint.presentation.Presentation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for AnimationManager.
 * Intentionally has low test coverage and some issues.
 */
public class AnimationManagerTest {
    
    private AnimationManager animationManager;
    private JPanel testComponent;
    private Presentation mockPresentation;
    
    @BeforeEach
    public void setUp() {
        // Reset the singleton instance between tests (not properly implemented)
        animationManager = AnimationManager.getInstance();
        animationManager.clearAnimations();
        testComponent = new JPanel();
        // Intentionally not initializing mockPresentation
    }
    
    @Test
    public void testGetInstance() {
        AnimationManager instance1 = AnimationManager.getInstance();
        AnimationManager instance2 = AnimationManager.getInstance();
        
        // Test singleton pattern
        assertSame(instance1, instance2);
    }
    
    @Test
    public void testSetAndGetDefaultAnimation() {
        Animation customAnimation = new Animation(800, "slide");
        animationManager.setDefaultAnimation(customAnimation);
        
        Animation retrievedAnimation = animationManager.getDefaultAnimation();
        assertSame(customAnimation, retrievedAnimation);
        assertEquals(800, retrievedAnimation.getDuration());
        assertEquals("slide", retrievedAnimation.getType());
    }
    
    @Test
    public void testSetAndGetAnimationForSlide() {
        Animation customAnimation = new Animation(1200, "random");
        animationManager.setAnimationForSlide(3, customAnimation);
        
        Animation retrievedAnimation = animationManager.getAnimationForSlide(null, 3);
        assertSame(customAnimation, retrievedAnimation);
    }
    
    // Intentionally missing test for animateSlideTransition method
    
    @Test
    public void testClearAnimations() {
        Animation customAnimation = new Animation(1200, "random");
        animationManager.setAnimationForSlide(3, customAnimation);
        
        animationManager.clearAnimations();
        
        // This will use the default animation since the cache was cleared
        Animation retrievedAnimation = animationManager.getAnimationForSlide(null, 3);
        assertEquals("fade", retrievedAnimation.getType());
    }
}
