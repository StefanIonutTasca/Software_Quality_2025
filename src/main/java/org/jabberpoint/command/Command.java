package org.jabberpoint.command;

import org.jabberpoint.core.JabberPoint;

/**
 * Command pattern interface for actions that can be performed on a presentation
 */
public interface Command {
    /**
     * Executes the command
     */
    void execute();
}


