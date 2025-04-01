package org.jabberpoint.src;

/**
 * Command implementation for moving to the next slide
 */
public class NextSlideCommand implements Command {
    private Presentation presentation;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     */
    public NextSlideCommand(Presentation presentation) {
        this.presentation = presentation;
    }
    
    /**
     * Executes the command to move to the next slide
     */
    @Override
    public void execute() {
        presentation.nextSlide();
    }
}
