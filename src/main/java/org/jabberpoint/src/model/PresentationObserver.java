package org.jabberpoint.src.model;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;

public interface PresentationObserver {
    void update(Presentation presentation, Slide slide);
}




