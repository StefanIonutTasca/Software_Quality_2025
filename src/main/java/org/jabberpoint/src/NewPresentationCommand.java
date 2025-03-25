/**
 * Command implementation for creating a new presentation
 */
public class NewPresentationCommand implements Command {
    private Presentation presentation;
    
    /**
     * Constructor
     * @param presentation The presentation to operate on
     */
    public NewPresentationCommand(Presentation presentation) {
        this.presentation = presentation;
    }
    
    /**
     * Executes the command to create a new presentation
     */
    @Override
    public void execute() {
        presentation.clear();
        presentation.setTitle("New Presentation");
        presentation.setSlideNumber(-1);
    }
}
