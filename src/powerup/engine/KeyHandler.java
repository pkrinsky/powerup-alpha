package powerup.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {
	private RobotController controller = null;

	
	public KeyHandler(RobotController controller) {
		super();
		this.controller = controller;
	}

	public void keyPressed(KeyEvent e) {
		controller.keyEvent(e);
		
	}
}
