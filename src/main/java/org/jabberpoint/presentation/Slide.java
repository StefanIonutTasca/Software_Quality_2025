package org.jabberpoint.presentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Slide class represents a single slide in a presentation.
 */
public class Slide {
    private String title;
    private List<String> textItems;
    
    public Slide(String title) {
        this.title = title;
        this.textItems = new ArrayList<>();
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
}
