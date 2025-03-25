package org.jabberpoint.src;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Frame;

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
     */
    @Override
    public void execute() {
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
