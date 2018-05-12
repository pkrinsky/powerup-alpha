package powerup.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {
	private GraphicsController controller = null;

	
	public KeyHandler(GraphicsController controller) {
		super();
		this.controller = controller;
	}

	public void keyPressed(KeyEvent e) {
		controller.keyEvent(e);
		
	}
}
