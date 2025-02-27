package org.jabberpoint.presentation;

import java.util.ArrayList;
import java.util.List;

/** Presentation class represents a complete slideshow with multiple slides. */
public class Presentation {
  private String title;
  private List<Slide> slides;
  private int currentSlideIndex;
  private Theme theme;

  public Presentation(String title) {
    this.title = title;
    this.slides = new ArrayList<>();
    this.currentSlideIndex = 0;
    this.theme = Theme.createDefaultTheme();
  }

  public Presentation(String title, Theme theme) {
    this.title = title;
    this.slides = new ArrayList<>();
    this.currentSlideIndex = 0;
    this.theme = theme;
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

  public boolean nextSlide() {
    if (currentSlideIndex < slides.size() - 1) {
      currentSlideIndex++;
      return true;
    }
    return false;
  }

  public boolean previousSlide() {
    if (currentSlideIndex > 0) {
      currentSlideIndex--;
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
}
