package org.jabberpoint.src.command;
import org.jabberpoint.src.app.JabberPoint;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.io.PresentationLoader;
import org.jabberpoint.src.io.PresentationLoaderFactory;
import org.jabberpoint.src.command.Command;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

/**
 * Command implementation for opening a file
 */
public class OpenFileCommand implements Command {
    private Presentation presentation;
    private Frame parent;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     * @param parent The parent frame for dialogs
     */
    public OpenFileCommand(Presentation presentation, Frame parent) {
        this.presentation = presentation;
        this.parent = parent;
    }
    
    /**
     * Executes the command to open a file
     * In headless environments (without GUI) or when parent is null,
     * this is a no-op as file chooser dialog cannot be shown
     */
    @Override
    public void execute() {
        // Skip file opening in headless environment or if parent is null
        if (parent == null || GraphicsEnvironment.isHeadless()) {
            return; // Cannot show file chooser in headless environment
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        int returnVal = fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                PresentationLoader loader = PresentationLoaderFactory.createLoader("xml");
                loader.loadPresentation(presentation, fileChooser.getSelectedFile().getPath());
                presentation.setSlideNumber(0);
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(parent, "IO Exception: " + exc.getMessage(),
                        "Jabberpoint Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}




