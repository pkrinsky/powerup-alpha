package powerup.network;

import powerup.engine.GraphicsController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;

public class GameClient {
	
	private GraphicsController controller = null;
	//private String serverAddress = "localhost";
	//private int serverPort = 9001;
	private GameServer server = null;
	
	public static void main(String[] args) {
		GameClient client = new GameClient();
		client.setup();
		client.gameLoop();
		//client.send(-1);
	}
	
	public void setup() {
		Util.log("GameClient.setup");
		server = new GameServer();
		server.addClient(this);
		server.startGame();
		gameLoop();
		
	}
	
	private void updateField(String name, Field field) {
		String s = server.getFieldAsString(name);
		field.load(s);
		//field = server.getField(name);
	}
	
	private void move(String name, int command) {
		server.move(name,command);
	}	
	
	private void gameLoop() {
		//Util.log("GameClient.gameLoop");
		String name = "001";
		boolean gameRunning = true;
		
		Field field = Field.getStaticField();
		updateField(name,field);
		
		controller = new GraphicsController(name);
		controller.setup();
			
		while (gameRunning) {
			//Util.log("GameClient.gameLoop "+field.getGameSecs());
			if (field.getGameSecs() > 0) {
				// get the latest field data from the server
				updateField(name,field);
				
				// see if the robot wants to make a move
				int command = controller.move(field);
				
				if (command != Robot.STOP) {
					Util.log("GameClient.gameLoop command:"+Robot.getCommandName(command));
					// send the move to the server
					move(name,command);
					// update the field data to see what happened
					updateField(name,field);
				}
				controller.drawField(field);
			}

			// wait for a little then start again
			try { Thread.sleep(250); } catch (Exception e) {}
		}
	}
	


	/*
	
	private void send(int command) {
		// if there is a local server use it otherwise send it across the network
		if (server == null) {
			String s = ""+command;
			byte[] buf = s.getBytes();
			
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress address = InetAddress.getByName(serverAddress);
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
				socket.send(packet);
				packet = new DatagramPacket(buf, buf.length, address, serverPort);
				Util.log("GameClient.sending s:"+s);
				socket.send(packet);
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} else {
			server.move(command);
		}
	}
		*/
	
	public void key(char key) {
		controller.key(key);
	}

	public GraphicsController getController() {
		return controller;
	}

	public void setController(GraphicsController controller) {
		this.controller = controller;
	}
	
	
}
