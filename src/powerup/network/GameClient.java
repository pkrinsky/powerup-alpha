package powerup.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import powerup.engine.GraphicsController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;

public class GameClient {
	public static final String DELIM="|";
	public static final String ROW_DELIM="~";
	public static final int DELAY = 200;
	
	private GraphicsController controller = null;
	private String serverAddress = null;
	private int serverPort = 0;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server = null;
	
	public static void main(String[] args) {
		GameClient client = new GameClient();
		client.setup(args[0],args[1]);
		client.gameLoop();
	}
	
	public void setup(String serverAddress, String serverPort) {
		
		if (serverAddress == null) {
			Util.log("GameClient.setup local server");
			server = new GameServer();
			server.addClient(this);
			server.startGame();	
		} else {
			Util.log("GameClient.setup network server "+serverAddress+" "+serverPort);
			this.serverAddress = serverAddress;
			this.serverPort = new Integer(serverPort).intValue();
			try {
				socket = new Socket(this.serverAddress,this.serverPort);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(),true);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private void getFieldData(String name, Field field) {
		String s = null;
		if (server == null) {
			//Util.log("GameClient.getFieldData requesting from server");
			StringBuffer sb = new StringBuffer();
			sb.append(GameServer.COMMAND_GET_FIELD);
			sb.append(DELIM);
			sb.append(name);
			sb.append(DELIM);			
			out.println(sb.toString());
			try {
				//Util.log("GameClient.getFieldData reading response");
				s = in.readLine();
				//Util.log("GameClient.getFieldData got response "+s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			s = server.getFieldAsString(name);
		}
		//Util.log("GameClient.getFieldData returned "+s);
				
		if (s!= null) field.load(s);
	}
	
	private void sendMove(String name, int command) {
		Util.log("GameClient.sendMove "+command);
		
		// if there is a local server use it otherwise send it across the network
		if (server == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(GameServer.COMMAND_MOVE);
			sb.append(DELIM);
			sb.append(name);
			sb.append(DELIM);
			sb.append(command);
			sb.append(DELIM);
			//sb.append(ROW_DELIM);			
			out.println(sb.toString());
		} else {
			server.move(name,command);
		}
	}	
	
	
	
	private void gameLoop() {
		//Util.log("GameClient.gameLoop");
		String name = "001";
		boolean gameRunning = true;
		
		Field field = Field.getStaticField();
		getFieldData(name,field);
		
		controller = new GraphicsController(name);
		controller.setup();
			
		while (gameRunning) {
			//Util.log("GameClient.gameLoop "+field.getGameSecs());
			if (field.getGameSecs() > 0) {
				// get the latest field data from the server
				getFieldData(name,field);
				controller.drawField(field);
				
				// see if the robot wants to make a move
				int command = controller.move(field);
				
				if (command != Robot.STOP) {
					Util.log("GameClient.gameLoop command:"+Robot.getCommandName(command));
					// send the move to the server
					sendMove(name,command);
					
					// update the field data to see what happened
					// getFieldData(name,field);
				}
				
			}

			// wait for a little then start again
			try { Thread.sleep(DELAY); } catch (Exception e) {}
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
