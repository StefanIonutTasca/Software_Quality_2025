package org.jabberpoint.command;

import java.util.Objects;
import org.jabberpoint.core.JabberPoint;
import org.jabberpoint.model.Presentation;

/**
 * Command implementation for exiting the application
 */
public class ExitCommand implements Command {
    private Presentation presentation;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     * @throws NullPointerException if presentation is null
     */
    public ExitCommand(Presentation presentation) {
        this.presentation = Objects.requireNonNull(presentation, "Presentation cannot be null");
    }
    
    /**
     * Executes the command to exit the application
     */
    @Override
    public void execute() {
        presentation.exit(0);
    }
}



