package org.jabberpoint;

import org.jabberpoint.presentation.Presentation;
import org.jabberpoint.presentation.Slide;

/**
 * Main class that starts the Jabberpoint application.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Jabberpoint presentation tool");
        
        // Create a sample presentation
        Presentation presentation = new Presentation("Sample Presentation");
        
        // Create some slides
        Slide slide1 = new Slide("Introduction");
        slide1.addTextItem("Welcome to Jabberpoint");
        slide1.addTextItem("A Java-based presentation tool");
        
        Slide slide2 = new Slide("Features");
        slide2.addTextItem("Simple, clean interface");
        slide2.addTextItem("Load and save presentations");
        slide2.addTextItem("Navigate between slides");
        
        // Add slides to the presentation
        presentation.addSlide(slide1);
        presentation.addSlide(slide2);
        
        // Display current slide information
        System.out.println("Presentation: " + presentation.getTitle());
        System.out.println("Number of slides: " + presentation.getSlideCount());
        System.out.println("Current slide: " + presentation.getCurrentSlideNumber());
        
        // In a real application, this would launch a GUI
        System.out.println("This is a placeholder. In the complete application, a GUI would be shown.");
    }
}
