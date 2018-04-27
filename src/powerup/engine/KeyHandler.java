package powerup.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import powerup.Main;

public class KeyHandler extends KeyAdapter {
	private Main main = null;

	
	public KeyHandler(Main main) {
		super();
		this.main = main;
	}

	public void keyPressed(KeyEvent e) {
		main.keyEvent(e);
		
	}
}
