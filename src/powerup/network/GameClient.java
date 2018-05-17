package powerup.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import powerup.engine.GraphicsController;
import powerup.engine.Util;
import powerup.field.Field;
import powerup.field.Robot;

public class GameClient {
	public static final String DELIM="|";
	public static final String ROW_DELIM="~";
	public static final int DELAY = 500;
	
	private GraphicsController controller = null;
	private String serverAddress = null;
	private int serverPort = 0;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private GameServer server = null;
	private String name = "000";
	
	public static void main(String[] args) {
		GameClient client = new GameClient();
		if (args.length > 2) {
			client.setup(args[0],args[1], args[2]);
		} else {
			client.setup(null, null, null);
		}
		client.gameLoop();
	}
	
	public void setup(String serverAddress, String serverPort, String name) {
		if (name != null) this.name = name;
		if (serverAddress == null) {
			Util.log("GameClient.setup local server");
			server = new GameServer();
			//server.addClient(this);
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
			} catch (ConnectException e) {
				Util.log("GameClient.setup could not connect to server");
				Util.log("GameClient.setup exiting");
				System.exit(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}	
	
	public String executeCommand(String name, String request) {
		String returnString = null;
		Util.log("GameClient.executeCommand robot:"+name+" sent:"+request);
		if (server == null) {
			try {
				out.println(request);
				returnString = in.readLine();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (ConnectException e) {
				Util.log("GameClient.setup could not connect to server");
				Util.log("GameClient.setup exiting");
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		} else {
			returnString = server.execute(name, request);
		}
		if (returnString != null)
			Util.log("GameClient.executeCommand received:"+returnString.replace(ROW_DELIM.charAt(0), '\n'));
		return returnString;
	}

	
	private void getFieldData(String name, Field field) {
		String s = null;
		//Util.log("GameClient.getFieldData requesting from server");
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_GET_FIELD);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);	
		s = executeCommand(name,sb.toString());
		//Util.log("GameClient.getFieldData returned "+s);
				
		if (s!= null) field.load(s);
	}
	
	private void sendCommand(String name, int command) {
		Util.log("GameClient.sendCommand "+command);
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_MOVE);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		sb.append(command);
		sb.append(DELIM);
		executeCommand(name,sb.toString());
	}	
	
	private void sendRegister(String name, int position) {
		Util.log("GameClient.sendRegister position:"+position);
		
		StringBuffer sb = new StringBuffer();
		sb.append(GameServer.COMMAND_REGISTER);
		sb.append(DELIM);
		sb.append(name);
		sb.append(DELIM);
		sb.append(position);
		sb.append(DELIM);
		executeCommand(name,sb.toString());
	}		
	
	
	private void gameLoop() {
		//Util.log("GameClient.gameLoop");
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
				int command = controller.getMove(field);
				
				// send the move to the server
				if (command != Robot.STOP) {
					Util.log("GameClient.gameLoop command:"+Robot.getCommandName(command));
					if (Robot.PLAYER_1 == command) {
						sendRegister(name,1);
					} else if (Robot.PLAYER_2 == command) {
						sendRegister(name,2);
					} else {
						sendCommand(name,command);
					}
				}
			}

			// wait for a little then start again
			try { Thread.sleep(DELAY); } catch (Exception e) {}
		}
	}
	

	

	public GraphicsController getController() {
		return controller;
	}

	public void setController(GraphicsController controller) {
		this.controller = controller;
	}
	
	
}
