package org.jabberpoint.src.control;
import org.jabberpoint.src.command.NextSlideCommand;
import org.jabberpoint.src.command.ExitCommand;
import org.jabberpoint.src.model.Presentation;
import org.jabberpoint.src.command.PrevSlideCommand;
import org.jabberpoint.src.command.Command;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.HashMap;
import java.util.Map;

/** <p>This is the KeyController (KeyListener)</p>
 * <p>Uses the Command pattern to execute actions</p>
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
*/

public class KeyController extends KeyAdapter {
	private Map<Integer, Command> commands = new HashMap<>();

	public KeyController(Presentation p) {
		// Initialize commands
		commands.put(KeyEvent.VK_PAGE_DOWN, new NextSlideCommand(p));
		commands.put(KeyEvent.VK_DOWN, new NextSlideCommand(p));
		commands.put(KeyEvent.VK_ENTER, new NextSlideCommand(p));
		commands.put((int)'+', new NextSlideCommand(p));
		
		commands.put(KeyEvent.VK_PAGE_UP, new PrevSlideCommand(p));
		commands.put(KeyEvent.VK_UP, new PrevSlideCommand(p));
		commands.put((int)'-', new PrevSlideCommand(p));
		
		commands.put((int)'q', new ExitCommand(p));
		commands.put((int)'Q', new ExitCommand(p));
	}

	public void keyPressed(KeyEvent keyEvent) {
		Command command = commands.get(keyEvent.getKeyCode());
		if (command != null) {
			command.execute();
		}
	}
}




