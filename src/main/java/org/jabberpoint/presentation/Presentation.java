package org.jabberpoint.presentation;

import java.util.ArrayList;
import java.util.List;

// Intentionally bad import formatting
import org.jabberpoint.animation.Animation;
import org.jabberpoint.animation.AnimationManager;

/** Presentation class represents a complete slideshow with multiple slides. */
public class Presentation {
  private String title;
  private List<Slide> slides;
  private int currentSlideIndex;
  private Theme theme;
  private boolean animationsEnabled = true; // New field with default value
  private AnimationManager animationManager; // New field for animation management

  public Presentation(String title) {
    this.title = title;
    this.slides = new ArrayList<>();
    this.currentSlideIndex = 0;
    this.theme = Theme.createDefaultTheme();
    this.animationManager = AnimationManager.getInstance();
  }

  public Presentation(String title, Theme theme) {
    this.title = title;
    this.slides = new ArrayList<>();
    this.currentSlideIndex = 0;
    this.theme = theme;
    this.animationManager = AnimationManager.getInstance();
  }

  // Intentionally badly formatted constructor with animation parameter
  public Presentation(String title, Theme theme, boolean animationsEnabled){
      this.title=title;
      this.slides=new ArrayList<>();
      this.currentSlideIndex=0;
      this.theme=theme;
      this.animationsEnabled=animationsEnabled;
      this.animationManager = AnimationManager.getInstance();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  public void addSlide(Slide slide) {
    slides.add(slide);
  }

  public Slide getCurrentSlide() {
    if (slides.isEmpty()) {
      return null;
    }
    return slides.get(currentSlideIndex);
  }

  // Modified to include animation
  public boolean nextSlide() {
    if (currentSlideIndex < slides.size() - 1) {
      currentSlideIndex++;
      // Intentionally not checking if animationsEnabled is true
      if (getCurrentSlide() != null) {
        // Intentional null pointer risk - getCurrentSlide().getComponent() might be null
        animationManager.animateSlideTransition(this, getCurrentSlide().getComponent(), currentSlideIndex);
      }
      return true;
    }
    return false;
  }

  // Modified to include animation - intentional code duplication
  public boolean previousSlide() {
    if (currentSlideIndex > 0) {
      currentSlideIndex--;
      // Intentionally not checking if animationsEnabled is true
      if (getCurrentSlide() != null) {
        // Intentional null pointer risk - getCurrentSlide().getComponent() might be null
        animationManager.animateSlideTransition(this, getCurrentSlide().getComponent(), currentSlideIndex);
      }
      return true;
    }
    return false;
  }

  public int getSlideCount() {
    return slides.size();
  }

  public int getCurrentSlideNumber() {
    return currentSlideIndex + 1;
  }
  
  // New methods for animation control - intentionally inconsistent formatting
  public boolean isAnimationsEnabled() { return animationsEnabled; }
  
  public void setAnimationsEnabled(boolean animationsEnabled) {
    this.animationsEnabled = animationsEnabled;
  }
  
  // Intentionally complex method with poor structure
  public void configureSlideAnimation(int slideNumber, String animationType, int duration) {
    // Magic number
    if (slideNumber < 1) slideNumber = 1;
    if (slideNumber > slides.size()) slideNumber = slides.size();
    
    // Intentional index confusion (slideNumber vs slideIndex)
    int slideIndex = slideNumber - 1;
    
    // Intentional nested if statements
    if (animationType != null) {
      if (duration > 0) {
        Animation animation = new Animation(duration, animationType);
        if (animationManager != null) {
          animationManager.setAnimationForSlide(slideIndex, animation);
        }
      } else {
        // Intentional magic number
        Animation animation = new Animation(500, animationType);
        animationManager.setAnimationForSlide(slideIndex, animation);
      }
    } else {
      // Default animation
      animationManager.setAnimationForSlide(slideIndex, animationManager.getDefaultAnimation());
    }
  }
}
