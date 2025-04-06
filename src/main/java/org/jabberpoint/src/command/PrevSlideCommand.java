package org.jabberpoint.src.command;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.model.Slide;
import org.jabberpoint.src.command.Command;

/**
 * Command implementation for moving to the previous slide
 */
public class PrevSlideCommand implements Command {
    private Presentation presentation;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     */
    public PrevSlideCommand(Presentation presentation) {
        this.presentation = presentation;
    }
    
    /**
     * Executes the command to move to the previous slide
     */
    @Override
    public void execute() {
        presentation.prevSlide();
    }
}




