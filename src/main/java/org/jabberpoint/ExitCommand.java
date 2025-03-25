/**
 * Command implementation for exiting the application
 */
public class ExitCommand implements Command {
    private Presentation presentation;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     */
    public ExitCommand(Presentation presentation) {
        this.presentation = presentation;
    }
    
    /**
     * Executes the command to exit the application
     */
    @Override
    public void execute() {
        presentation.exit(0);
    }
}



