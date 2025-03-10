package org.jabberpoint.animation;

import org.jabberpoint.presentation.Presentation;
import org.jabberpoint.presentation.Slide;
import javax.swing.JComponent;
import java.util.HashMap;
import java.util.Map;

/**
 * AnimationManager class manages animations for presentations.
 * This class integrates with the Presentation class to apply animations during slide transitions.
 */
public class AnimationManager {
    // Intentional use of raw type HashMap
    private HashMap animationCache;
    private Animation defaultAnimation;
    
    // Singleton pattern implementation (intentionally not thread-safe)
    private static AnimationManager instance;
    
    private AnimationManager() {
        // Intentionally using raw type
        animationCache = new HashMap();
        defaultAnimation = new Animation(300, "fade");
    }
    
    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }
    
    /**
     * Apply animation to a slide transition.
     * @param presentation The presentation containing the slides
     * @param slideComponent The component representing the slide
     * @param slideNumber The slide number to animate
     */
    public void animateSlideTransition(Presentation presentation, JComponent slideComponent, int slideNumber) {
        // Intentional null check missing
        Animation animation = getAnimationForSlide(presentation, slideNumber);
        
        // Intentional switch without default
        switch (animation.getType()) {
            case "fade":
                animation.startFadeAnimation(slideComponent);
                break;
            case "slide":
                animation.startSlideAnimation(slideComponent);
                break;
            case "random":
                animation.applyRandomEffect(slideComponent);
                break;
        }
    }
    
    /**
     * Get the animation for a specific slide.
     * @param presentation The presentation containing the slide
     * @param slideNumber The slide number
     * @return The animation to apply
     */
    public Animation getAnimationForSlide(Presentation presentation, int slideNumber) {
        // Intentional unchecked cast
        Animation animation = (Animation) animationCache.get(slideNumber);
        if (animation == null) {
            // Return default animation if none is found
            return defaultAnimation;
        }
        return animation;
    }
    
    /**
     * Set animation for a specific slide.
     * @param slideNumber The slide number
     * @param animation The animation to apply
     */
    public void setAnimationForSlide(int slideNumber, Animation animation) {
        // Intentional unchecked call
        animationCache.put(slideNumber, animation);
    }
    
    /**
     * Clear all animations.
     */
    public void clearAnimations() {
        animationCache.clear();
    }
    
    /**
     * Set the default animation.
     * @param animation The animation to set as default
     */
    public void setDefaultAnimation(Animation animation) {
        this.defaultAnimation = animation;
    }
    
    /**
     * Get the default animation.
     * @return The default animation
     */
    public Animation getDefaultAnimation() {
        return defaultAnimation;
    }
}
