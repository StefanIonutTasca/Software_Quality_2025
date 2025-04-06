package org.jabberpoint.src.command;
import org.jabberpoint.src.app.JabberPoint;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.io.XMLPresentationLoader;
import org.jabberpoint.src.command.Command;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

/**
 * Command implementation for saving a file
 */
public class SaveFileCommand implements Command {
    private Presentation presentation;
    private Frame parent;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     * @param parent The parent frame for dialogs
     */
    public SaveFileCommand(Presentation presentation, Frame parent) {
        this.presentation = presentation;
        this.parent = parent;
    }
    
    /**
     * Executes the command to save a file
     * In headless environments (without GUI) or when parent is null,
     * this is a no-op as file chooser dialog cannot be shown
     */
    @Override
    public void execute() {
        // Skip file saving in headless environment or if parent is null
        if (parent == null || GraphicsEnvironment.isHeadless()) {
            return; // Cannot show file chooser in headless environment
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        int returnVal = fileChooser.showSaveDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                XMLPresentationLoader xmlLoader = new XMLPresentationLoader();
                xmlLoader.savePresentation(presentation, fileChooser.getSelectedFile().getPath());
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(parent, "IO Exception: " + exc.getMessage(),
                        "Jabberpoint Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}




