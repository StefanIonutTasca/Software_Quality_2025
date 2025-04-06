package org.jabberpoint.core;

import org.jabberpoint.model.Slide;
import org.jabberpoint.model.Presentation;

public interface PresentationObserver {
    void update(Presentation presentation, Slide slide);
}


