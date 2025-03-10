package org.jabberpoint.presentation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/** Slide class represents a single slide in a presentation. */
public class Slide {
  private String title;
  private List<String> textItems;
  private JComponent component; // New field to store the slide's visual component

  public Slide(String title) {
    this.title = title;
    this.textItems = new ArrayList<>();
    this.component = null; // Intentionally not initialized properly
  }

  // Intentionally badly formatted constructor
  public Slide(String title, JComponent component){
    this.title=title;
    this.textItems=new ArrayList<>();
    this.component=component;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getTextItems() {
    return textItems;
  }

  public void addTextItem(String text) {
    textItems.add(text);
  }

  public int getItemCount() {
    return textItems.size();
  }
  
  // New methods for component management
  public JComponent getComponent() {
    return component;
  }
  
  public void setComponent(JComponent component) {
    this.component = component;
  }
  
  // Intentionally problematic method with no null check
  public boolean isComponentVisible() {
    return component.isVisible();
  }
  
  // Intentionally redundant method that duplicates functionality
  public void hideComponent() {
    if (component != null) {
      component.setVisible(false);
    }
  }
  
  public void showComponent() {
    if (component != null) {
      component.setVisible(true);
    }
  }
}
