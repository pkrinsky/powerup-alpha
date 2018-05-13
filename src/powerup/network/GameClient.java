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
		//client.send(-1);
	}
	
	public void setup(String serverAddress, String serverPort) {
		Util.log("GameClient.setup");
		if (serverAddress == null) {
			server = new GameServer();
			server.addClient(this);
			server.startGame();	
		} else {
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
		
		gameLoop();
		
	}
	
	private void getFieldData(String name, Field field) {
		String s = null;
		if (server == null) {
			out.println(GameServer.COMMAND_GET_FIELD);
			try {
				Util.log("GameClient.getFieldData readLine ...");
				s = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			s = server.getFieldAsString(name);
		}
		Util.log("GameClient.getFieldData returned "+s);
				
		if (s!= null) field.load(s);
	}
	
	private void sendMove(String name, int command) {
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_MOVE);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		sb.append(command);
		sb.append(ROW_DELIM);
		
		// if there is a local server use it otherwise send it across the network
		if (server != null) {
			out.println(sb.toString());
		} else {
			server.move(sb.toString());
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
				
				// see if the robot wants to make a move
				int command = controller.move(field);
				
				if (command != Robot.STOP) {
					Util.log("GameClient.gameLoop command:"+Robot.getCommandName(command));
					// send the move to the server
					sendMove(name,command);
					// update the field data to see what happened
					getFieldData(name,field);
				}
				controller.drawField(field);
			}

			// wait for a little then start again
			try { Thread.sleep(250); } catch (Exception e) {}
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
