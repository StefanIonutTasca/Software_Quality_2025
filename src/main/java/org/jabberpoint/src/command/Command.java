package org.jabberpoint.src.command;
import org.jabberpoint.src.model.Presentation;

/**
 * Command pattern interface for actions that can be performed on a presentation
 */
public interface Command {
    /**
     * Executes the command
     */
    void execute();
}




