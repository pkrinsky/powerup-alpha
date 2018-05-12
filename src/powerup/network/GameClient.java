package powerup.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import powerup.engine.GraphicsController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;

public class GameClient {
	public static final String DELIM="|";
	public static final String ROW_DELIM="\n";
	
	private GraphicsController controller = null;
	private String serverAddress = "localhost";
	private int serverPort = 9001;
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
					sendMove(name,command);
					// update the field data to see what happened
					updateField(name,field);
				}
				controller.drawField(field);
			}

			// wait for a little then start again
			try { Thread.sleep(250); } catch (Exception e) {}
		}
	}
	

	private void sendMove(String name, int command) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(DELIM);
		sb.append(command);
		
		// if there is a local server use it otherwise send it across the network
		if (server == null) {
			byte[] buf = sb.toString().getBytes();
			
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress address = InetAddress.getByName(serverAddress);
				DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
				socket.send(packet);
				packet = new DatagramPacket(buf, buf.length, address, serverPort);
				Util.log("GameClient.sending s:"+sb.toString());
				socket.send(packet);
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} else {
			server.move(sb.toString());
		}
	}
	
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
